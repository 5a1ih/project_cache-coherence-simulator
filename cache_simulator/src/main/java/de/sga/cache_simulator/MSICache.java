package de.sga.cache_simulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MSICache class that simulates cache behavior using the MSI protocol.
 */
public class MSICache extends Cache {
    public MSICache(int numLines, int cacheLineSize, String processorName, Bus bus) {
        super(numLines, cacheLineSize, processorName, bus);
    }

    @Override
    public void executeMemoryTrace(MemoryTrace memoryTrace) {
        String operation = memoryTrace.getOperation();
        String processor = memoryTrace.getProcessorName();
        int address = memoryTrace.getAdress();

        if ("R".equalsIgnoreCase(operation)) {
            myLogger.writeLog(String.format("\n\nRead operation Processor %s Word %d", processor, address));
            read(address);
        } else if ("W".equalsIgnoreCase(operation)) {
            myLogger.writeLog(String.format("\n\nWrite operation Processor %s Word %d", processor, address));
            write(address);
        }
    }

    public void read(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        myLogger.writeLog("Before:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));

        if (line.entryExists(address)) {
            handleReadHit(line);
        } else {
            handleReadMiss(line, address);
        }
        myLogger.writeLog("After:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));
    }

    private void handleReadHit(CacheLine line) {

        if (line.getState() == CacheState.MODIFIED) {
            readHits++;
            modifiedReadHits++;
            myLogger.writeLog("Read hit.");
        }else if(line.getState() == CacheState.SHARED) {
            readHits++;
            sharedReadHits++;
            myLogger.writeLog("Read hit.");
        }
    }

    private void handleReadMiss(CacheLine line, int address) {
        readMiss++;
        myLogger.writeLog("Read miss.");
        line.setState(CacheState.SHARED);
        bus.busRead(processorName, address);
        line.loadWords(address, line.getState());
    }

    public void write(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        myLogger.writeLog("Before:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));

        if (line.entryExists(address)) {
            handleWriteHit(line, address);
        } else {
            handleWriteMiss(line, address);
        }
        myLogger.writeLog("After:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));
    }

    private void handleWriteHit(CacheLine line, int address) {
        if (line.getState() == CacheState.MODIFIED) {
            writeHits++;
            modifiedWriteHits++;
            myLogger.writeLog("Write hit.");
        } else if (line.getState() == CacheState.SHARED) {
            writeHits++;
            sharedWriteHits++;
            line.setState(CacheState.MODIFIED);
            bus.busUpgrade(processorName, address);
            myLogger.writeLog("Write hit, upgraded state.");
        } else {
            handleWriteMiss(line, address);
        }
    }

    private void handleWriteMiss(CacheLine line, int address) {
        writeMiss++;
        myLogger.writeLog("Write miss.");
        line.setState(CacheState.MODIFIED);
        bus.busReadExclusive(processorName, address);
        line.loadWords(address, line.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        int address = (int) evt.getNewValue();

        switch (propertyName) {
            case "busRead" -> handleBusRead(address);
            case "busReadExclusive" -> handleBusReadExclusive(address);
            case "busUpgrade" -> handleBusUpgrade(address);
            default -> {
            }
        }
    }

    private void handleBusRead(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            if (line.getState() == CacheState.MODIFIED) {
                line.setState(CacheState.SHARED);
                bus.busWriteBack(processorName, address);
            }
        }
    }

    private void handleBusReadExclusive(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            myLogger.writeInvalidationLog(address, this.processorName, index, line.getTag(), line.getState().name());
            line.invalidate();
            invalidations++;
        }
    }

    private void handleBusUpgrade(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            myLogger.writeInvalidationLog(address, this.processorName, index, line.getTag(), line.getState().name());
            line.invalidate();
            invalidations++;
        }
    }
}

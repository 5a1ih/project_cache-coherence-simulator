package de.sga.cache_simulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * MESICache class that simulates cache behavior using the MESI protocol.
 */
public class MESICache extends Cache {
    public MESICache(int numLines, int cacheLineSize, String processorName, Bus bus) {
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
        if (line.getState() == CacheState.MODIFIED || line.getState() == CacheState.SHARED || line.getState() == CacheState.EXCLUSIVE) {
            readHits++;
            myLogger.writeLog("Read hit.");
        }
    }

    private void handleReadMiss(CacheLine line, int address) {
        readMiss++;
        myLogger.writeLog("Read miss.");
        if (bus.busHasline(processorName, address)) {
            bus.busRead(processorName, address);
            line.setState(CacheState.SHARED);
        }else {
            line.setState(CacheState.EXCLUSIVE);
        }
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
        if (null == line.getState()) {
            handleWriteMiss(line, address);
        } else switch (line.getState()) {
            case MODIFIED -> {
                writeHits++;
                myLogger.writeLog("Write hit.");
            }
            case SHARED -> {
                writeHits++;
                line.setState(CacheState.MODIFIED);
                bus.busUpgrade(processorName, address);
                myLogger.writeLog("Write hit, upgraded state.");
            }
            case EXCLUSIVE -> {
                writeHits++;
                line.setState(CacheState.MODIFIED);
                myLogger.writeLog("Write hit, upgraded state.");
            }
            default -> handleWriteMiss(line, address);
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
            case "busWriteBack" -> handleBusWriteBack(address);
            default -> {
            }
        }
    }

    private void handleBusRead(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            switch (line.getState()) {
                case MODIFIED -> {
                    line.setState(CacheState.SHARED);
                    bus.busWriteBack(processorName, address);
                }
                case EXCLUSIVE -> {
                    line.setState(CacheState.SHARED);
                    bus.busWriteBack(processorName, address);
                }
                case SHARED -> {
                    bus.busWriteBack(processorName, address);
                }
                default -> throw new AssertionError();
            }
        }
    }

    private void handleBusReadExclusive(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            line.invalidate();
            invalidations++;
            myLogger.writeInvalidationLog(String.format("Ein Lesevorgang von Prozessor %s zu Wort %d suchte nach Tag %d in Cache-Line %d, wurde in diesem Cache im Status Invalid (Cache-Miss) gefunden.",
                this.processorName, address, line.getTag(), index));
        }
    }

    private void handleBusUpgrade(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        if (line.entryExists(address)) {
            invalidations++;
            line.invalidate();
            myLogger.writeInvalidationLog(String.format("Ein Lesevorgang von Prozessor %s zu Wort %d suchte nach Tag %d in Cache-Line %d, wurde in diesem Cache im Status Invalid (Cache-Miss) gefunden.",
                this.processorName, address, line.getTag(), index));
        }
    }

    private void handleBusWriteBack(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];

        if (line.entryExists(address)) {
            line.setState(CacheState.SHARED);
        }
    }

    public boolean hasLine(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        return line.entryExists(address);
    }
}

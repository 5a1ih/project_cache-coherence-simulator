/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 *
 * @author salih
 */
public class Cache implements PropertyChangeListener {
    private int numLines;
    private int lineSize;
    private String processorName;
    private CacheLine[] cacheLines;
    private final Bus bus;
    private ProtocolType protocolType;
    private CacheLogger myLogger = CacheLogger.getLogger();

    private int writeHits;
    private int writeMiss;
    private int readHits;
    private int readMiss;

    public Cache(int numLines, int cacheLineSize, String processorName, Bus bus, ProtocolType protocolType) {
        this.numLines = numLines;
        this.lineSize = cacheLineSize;
        this.processorName = processorName;
        this.cacheLines = new CacheLine[this.numLines];
        this.protocolType = protocolType;
        for (int i = 0; i < cacheLines.length; i++) {
            cacheLines[i] = new CacheLine(lineSize, numLines);
        }
        this.bus = bus;
    }

    public String getStats() {
        return String.format("CPU (%s) Write hits (%d) Write miss (%d) Read hits (%d) Read miss (%d)", 
                this.processorName, writeHits, writeMiss, readHits, readMiss);
    }

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

    public String getProcessorName() {
        return processorName;
    }

    public void read(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];
        myLogger.writeLog("Before:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));

        if (line.entryExists(address)) {
            handleReadHit(line, address);
        } else {
            handleReadMiss(line, address);
        }
        myLogger.writeLog("After:");
        myLogger.writeLog(String.format("CPU: %s, Line state: %s", this.processorName, line.getState().name()));
    }

    private void handleReadHit(CacheLine line, int address) {
        if (line.getState() == CacheState.MODIFIED || line.getState() == CacheState.SHARED ||
            (protocolType == ProtocolType.MESI && line.getState() == CacheState.EXCLUSIVE)) {
            readHits++;
            myLogger.writeLog(String.format("Read hit for word %d.", address));
        } else {
            handleReadMiss(line, address);
        }
    }

    private void handleReadMiss(CacheLine line, int address) {
        readMiss++;
        myLogger.writeLog(String.format("Read miss for word %d.", address));
        if (protocolType == ProtocolType.MESI) {
            line.setState(CacheState.EXCLUSIVE);
            line.loadWords(address, CacheState.EXCLUSIVE);
        } else {
            line.setState(CacheState.SHARED);
            line.loadWords(address, CacheState.SHARED);
        }
        bus.busRead(processorName, address);
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
            myLogger.writeLog(String.format("Write hit for word %d.", address));
        } else if (line.getState() == CacheState.SHARED ||
                   (protocolType == ProtocolType.MESI && line.getState() == CacheState.EXCLUSIVE)) {
            writeHits++;
            line.setState(CacheState.MODIFIED);
            bus.busUpgrade(processorName, address);
            myLogger.writeLog(String.format("Write hit for word %d, upgraded state.", address));
        } else {
            handleWriteMiss(line, address);
        }
    }

    private void handleWriteMiss(CacheLine line, int address) {
        writeMiss++;
        line.loadWords(address, CacheState.MODIFIED);
        bus.busReadExclusive(processorName, address);
        myLogger.writeLog(String.format("Write miss for word %d.", address));
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
            if (protocolType == ProtocolType.MESI) {
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
    }

    private void handleBusReadExclusive(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];

        if (line.entryExists(address)) {
            line.invalidate();
        }
    }

    private void handleBusUpgrade(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];

        if (line.entryExists(address)) {
            line.invalidate();
        }
    }

    private void handleBusWriteBack(int address) {
        int index = (address / lineSize) % numLines;
        CacheLine line = cacheLines[index];

        if (line.entryExists(address)) {
            line.setState(CacheState.SHARED);
        }
    }
}
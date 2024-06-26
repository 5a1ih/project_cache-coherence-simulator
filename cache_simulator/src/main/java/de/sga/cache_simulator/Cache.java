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
public class Cache implements PropertyChangeListener{
    private int numLines;
    private int lineSize;
    private String processorName;
    private CacheLine[] cacheLines;
    final private Bus bus;
    private ProtocolType protocolType;
    private CacheLogger myLogger = CacheLogger.getLogger();
    
    private int writeHits;
    private int writeMiss;
    private int readHits;
    private int readMiss;
    
    public Cache(
            int numLines, 
            int cacheLineSize, 
            String processorName, 
            Bus bus,
            ProtocolType protocolType
    ) {
        this.numLines = numLines;
        this.lineSize = cacheLineSize;
        this.processorName = processorName;
        cacheLines = new CacheLine[this.numLines];
        this.protocolType = protocolType;
        for (int i = 0; i < cacheLines.length; i++) {
            cacheLines[i] = new CacheLine(lineSize, numLines);
        }
        this.bus = bus;
    }
    
    public String getStats() {
        return String.format("CPU (%s) Write hits (%s) Write miss (%s) Read hits (%s) Read miss (%s)", this.getProcessorName(),
                String.valueOf(writeHits), String.valueOf(writeMiss), String.valueOf(readHits), String.valueOf(readMiss));
    }
    
    public void executeMemoryTrace(MemoryTrace memoryTrace) {
        if (memoryTrace.getOperation().equalsIgnoreCase("R")) {
            if (memoryTrace.getAdress() == 100 && memoryTrace.getProcessorName().equalsIgnoreCase("P2")) {
                
            }
            myLogger.writeLog("\n\nLesevorgang Prozessor " + memoryTrace.getProcessorName() + " Wort " + String.valueOf(memoryTrace.getAdress()));
            read(memoryTrace.getAdress());
        } else if (memoryTrace.getOperation().equalsIgnoreCase("W")) {
            myLogger.writeLog("\n\nSchreibvorgang Prozessor " + memoryTrace.getProcessorName() + " Wort " + String.valueOf(memoryTrace.getAdress()));
            write(memoryTrace.getAdress());
        }
    }

    public String getProcessorName() {
        return processorName;
    }

    public void read(int address) {
        int index = (address /lineSize)%numLines;
        CacheLine line = cacheLines[index];
        myLogger.writeLog("Vorher:");
        myLogger.writeLog(String.format("CPU: %s, Linestate: %s", this.getProcessorName(), line.getState().name()));

        if (line.entryExists(address)) {
            if (line.getState() == CacheState.MODIFIED || line.getState() == CacheState.SHARED ||
                    (protocolType == ProtocolType.MESI && line.getState() == CacheState.EXCLUSIVE)) {
                readHits++;
                myLogger.writeLog(String.format("Read hit for word %s.", String.valueOf(address)));
            } else {
                myLogger.writeLog(String.format("Read miss for word %s.", String.valueOf(address)));
                readMiss++;
                if (protocolType == ProtocolType.MESI) {
                    line.setState(CacheState.EXCLUSIVE);
                    line.loadWords(address, CacheState.EXCLUSIVE);
                } else {
                    line.setState(CacheState.SHARED);
                    line.loadWords(address, CacheState.SHARED);
                }
                bus.busRead(processorName, address);
            }
        } else {
            myLogger.writeLog(String.format("Read miss for word %s.", String.valueOf(address)));
            readMiss++;
            if (protocolType == ProtocolType.MESI) {
                line.setState(CacheState.EXCLUSIVE);
                line.loadWords(address, CacheState.EXCLUSIVE);
            } else {
                line.setState(CacheState.SHARED);
                line.loadWords(address, CacheState.SHARED);
            }
            bus.busRead(processorName, address);
        }
        myLogger.writeLog("Nacher:");
        myLogger.writeLog(String.format("CPU: %s, Linestate: %s", this.getProcessorName(), line.getState().name()));
    }
    
    public void write(int address) {
        int index = (address /lineSize)%numLines;
        CacheLine line = cacheLines[index];
        myLogger.writeLog("Vorher:");
        myLogger.writeLog(String.format("CPU: %s, Linestate: %s", this.getProcessorName(), line.getState().name()));
        if (line.entryExists(address)) {
            if (line.getState() == CacheState.MODIFIED) {
                myLogger.writeLog(String.format("Write hit for word %s.", String.valueOf(address)));
                writeHits++;
            } else if (line.getState() == CacheState.SHARED ||
                       (protocolType == ProtocolType.MESI && line.getState() == CacheState.EXCLUSIVE)) {
                myLogger.writeLog(String.format("Write hit for word %s.", String.valueOf(address)));
                writeHits++;
                line.setState(CacheState.MODIFIED);
                bus.busUpgrade(processorName, address);
            } else {
                myLogger.writeLog(String.format("Write miss for word %s.", String.valueOf(address)));
                writeMiss++;
                line.loadWords(address, CacheState.MODIFIED);
                bus.busReadExclusive(processorName, address);
            }
        } else {
            myLogger.writeLog(String.format("Write miss for word %s.", String.valueOf(address)));
            writeMiss++;
            line.loadWords(address, CacheState.MODIFIED);
            bus.busReadExclusive(processorName, address);
        }
        myLogger.writeLog("Nacher:");
        myLogger.writeLog(String.format("CPU: %s, Linestate: %s", this.getProcessorName(), line.getState().name()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        int address = (int) evt.getNewValue();
        
        switch (propertyName) {
            case "busRead" -> {
                handleBusRead(address);
            }
            case "busReadExclusive" -> {
                handleBusReadExclusive(address);
            }
            case "busUpgrade" -> {
                handleBusUpgrade(address);
            }
            case "busWriteBack" -> {
                handleBusWriteBack(address);
            }
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
            } else if (protocolType == ProtocolType.MSI) {
                if (line.getState() == CacheState.MODIFIED) {
                    line.setState(CacheState.SHARED);
                    bus.busWriteBack(processorName, address);
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
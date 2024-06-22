/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import de.sga.cache_simulator.Bus;
import de.sga.cache_simulator.CacheLine;
import de.sga.cache_simulator.CacheState;
import de.sga.cache_simulator.ProtocolType;
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
        for(CacheLine cl : cacheLines) {
            cl = new CacheLine(lineSize, numLines);
        }
        this.bus = bus;
    }
    
    public void executeMemoryTrace(MemoryTrace memoryTrace) {
        if (memoryTrace.getOperation().equalsIgnoreCase("R")) {
            read(memoryTrace.getAdress());
        } else if (memoryTrace.getOperation().equalsIgnoreCase("W")) {
            write(memoryTrace.getAdress());
        }
    }

    public String getProcessorName() {
        return processorName;
    }

    public void read(int address) {
        int index = (address /lineSize)%numLines;
        CacheLine line = cacheLines[index];
        
        if (line.entryExists(address)) {
            if (line.getState() == CacheState.MODIFIED
                    || line.getState() == CacheState.SHARED || (protocolType == ProtocolType.MESI
                    && line.getState() == CacheState.EXCLUSIVE)) {
                readHits++;
            }else {
                readMiss++;
                bus.busRead(processorName, address);
                if (protocolType == ProtocolType.MESI) {
                    line.loadWords(address, CacheState.EXCLUSIVE);
                } else {
                    line.loadWords(address, CacheState.SHARED);
                }
            }
            
        }else {
            readMiss++;
            bus.busRead(processorName, address);
            if (protocolType == ProtocolType.MESI) {
                line.loadWords(address, CacheState.EXCLUSIVE);
            } else {
                line.loadWords(address, CacheState.SHARED);
            }
        }
    }
    
    public void write(int address) {
        int index = (address /lineSize)%numLines;
        CacheLine line = cacheLines[index];
        
        if (line.entryExists(address)) {
            if (line.getState() == CacheState.MODIFIED) {
                writeHits++;
            } else if (line.getState() == CacheState.SHARED ||
                       (protocolType == ProtocolType.MESI && line.getState() == CacheState.EXCLUSIVE)) {
                bus.busUpgrade(processorName, address);
                line.setState(CacheState.MODIFIED);
            } else {
                writeMiss++;
                bus.busReadExclusive(processorName, address);
                line.loadWords(address, CacheState.MODIFIED);
            }
        } else {
            writeMiss++;
            bus.busReadExclusive(processorName, address);
            line.loadWords(address, CacheState.MODIFIED);
        }
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
                if (line.getState() == CacheState.MODIFIED) {
                    if (protocolType == ProtocolType.MESI) {
                        if (line.getState() == CacheState.MODIFIED) {
                            line.setState(CacheState.SHARED);
                            bus.busWriteBack(this.processorName, address);
                        } else if (line.getState() == CacheState.EXCLUSIVE) {
                            line.setState(CacheState.SHARED);
                        }
                    }
                }
            }else if(protocolType == ProtocolType.MSI) {
                if (line.getState() == CacheState.MODIFIED) {
                    line.setState(CacheState.SHARED);
                    bus.busWriteBack(this.processorName, address);
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
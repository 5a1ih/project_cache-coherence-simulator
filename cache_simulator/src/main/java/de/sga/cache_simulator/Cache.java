/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package de.sga.cache_simulator;

import java.beans.PropertyChangeListener;

/**
 *
 * @author salih
 */
public abstract class Cache implements PropertyChangeListener{
    protected int numLines;
    protected int lineSize;
    protected String processorName;
    protected CacheLine[] cacheLines;
    protected final Bus bus;
    protected CacheLogger myLogger = CacheLogger.getLogger();

    protected int writeHits = 0;
    protected int writeMiss = 0;
    protected int readHits = 0;
    protected int readMiss = 0;
    protected int invalidations = 0;
    
    protected int modifiedWriteHits = 0;
    protected int exclusiveWriteHits = 0;
    protected int sharedWriteHits = 0;
    
    protected int modifiedReadHits = 0;
    protected int exclusiveReadHits = 0;
    protected int sharedReadHits = 0;
    
    public Cache(int numLines, int cacheLineSize, String processorName, Bus bus) {
        this.numLines = numLines;
        this.lineSize = cacheLineSize;
        this.processorName = processorName;
        this.cacheLines = new CacheLine[this.numLines];
        for (int i = 0; i < cacheLines.length; i++) {
            cacheLines[i] = new CacheLine(lineSize, numLines);
        }
        this.bus = bus;
    }
   
    public String getStats() {
        return String.format("CPU {%s}\nWrite hits {%d}[MODIFIED {%d} | Exclusive(MESI) {%d} | SHARED {%d}] Write miss {%d}\nRead hits {%d}[MODIFIED {%d} | Exclusive(MESI) {%d} | SHARED {%d}] Read miss {%d} Invalidations {%d}\n",
                this.processorName, writeHits, modifiedWriteHits, exclusiveWriteHits, sharedWriteHits, writeMiss, readHits, modifiedReadHits, exclusiveReadHits, sharedReadHits, readMiss, invalidations);
    }
    public String getProcessorName() {
        return processorName;
    }
    
    public String getContent() {
        for (int i = 0; i<cacheLines.length; i++) {
            if (!(cacheLines[i].isEmpty())) {
                System.out.println(String.format("%s: %s %s %s", processorName, String.valueOf(i), String.valueOf(cacheLines[i].getTag()), cacheLines[i].getState().name())); 
            }
        }
        return null;
    }
    
    public String getInvalidations() {
        return String.valueOf(invalidations);
    }
    
    public abstract void executeMemoryTrace(MemoryTrace memoryTrace);
}

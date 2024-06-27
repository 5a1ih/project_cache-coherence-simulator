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

    protected int writeHits;
    protected int writeMiss;
    protected int readHits;
    protected int readMiss;
    protected int invalidations;
    
    public Cache(int numLines, int cacheLineSize, String processorName, Bus bus) {
        this.numLines = numLines;
        this.lineSize = cacheLineSize;
        this.processorName = processorName;
        this.cacheLines = new CacheLine[this.numLines];
        for (int i = 0; i < cacheLines.length; i++) {
            cacheLines[i] = new CacheLine(lineSize, numLines);
        }
        this.bus = bus;
        this.invalidations = 0;
    }
   
    public String getStats() {
        return String.format("CPU (%s) Write hits (%d) Write miss (%d) Read hits (%d) Read miss (%d) Invalidations (%d)",
                this.processorName, writeHits, writeMiss, readHits, readMiss, invalidations);
    }
    public String getProcessorName() {
        return processorName;
    }
    
    public String getContent() {
        for (int i = 0; i<cacheLines.length; i++) {
            if (!(cacheLines[i].isEmpty())) {
                System.out.println(String.format("%s %s %s", String.valueOf(i), String.valueOf(cacheLines[i].getTag()), cacheLines[i].getState().name())); 
            }
        }
        return null;
    }
    
    public String getInvalidations() {
        return String.valueOf(invalidations);
    }
    
    public abstract void executeMemoryTrace(MemoryTrace memoryTrace);
}

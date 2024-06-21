/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import java.util.ArrayList;

/**
 *
 * @author salih
 */
public class Cache {
    private int numLines;
    private int lineSize;
    private int processorId;
    private CacheLine[] cacheLines;
    
    public Cache(int numLines, int cacheLineSize, int processorId) {
        this.numLines = numLines;
        this.lineSize = cacheLineSize;
        this.processorId = processorId;
        cacheLines = new CacheLine[this.numLines];
    }
    
    public void write(int address) {
        int index = (address /lineSize)%numLines;
        cacheLines[index] = new CacheLine(
                address, 
                this.lineSize, 
                this.numLines
        );
    }
}
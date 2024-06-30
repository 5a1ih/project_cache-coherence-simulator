/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

/**
 *
 * @author salih
 */
public class CacheLine {
    private int tag;
    private int offset;
    private int lineSize;
    private int numLines;
    private int adresses[];
    private CacheState state;
    
    public int getTag() {
        return tag;
    }
    
    public CacheLine(int lineSize, int numLines) {
        this.lineSize = lineSize;
        this.numLines = numLines;
        this.state = CacheState.INVALID;
    }

    public void setState(CacheState state) {
        this.state = state;
    }

    public CacheState getState() {
        return state;
    }
    
    public void loadWords(int address, CacheState state) {
        this.tag = address / (lineSize*numLines);
        this.offset = address % lineSize;
        
        int baseAdress = (address/lineSize)*lineSize;
        
        this.adresses = new int[lineSize];
        for (int i = 0; i < lineSize; i++) {
            this.adresses[i] = baseAdress+i;
        }
        this.state = state;
    }
    
    public boolean entryExists(int adress) {
        return !(state == CacheState.INVALID) && tag == adress / (lineSize * numLines);
    }
    
    public void invalidate() {
        this.state = CacheState.INVALID;
    }
    
    public boolean isEmpty() {
        if (adresses != null && lineSize >0) {
            return false;
        }
        return true;
    }
}

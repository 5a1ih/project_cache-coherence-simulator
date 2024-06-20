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
    private boolean valid;
    private boolean dirty;
    private int adresses[];
    CacheState state;
    
    public CacheLine(int word, int lineSize, int numLines) {
        loadWords(word, lineSize, numLines);
    }
    
    public void loadWords(int address, int lineSize, int numLines) {
        this.tag = address / (lineSize*numLines);
        this.offset = address % lineSize;
        
        int baseAdress = (address/lineSize)*lineSize;
        
        this.adresses = new int[lineSize];
        for (int i = 0; i < lineSize; i++) {
            this.adresses[i] = baseAdress+i;
        }
        this.valid = true;
    }
    
    public void invalidateCacheLine() {}
    public void updateCacheLine() {}
}

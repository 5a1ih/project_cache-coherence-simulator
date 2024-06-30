/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import java.util.ArrayList;

public class CacheFactory {
    private static ArrayList<Cache> caches = new ArrayList<>();
    public static Cache createCache(ProtocolType protocolType, int numLines, int lineSize, String processorName, Bus bus) {
        Cache c;
        switch (protocolType) {
            case MESI -> {
                c = new MESICache(numLines, lineSize, processorName, bus);
                caches.add(c);
                return c;
            }
            case MSI -> {
                c = new MSICache(numLines, lineSize, processorName, bus);
                caches.add(c);
                return c;
            }
            default -> throw new IllegalArgumentException("Unsupported protocol type: " + protocolType);
        }
    }
    
    public static ArrayList<Cache> getCaches() {
        return caches;
    }
}

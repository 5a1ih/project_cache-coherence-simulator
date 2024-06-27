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
public class CacheLogger {
    private static CacheLogger instance;
    private boolean verbose = true;
    private boolean content = false;
    private boolean hitRate = false;
    private boolean invalidationRate = false;
    
    private CacheLogger() {};
    
    public static CacheLogger getLogger() {
        if (null == instance) {
            instance = new CacheLogger();
        }
        return instance;
    }
    
    public void executeOption(String option) {
        if (option.equalsIgnoreCase("verbose") || option.equalsIgnoreCase("v")) {
            verbose = verbose != true;
        }else if (option.equalsIgnoreCase("c")) {
            content = content != true;
            for (Cache cache : CacheFactory.getCaches()) {
                cache.getContent();
            }
        }else if (option.equalsIgnoreCase("h")) {
            hitRate = hitRate != true;
            printStatsByList(CacheFactory.getCaches());
        }else if(option.equalsIgnoreCase("i")) {
            invalidationRate = invalidationRate != true;
            printInvalidationsByList(CacheFactory.getCaches());
        }
    }
    public void writeLog(String message) {
        if(verbose==true)
            System.out.println(message);
    }
    
    public void writeInvalidationLog(String message) {
        if(invalidationRate==true)
            System.out.println(message);
    }
    
    public void printStats(Cache cache) {
        writeLog("Print stats for: " + cache.getStats());
    }
    
    public void printInvalidations(Cache cache) {
        writeLog(String.format("Print invalidations for %s: %s", cache.getProcessorName(), cache.getInvalidations()));
    }
    
    public void printInvalidationsByList(ArrayList<Cache> caches) {
        for (Cache cache : caches) {
            printInvalidations(cache);
        }
    }
    
    public void printStatsByList(ArrayList<Cache> caches) {
        for (Cache cache : caches) {
            printStats(cache);
        }
    }
}

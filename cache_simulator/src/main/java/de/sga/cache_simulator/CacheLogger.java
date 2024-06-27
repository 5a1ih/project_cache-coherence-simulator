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
    private boolean content;
    private boolean hitRate;
    private boolean invalidationRate;
    
    private CacheLogger() {};
    
    public static CacheLogger getLogger() {
        if (null == instance) {
            instance = new CacheLogger();
        }
        return instance;
    }
    
    public void executeOption(String option) {
        if (option.equalsIgnoreCase("verbose") || option.equalsIgnoreCase("v")) {
            if (verbose==false) {
                verbose=true;
            }else if(verbose == true) {
                verbose = false;
            }
        }else if (option.equalsIgnoreCase("c")) {
            for (Cache cache : CacheFactory.getCaches()) {
                cache.getContent();
            }
        }else if (option.equalsIgnoreCase("h")) {
            for (Cache cache : CacheFactory.getCaches()) {
                printStats(cache);
            }
        }
    }
    public void writeLog(String message) {
        if(verbose==true)
            System.out.println(message);
    }
    
    public void printStats(Cache cache) {
        writeLog("Print stats for: " + cache.getStats());
    }
    
    public void printStatsByList(ArrayList<Cache> caches) {
        for (Cache cache : caches) {
            printStats(cache);
        }
    }
}

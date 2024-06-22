/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

/**
 *
 * @author salih
 */
public class CacheLogger {
    private static CacheLogger instance;
    private CacheLogger() {};
    
    public static CacheLogger getLogger() {
        if (null == instance) {
            instance = new CacheLogger();
        }
        return instance;
    }
    
    public void setOption(String option) {}
    public void writeLog(String message) {
        System.out.println(message);
    }
}

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
public interface Cache extends PropertyChangeListener{
    void executeMemoryTrace(MemoryTrace memoryTrace);
    String getStats();
    String getProcessorName();
    String getContent();
}

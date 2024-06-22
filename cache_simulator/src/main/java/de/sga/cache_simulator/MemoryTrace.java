/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

/**
 *
 * @author salih
 */
public class MemoryTrace {
    private String processorName;
    private String operation;
    private int adress;

    public MemoryTrace(String processorName, String operation, int adress) {
        this.processorName = processorName;
        this.operation = operation;
        this.adress = adress;
    }

    public String getProcessorName() {
        return processorName;
    }

    public String getOperation() {
        return operation;
    }

    public int getAdress() {
        return adress;
    }
}

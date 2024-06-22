/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author salih
 */
public class MemoryTraceList {
    String filepath;
    
    public MemoryTraceList(String filepath) {
        this.filepath = filepath;
    }
    
    public ArrayList<MemoryTrace> loadTraceFile() throws IOException{
        BufferedReader reader;
        
        reader = new BufferedReader(new FileReader(this.filepath));
        String line;
        ArrayList<MemoryTrace> traces = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            traces.add(new MemoryTrace(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
        
        return traces;
    }
}

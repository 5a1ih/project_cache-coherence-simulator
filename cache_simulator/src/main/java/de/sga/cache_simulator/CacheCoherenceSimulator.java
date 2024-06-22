/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package de.sga.cache_simulator;
import java.util.List;

/**
 *
 * @author salih
 */
public class CacheCoherenceSimulator {

    public static void main(String[] args) {
        Bus bus = new Bus();
        Cache cacheOne = new Cache(512, 4, "P0", bus, ProtocolType.MESI);
        Cache cacheTwo = new Cache(512, 4, "P1", bus, ProtocolType.MESI);
        Cache cacheThree = new Cache(512, 4, "P2", bus, ProtocolType.MESI);
        Cache cacheFour = new Cache(512, 4, "P3", bus, ProtocolType.MESI);
       
        bus.addListener(cacheOne);
        bus.addListener(cacheTwo);
        bus.addListener(cacheThree);
        bus.addListener(cacheFour);
        
        MemoryTraceList memTrace1 = 
                new MemoryTraceList(
                        "C:\\Users\\salih\\Documents\\NetBeansProjects\\project_cache-coherence-simulator\\trace1.txt"
                );
        try {
            List<MemoryTrace> traces = memTrace1.loadTraceFile();
            for(MemoryTrace trace : traces) {
                switch (trace.getProcessorName()) {
                    case "P0" -> {
                        cacheOne.executeMemoryTrace(trace);
                    }
                    case "P1" -> {
                        cacheTwo.executeMemoryTrace(trace);
                    }
                    case "P2" -> {
                        cacheThree.executeMemoryTrace(trace);
                    }
                    case "P3" -> {
                        cacheFour.executeMemoryTrace(trace);
                    }
                    default -> {}
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

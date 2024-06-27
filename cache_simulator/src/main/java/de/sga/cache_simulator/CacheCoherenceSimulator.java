/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package de.sga.cache_simulator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.*;

/**
 *
 * @author salih
 */
public class CacheCoherenceSimulator {
    CacheLogger myLogger = CacheLogger.getLogger();
    String traceFile = "C:\\Users\\salih\\Documents\\NetBeansProjects\\project_cache-coherence-simulator\\trace5.txt";

    public CacheCoherenceSimulator(String[] args) throws ParseException {
        //setOptions(args);
        myLogger.writeLog("Init bus...");
        Bus bus = new Bus();
        ArrayList<Cache> caches = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Cache cache = CacheFactory.createCache(ProtocolType.MESI, 1024, 2, String.format("P%s", String.valueOf(i)), bus);
            caches.add(cache);
            bus.addListener(cache);
        }
        
        MemoryTraceList memTrace1 = new MemoryTraceList(traceFile);
        
        try {
            List<MemoryTrace> traces = memTrace1.loadTraceFile();
            for(MemoryTrace trace : traces) {
                switch (trace.getProcessorName()) {
                    case "P0" -> {
                        caches.get(0).executeMemoryTrace(trace);
                    }
                    case "P1" -> {
                        caches.get(1).executeMemoryTrace(trace);
                    }
                    case "P2" -> {
                        caches.get(2).executeMemoryTrace(trace);
                    }
                    case "P3" -> {
                        caches.get(3).executeMemoryTrace(trace);
                    }
                    case "v" -> {
                        myLogger.executeOption(trace.getOperation());
                    }
                    case "c" -> {
                        myLogger.executeOption(trace.getOperation());
                    }
                    case "h" -> {
                        myLogger.printStatsByList(caches);
                    }
                    case "i" -> {
                        myLogger.executeOption(trace.getOperation());
                    }
                    default -> {}
                }
            }
            myLogger.printStatsByList(caches);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    void setOptions(String[] args) throws ParseException {
        Options options = new Options();
        Option file = createOption("f", "file", "FILE", "Tracedatei für die Ausführung der Simulation", true);
        Option verbose = createOption("v", "verbose", "VERBOSE", "Zeilenweise Erklärung", false);
        options.addOption(file).addOption(verbose);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        
        traceFile = cmd.getOptionValue(file);
        
        if (cmd.hasOption(verbose)) {
            myLogger.executeOption(verbose.getLongOpt());
        }
    }
    
    Option createOption(String shortName, String longName, String argName, String description, boolean required) {
        return Option.builder(shortName)
                .longOpt(longName)
                .argName(argName)
                .desc(description)
                .hasArg()
                .required(required)
                .build();
    }
}

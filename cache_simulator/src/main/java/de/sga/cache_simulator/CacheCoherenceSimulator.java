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
    String traceFile;
    private boolean verboseFlag = false;
    private boolean contentFlag = false;
    private boolean hitRateFlag = false;
    private boolean invalidationRateFlag = false;
    
    private int numCacheLineSize;
    private int numCacheLines;
    private ProtocolType type;

    public CacheCoherenceSimulator(String[] args) throws ParseException {
        runSimulation(args);
    }

    private void runSimulation(String[] args) throws ParseException {
        setOptions(args);
        myLogger.writeLog("Init bus...");
        Bus bus = new Bus();
        ArrayList<Cache> caches = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Cache cache = CacheFactory.createCache(type, numCacheLines, numCacheLineSize, String.format("P%d", i), bus);
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
            evaluateFlagsAtEnd(caches);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    final void setOptions(String[] args) throws ParseException {
        Options options = new Options();
        Option file = createOption("f", "file", "FILE", "Tracedatei für die Ausführung der Simulation.", true);
        Option cacheLines = createOption("cl", "cache-line", "CACHE-LINE", "Anzahl cache lines.", false);
        Option cacheLineSize = createOption("cls", "cache-line-size", "CACHE-LINE-SIZE", "Größe cache line.", false);
        Option protocolType = createOption("p", "protocol-type", "PROTOCOL-TYPE", "Protokoltyp.", true);
        Option verbose = createFlag("v", "verbose", "Zeilenweise Erklärung.");
        Option content = createFlag("c", "content", "Ausgabe Inhalt der Caches.");
        Option hitRate = createFlag("h", "hit-rate", "Ausgabe Treffer/Misses.");
        Option invalidationRate = createFlag("i", "invalidation-rate", "Ausgabe Invalidierungsstatistiken der Caches");
        options.addOption(file).addOption(verbose).addOption(content).addOption(hitRate).addOption(invalidationRate).addOption(cacheLines).addOption(cacheLineSize).addOption(protocolType);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        
        traceFile = cmd.getOptionValue(file).replace("\\", "\\\\");
        
        if (cmd.hasOption(cacheLines)) {
            numCacheLines = Integer.parseInt(cmd.getOptionValue("cl"));
        } else {
            numCacheLines = 512;
        }
        
        if (cmd.hasOption(cacheLines)) {
            numCacheLineSize = Integer.parseInt(cmd.getOptionValue("cls"));
        }else {
            numCacheLineSize = 4;
        }
        
        if (cmd.hasOption(protocolType)) {
            String opt = cmd.getOptionValue("p");
            if (opt.equals("mesi")) {
                this.type = ProtocolType.MESI;
            }else if (opt.equalsIgnoreCase("msi")) {
                this.type = ProtocolType.MSI;
            } else {
                throw new RuntimeException("Protocol incorrect. Check your input. Supported input [msi | mesi]");
            }
        }
        
        if (cmd.hasOption(verbose)) {
            myLogger.executeOption(verbose.getOpt());
            verboseFlag = true;
        }
        if (cmd.hasOption(content)) {
            contentFlag = true;
        }
        if (cmd.hasOption(hitRate)) {
            hitRateFlag = true;
        }
        if (cmd.hasOption(invalidationRate)) {
            myLogger.executeOption(invalidationRate.getOpt());
            invalidationRateFlag = true;
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
    
    Option createFlag(String shortName, String longName, String description) {
        return Option.builder(shortName)
                .longOpt(longName)
                .desc(description)
                .required(false)
                .hasArg(false)  // Ensure the flag does not take an argument
                .build();
    }
    
    void evaluateFlagsAtEnd(List<Cache> caches) {
        System.out.println("\n\n");
        if (contentFlag == true) {
            myLogger.executeOption("c");
        }
        if (hitRateFlag == true) {
            myLogger.printStatsByList(CacheFactory.getCaches());
        }
        if (invalidationRateFlag == true) {
            myLogger.executeOption("i");
        }
    }
}

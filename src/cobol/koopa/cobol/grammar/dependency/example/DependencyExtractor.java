package koopa.cobol.grammar.dependency.example;

import koopa.cobol.grammar.dependency.CobolDependencyGrammar;
import koopa.core.parsers.Parse;
import koopa.core.parsers.ParserCombinator;
import koopa.core.sources.Source;
import koopa.core.sources.BasicSource;
import koopa.core.data.Token;
import koopa.core.data.Data;
import koopa.core.trees.Tree;
import koopa.core.trees.TreeBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple COBOL Dependency Extractor
 * 
 * This class demonstrates how to use the simplified COBOL dependency grammar
 * to extract only the essential dependency information from COBOL files.
 */
public class DependencyExtractor {
    
    private CobolDependencyGrammar grammar;
    
    public DependencyExtractor() {
        this.grammar = CobolDependencyGrammar.instance();
    }
    
    /**
     * Extract dependencies from a COBOL file
     */
    public DependencyInfo extractDependencies(File cobolFile) throws Exception {
        DependencyInfo info = new DependencyInfo();
        info.fileName = cobolFile.getName();
        
        // Read the file content
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(cobolFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        // Create a source from the content
        Source source = new BasicSource(cobolFile.getName(), content.toString());
        
        // Parse using the dependency grammar
        ParserCombinator parser = grammar.dependencyAnalysis();
        Parse parse = new Parse(source);
        
        if (parser.matches(parse)) {
            System.out.println("Successfully parsed: " + cobolFile.getName());
            // In a real implementation, you would traverse the parse tree
            // to extract specific dependency information
        } else {
            System.err.println("Failed to parse file: " + cobolFile.getName());
        }
        
        return info;
    }
    
    /**
     * Extract dependencies from multiple COBOL files
     */
    public List<DependencyInfo> extractDependencies(List<File> cobolFiles) throws Exception {
        List<DependencyInfo> results = new ArrayList<>();
        
        for (File file : cobolFiles) {
            try {
                DependencyInfo info = extractDependencies(file);
                results.add(info);
            } catch (Exception e) {
                System.err.println("Error processing " + file.getName() + ": " + e.getMessage());
            }
        }
        
        return results;
    }
    
    /**
     * Build a dependency graph from the extracted information
     */
    public DependencyGraph buildDependencyGraph(List<DependencyInfo> dependencies) {
        DependencyGraph graph = new DependencyGraph();
        
        for (DependencyInfo info : dependencies) {
            // Add nodes for each program/file
            graph.addNode(info.fileName);
            
            // Add edges for COPY dependencies
            for (String copyDep : info.copyDependencies) {
                graph.addEdge(info.fileName, copyDep, "COPY");
            }
            
            // Add edges for CALL dependencies
            for (String callDep : info.callDependencies) {
                graph.addEdge(info.fileName, callDep, "CALL");
            }
        }
        
        return graph;
    }
    
    /**
     * Data class to hold dependency information
     */
    public static class DependencyInfo {
        public String fileName;
        public Set<String> copyDependencies = new HashSet<>();
        public Set<String> callDependencies = new HashSet<>();
        public Set<String> programNames = new HashSet<>();
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("File: ").append(fileName).append("\n");
            sb.append("Program Names: ").append(programNames).append("\n");
            sb.append("COPY Dependencies: ").append(copyDependencies).append("\n");
            sb.append("CALL Dependencies: ").append(callDependencies).append("\n");
            return sb.toString();
        }
    }
    
    /**
     * Simple dependency graph representation
     */
    public static class DependencyGraph {
        private Set<String> nodes = new HashSet<>();
        private List<DependencyEdge> edges = new ArrayList<>();
        
        public void addNode(String node) {
            nodes.add(node);
        }
        
        public void addEdge(String from, String to, String type) {
            edges.add(new DependencyEdge(from, to, type));
        }
        
        public Set<String> getNodes() {
            return nodes;
        }
        
        public List<DependencyEdge> getEdges() {
            return edges;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Dependency Graph:\n");
            sb.append("Nodes: ").append(nodes).append("\n");
            sb.append("Edges:\n");
            for (DependencyEdge edge : edges) {
                sb.append("  ").append(edge.from).append(" --[").append(edge.type).append("]--> ").append(edge.to).append("\n");
            }
            return sb.toString();
        }
    }
    
    /**
     * Represents a dependency edge in the graph
     */
    public static class DependencyEdge {
        public final String from;
        public final String to;
        public final String type;
        
        public DependencyEdge(String from, String to, String type) {
            this.from = from;
            this.to = to;
            this.type = type;
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java DependencyExtractor <cobol-file1> [cobol-file2] ...");
            System.out.println("Example: java DependencyExtractor *.CBL");
            return;
        }
        
        try {
            DependencyExtractor extractor = new DependencyExtractor();
            List<File> cobolFiles = new ArrayList<>();
            
            // Add all provided files
            for (String arg : args) {
                File file = new File(arg);
                if (file.exists()) {
                    cobolFiles.add(file);
                } else {
                    System.err.println("File not found: " + file.getAbsolutePath());
                }
            }
            
            if (cobolFiles.isEmpty()) {
                System.err.println("No valid COBOL files provided");
                return;
            }
            
            // Extract dependencies
            List<DependencyInfo> dependencies = extractor.extractDependencies(cobolFiles);
            
            // Print results
            for (DependencyInfo info : dependencies) {
                System.out.println(info);
                System.out.println("---");
            }
            
            // Build and print dependency graph
            DependencyGraph graph = extractor.buildDependencyGraph(dependencies);
            System.out.println(graph);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

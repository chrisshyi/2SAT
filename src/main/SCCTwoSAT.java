package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements a solution to the 2-SAT problem by treating the problem as an implication graph.
 * Kosaraju's algorithm for finding strongly connected components (SCC) is then used to determine if the
 * 2-SAT instance is satisfiable.
 */
public class  SCCTwoSAT {
    /**
     * The finishing time variable used in the first DFS pass of Kosaraju's algorithm
     */
    private int finishingTime;
    /**
     * The leader variable used in the second DFS pass of Kosaraju's algorithm
     */
    private int leaderVariable;

    /**
     * Maps each vertex to its finishing time
     */
    private Map<Integer, Integer> finishingTimeMap;
    /**
     * Maps finishing time to their respective vertices
     */
    private Map<Integer, Integer> reverseFinishingTimeMap;

    /**
     * Maps a leader variable to all the members its corresponding strongly connected component
     */
    private Map<Integer, Set<Integer>> sccMap;

    /**
     * Outer loop for the DFS subroutine in Kosaraju's algorithm for finding SCCs
     * @param graph the graph in question, as an adjacency list
     * @param firstPass true if this is the first DFS pass, false if otherwise
     * @return false if the 2-SAT instance represented by the graph is not satisfiable, true if otherwise
     */
    public boolean outerDFSLoop(Map<Integer, Set<Integer>> graph, boolean firstPass) {
        if (firstPass) {
            finishingTime = 0;
            finishingTimeMap = new HashMap<>();
            reverseFinishingTimeMap = new HashMap<>();
        } else {
            leaderVariable = finishingTimeMap.size();
            sccMap = new HashMap<>();
        }
        Set<Integer> exploredNodes = new HashSet<>();
        for (int i = leaderVariable; i >= 1; i--) {
            if (!exploredNodes.contains(i)) {
                leaderVariable = i;
                if (!innerDFSLoop(graph, i, exploredNodes, firstPass)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean innerDFSLoop(Map<Integer, Set<Integer>> graph, int startVertex
            , Set<Integer> exploredNodes, boolean firstPass) {
        exploredNodes.add(startVertex);
        for (int vertex : graph.get(startVertex)) {
            if (!exploredNodes.contains(vertex)) {
                innerDFSLoop(graph, vertex, exploredNodes, firstPass);
            }
        }
        if (firstPass) {
            this.finishingTime++;
            finishingTimeMap.put(startVertex, this.finishingTime);
            reverseFinishingTimeMap.put(this.finishingTime, startVertex);
        } else {
            if (!sccMap.containsKey(this.leaderVariable)) {
                sccMap.put(this.leaderVariable, new HashSet<>());
            }
            /*
             * Get the original vertex back using the reverse map for finishing times
             */
            int originalVertex = reverseFinishingTimeMap.get(startVertex);
            sccMap.get(this.leaderVariable).add(originalVertex);
            return sccMap.get(this.leaderVariable).contains(-originalVertex);
        }
        return true;
    }

    public Map<Integer, Integer> getFinishingTimeMap() {
        return finishingTimeMap;
    }

    public Map<Integer, Integer> getReverseFinishingTimeMap() {
        return reverseFinishingTimeMap;
    }

    public Map<Integer, Set<Integer>> getSccMap() {
        return sccMap;
    }

    /**
     * Creates the implication graph of a 2-SAT instance
     * @param file file containing the 2-SAT data
     * @return the implication graph as an adjacency list
     */
    public Map<Integer, Set<Integer>> formTwoSATGraph(File file) {
        Map<Integer, Set<Integer>> graph;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            /* Consume the first line, which is just the number of literals in the 2-SAT instance */
            br.readLine();
            graph = new HashMap<>();
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(" ");
                int firstLiteral = Integer.parseInt(splitLine[0]);
                int secondLiteral = Integer.parseInt(splitLine[1]);

                if (!graph.containsKey(-firstLiteral)) {
                    graph.put(-firstLiteral, new HashSet<>());
                }
                graph.get(-firstLiteral).add(secondLiteral);

                if (!graph.containsKey(-secondLiteral)) {
                    graph.put(-secondLiteral, new HashSet<>());
                }
                graph.get(-secondLiteral).add(firstLiteral);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    /**
     * Creates the reverse of a directed graph
     * @param graph the graph as an adjacency list
     * @return the reversed graph as an adjacency list
     */
    public Map<Integer, Set<Integer>> formReverseMap(Map<Integer, Set<Integer>> graph) {
        Map<Integer, Set<Integer>> reverseGraph = new HashMap<>();
        for (int key : graph.keySet()) {
            for (int vertex : graph.get(key)) {
                if (!reverseGraph.containsKey(vertex)) {
                    reverseGraph.put(vertex, new HashSet<>());
                }
                reverseGraph.get(vertex).add(key);
            }
        }
        return reverseGraph;
    }
}

package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implements a solution to the 2-SAT problem by treating the problem as an implication graph.
 * Kosaraju's algorithm for finding strongly connected components (SCC) is then used to determine if the
 * 2-SAT instance is satisfiable.
 */
public class SCCTwoSAT {
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
            sccMap.get(this.leaderVariable).add(startVertex);
            return sccMap.get(this.leaderVariable).contains(-startVertex);
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
}

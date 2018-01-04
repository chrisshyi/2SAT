package tests.scc;

import main.SCCTwoSAT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SCCTwoSATTest {

    SCCTwoSAT sccTwoSAT;

    @BeforeEach
    void setUp() {
        sccTwoSAT = new SCCTwoSAT();
    }

    @Test
    void testGraphFormation() {
        Map<Integer, Set<Integer>> graph =
                sccTwoSAT.formTwoSATGraph(new File(System.getProperty("user.dir") + "/src/tests/scc/test1.txt"));
        assertEquals(graph.get(-1).size(), 1);
        assertTrue(graph.get(-1).contains(2));

        assertEquals(graph.size(), 4);
        assertTrue(graph.get(-3).contains(-4));
        assertTrue(graph.get(4).contains(3));
    }

    @Test
    void testReverseGraphFormation() {
        Map<Integer, Set<Integer>> graph =
                sccTwoSAT.formTwoSATGraph(new File(System.getProperty("user.dir") + "/src/tests/scc/test1.txt"));
        Map<Integer, Set<Integer>> reverseGraph = sccTwoSAT.formReverseGraph(graph);
        assertEquals(reverseGraph.size(), 4);
        assertTrue(reverseGraph.get(2).contains(-1));
        assertTrue(reverseGraph.get(-4).contains(-3));
        assertTrue(reverseGraph.get(3).contains(4));
    }

    @Test
    void testFinishingTimes() {
        Map<Integer, Set<Integer>> graph =
                sccTwoSAT.formTwoSATGraph(new File(System.getProperty("user.dir") + "/src/tests/scc/test2.txt"));
        Map<Integer, Set<Integer>> reverseGraph = sccTwoSAT.formReverseGraph(graph);

        sccTwoSAT.outerDFSLoop(reverseGraph, true);
        Map<Integer, Integer> finishingTimes = sccTwoSAT.getFinishingTimeMap();
        assertEquals(finishingTimes.size(), 8);
        assertEquals((int) finishingTimes.get(-1), 1);
        assertEquals((int) finishingTimes.get(-4), 7);
        assertEquals((int) finishingTimes.get(-3), 6);

        Map<Integer, Integer> reverseFinishingTimes = sccTwoSAT.getReverseFinishingTimeMap();
        assertEquals(reverseFinishingTimes.size(), 8);
        assertEquals((int) reverseFinishingTimes.get(1), -1);
        assertEquals((int) reverseFinishingTimes.get(4), 2);
        assertEquals((int) reverseFinishingTimes.get(7), -4);
    }

    @Test
    void testRenumbering() {
        Map<Integer, Set<Integer>> graph =
                sccTwoSAT.formTwoSATGraph(new File(System.getProperty("user.dir") + "/src/tests/scc/test2.txt"));
        Map<Integer, Set<Integer>> reverseGraph = sccTwoSAT.formReverseGraph(graph);

        sccTwoSAT.outerDFSLoop(reverseGraph, true);
        Map<Integer, Integer> finishingTimes = sccTwoSAT.getFinishingTimeMap();

        Map<Integer, Set<Integer>> renumberedGraph = sccTwoSAT.renumberVertices(graph, finishingTimes);
        assertEquals(renumberedGraph.size(), graph.size());
    }

    @Test
    void testSCC() {
        Map<Integer, Set<Integer>> graph =
                sccTwoSAT.formTwoSATGraph(new File(System.getProperty("user.dir") + "/src/tests/scc/test2.txt"));
        Map<Integer, Set<Integer>> reverseGraph = sccTwoSAT.formReverseGraph(graph);

        sccTwoSAT.outerDFSLoop(reverseGraph, true);
        Map<Integer, Integer> finishingTimes = sccTwoSAT.getFinishingTimeMap();

        Map<Integer, Set<Integer>> renumberedGraph = sccTwoSAT.renumberVertices(graph, finishingTimes);
        sccTwoSAT.outerDFSLoop(renumberedGraph, false);
        Map<Integer, Set<Integer>> sccMap = sccTwoSAT.getSccMap();
        assertEquals(sccMap.size(), 8);
    }
}
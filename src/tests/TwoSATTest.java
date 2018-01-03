package tests;

import main.Clause;
import org.junit.jupiter.api.Test;

import main.TwoSAT;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TwoSATTest {

    @Test
    void testSmallInput() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/test1.txt");
        assertTrue(TwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testSmallInput2() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/test2.txt");
        assertTrue(!TwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testSmallInput3() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/test3.txt");
        assertTrue(TwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testEvaluateClause1() {
        Map<Integer, Boolean> varMap = new HashMap<>();
        varMap.put(1, true);
        varMap.put(2, false);
        Clause clause = new Clause(-1, 2);
        assertFalse(TwoSAT.evaluateClause(varMap, clause));
    }
}
package tests.papa;

import main.Clause;
import org.junit.jupiter.api.Test;

import main.PapaTwoSAT;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PapaTwoSATTest {

    @Test
    void testSmallInput() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/papa/test1.txt");
        assertTrue(PapaTwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testSmallInput2() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/papa/test2.txt");
        assertTrue(!PapaTwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testSmallInput3() {
        File testFile = new File(System.getProperty("user.dir") + "/src/tests/papa/test3.txt");
        assertTrue(PapaTwoSAT.solveTwoSAT(testFile));
    }

    @Test
    void testEvaluateClause1() {
        Map<Integer, Boolean> varMap = new HashMap<>();
        varMap.put(1, true);
        varMap.put(2, false);
        Clause clause = new Clause(-1, 2);
        assertFalse(PapaTwoSAT.evaluateClause(varMap, clause));
    }
}
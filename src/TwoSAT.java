import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A solution to the 2-SAT problem using Papadimitriou's randomized algorithm
 */
public class TwoSAT {

    /**
     * Evaluates a 2-SAT clause using a variable to boolean mapping
     * @param varMap mapping of a variables to their boolean values
     * @param clause the 2-SAT clause in question
     * @return true if the clause is satisfied, false if otherwise
     */
    private static boolean evaluateClause(Map<Integer, Boolean> varMap, Clause clause) {
        int firstVar = clause.firstLiteral;
        boolean varOne = varMap.get(Math.abs(firstVar));
        if (firstVar < 0) {
            varOne = !varOne;
        }

        int secondVar = clause.secondLiteral;
        boolean varTwo = varMap.get(Math.abs(secondVar));
        if (secondVar < 0) {
            varTwo = !varTwo;
        }

        return varOne || varTwo;
    }

    /**
     * Uses Papadimitriou's algorithm to determine whether a 2-SAT instance is satisfiable.
     * Does NOT include the other loop for running log(n) times
     * @param varMap mapping of variables to their boolean values
     * @param clauseMap mapping of clauses to their boolean values
     * @param unsatisfiedClauses list of unsatisfied clauses
     * @return true if the instance can be satisfied, false if otherwise
     */
    private static boolean papaTwoSAT(Map<Integer, Boolean> varMap, Map<Clause, Boolean> clauseMap,
                                      Set<Clause> unsatisfiedClauses) {
        int numVar = varMap.size();
        for (int i = 0; i < 2 * Math.pow(numVar, 2); i++) {
            System.out.printf("%d iteration of the inner loop, " +
                    "%d unsatisfied clauses left\n", i + 1, unsatisfiedClauses.size());
            if (unsatisfiedClauses.size() == 0) {
                return true;
            }
            /*
            ** Pick a random clause
             */
            Iterator<Clause> iterator = unsatisfiedClauses.iterator();
            Clause randClause = iterator.next();

            int randVar = Math.abs(randClause.getRandomLiteral());

            boolean oldVal = varMap.get(randVar);
            varMap.put(randVar, !oldVal);

            boolean newClauseVal = evaluateClause(varMap, randClause);
            if (newClauseVal) {
                unsatisfiedClauses.remove(randClause);
            }
            clauseMap.put(randClause, newClauseVal);
        }
        return false;
    }

    /**
     * Gets a random boolean value with equal probability for true and false
     * @return true or false
     */
    private static boolean getRandomBool() {
        return Math.random() >= 0.5;
    }

    /**
     * The outer loop of Papadimitriou's algorithm
     * @param dataFile the data file to be used
     * @return true if the 2-SAT instance in the data file is satisfiable, false if otherwise
     */
    public static boolean solveTwoSAT(File dataFile) {
        Set<Clause> allClauses = null;
        int numVars = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            numVars = Integer.parseInt(br.readLine());
            allClauses = new HashSet<>(numVars);

            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(" ");
                int literalOne = Integer.parseInt(splitLine[0]);
                int literalTwo = Integer.parseInt(splitLine[1]);

                allClauses.add(new Clause(literalOne, literalTwo));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        double logTwoN = Math.log(numVars) / Math.log(2);
        for (int i = 0; i < logTwoN; i++) {
            System.out.printf("%d iteration of the outer loop\n", i + 1);
            Map<Integer, Boolean> varMap = new HashMap<>();
            Map<Clause, Boolean> clauseMap = new HashMap<>();
            Set<Clause> unsatisfiedClauses = new HashSet<>();
            /*
             * Assign the variables randomly
             */
            for (int var = 1; var <= numVars; var++) {
                boolean boolVal = getRandomBool();
                varMap.put(var, boolVal);
            }
            /*
             * Compute corresponding clause boolean values
             */
            for (Clause clause : allClauses) {
                boolean clauseVal = evaluateClause(varMap, clause);
                clauseMap.put(clause, clauseVal);
                if (!clauseVal) {
                    unsatisfiedClauses.add(clause);
                }
            }
            /*
            ** Run Papadimitriou's algorithm
             */
            if (papaTwoSAT(varMap, clauseMap, unsatisfiedClauses)) {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) {
        String solvableStr = "";
//        System.out.println(System.getProperty("user.dir"));
        for (int i = 1; i <= 6; i++) {
            String projectDir = System.getProperty("user.dir");
            StringBuilder sb = new StringBuilder(projectDir);
            String filePath =
                    sb.append(File.separator).append("data")
                            .append(File.separator).append("2sat").append(i).append(".txt").toString();
            File dataFile = new File(filePath);
            if (solveTwoSAT(dataFile)) {
                solvableStr += "1";
            } else {
                solvableStr += "0";
            }
        }
        System.out.printf("Solvable str: %s\n", solvableStr);
    }
}

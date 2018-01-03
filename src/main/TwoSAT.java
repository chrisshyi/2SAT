package main;

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
    public static boolean evaluateClause(Map<Integer, Boolean> varMap, Clause clause) {
        int firstVar = clause.firstLiteral;
//        System.out.println("first var is: " + firstVar);
        boolean varOne = varMap.get(Math.abs(firstVar));
        if (firstVar < 0) {
            varOne = !varOne;
        }

        int secondVar = clause.secondLiteral;
//        System.out.println("second var is: " + secondVar);
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
                                      Set<Clause> unsatisfiedClauses
            , Map<Integer, Set<Clause>> varToClause) {
        int numVar = varMap.size();
        for (long i = 0; i < 2 * Math.pow(numVar, 2); i++) {
            System.out.printf("%d iteration of the inner loop, " +
                    "%d unsatisfied clauses left\n", i + 1, unsatisfiedClauses.size());
            if (unsatisfiedClauses.size() == 0) {
                return true;
            }
            /*
            ** Pick a random unsatisfied clause
             */
            Iterator<Clause> iterator = unsatisfiedClauses.iterator();
            Clause randUnsatisfiedClause = iterator.next();

            int randVar = randUnsatisfiedClause.getRandomLiteral();

            boolean oldVal = varMap.get(Math.abs(randVar));
            /* Flip the value of that random variable */
            varMap.put(randVar, !oldVal);

            /* Update all the clauses associated with that variable */
            boolean newClauseVal = evaluateClause(varMap, randUnsatisfiedClause);
            if (newClauseVal) {
                unsatisfiedClauses.remove(randUnsatisfiedClause);
            }
            clauseMap.put(randUnsatisfiedClause, newClauseVal);
            /*
             * Re-evaluate all clauses that the randomly chosen variable is a part of
             */
            for (Clause clause : varToClause.get(randVar)) {
                if (clauseMap.containsKey((clause))) {
                    if (!evaluateClause(varMap, clause)) {
                        unsatisfiedClauses.add(clause);
                        clauseMap.put(clause, false);
                    } else {
                        clauseMap.put(clause, true);
                    }
                }
            }
            for (Clause clause : varToClause.get(-randVar)) {
                if (clauseMap.containsKey(clause)) {
                    if (!evaluateClause(varMap, clause)) {
                        unsatisfiedClauses.add(clause);
                        clauseMap.put(clause, false);
                    } else {
                        clauseMap.put(clause, true);
                    }
                }
            }
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
        Map<Integer, Set<Clause>> varToClause;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            numVars = Integer.parseInt(br.readLine());
            allClauses = new HashSet<>(numVars);
            varToClause = new HashMap<>(numVars);

            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(" ");
                int literalOne = Integer.parseInt(splitLine[0]);
                int literalTwo = Integer.parseInt(splitLine[1]);

                Clause newClause = new Clause(literalOne, literalTwo);
                if (!varToClause.containsKey(literalOne)) {
                    varToClause.put(literalOne, new HashSet<>());
                }
                varToClause.get(literalOne).add(newClause);

                if (!varToClause.containsKey(literalTwo)) {
                    varToClause.put(literalTwo, new HashSet<>());
                }
                varToClause.get(literalTwo).add(newClause);

                allClauses.add(newClause);
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
             ** Make a copy of the key set for varToClause for pruning
             */
            Set<Integer> copyOfKeys = new HashSet<>(varToClause.keySet());
            for (Integer key : copyOfKeys) {
                /*
                 * If the key only exists in one form, then we can remove all its associated clauses
                 * from consideration, since those clauses can easily be made true
                 */
                if (!varToClause.containsKey(-key)) {
                    Set<Clause> clauses = varToClause.get(key);
                    allClauses.removeAll(clauses);
                    varToClause.remove(key);
                    varMap.remove(key);
                }
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
            if (papaTwoSAT(varMap, clauseMap, unsatisfiedClauses, varToClause)) {
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

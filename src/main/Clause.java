package main;

import java.util.Objects;

/**
 * Represents a clause in the 2-SAT problem. A disjunction (OR) of two boolean variables.
 */
public class Clause {

    /**
     * The first variable (identified by variable number)
     */
    public final int firstLiteral;
    /**
     * The second variable
     */
    public final int secondLiteral;

    public Clause(int firstLiteral, int secondLiteral) {
        this.firstLiteral = firstLiteral;
        this.secondLiteral = secondLiteral;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return firstLiteral == clause.firstLiteral &&
                secondLiteral == clause.secondLiteral;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstLiteral, secondLiteral);
    }

    /**
     * Gets one of the literals randomly
     * @return one of the literals forming the clause
     */
    public int getRandomLiteral() {
        double x = Math.random();
        if (x >= 0.5) {
            return firstLiteral;
        }
        return secondLiteral;
    }
}

package model;

import java.io.Serializable;
import java.util.Random;

/**
 * Simulates a pair of six-sided dice.
 */
public class Dice implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int REGULATORY_CRISIS_SUM = 7;

    private final Random random = new Random();
    private int lastDie1;
    private int lastDie2;

    /** Rolls both dice and returns the sum (2-12). */
    public int roll() {
        lastDie1 = 1 + random.nextInt(6);
        lastDie2 = 1 + random.nextInt(6);
        return lastDie1 + lastDie2;
    }

    public int getLastDie1() { return lastDie1; }
    public int getLastDie2() { return lastDie2; }
}

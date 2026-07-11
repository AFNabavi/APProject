package model;

import java.io.Serializable;

/**
 * A single tile of the map grid. Holds the sector's specialization
 * ({@link SectorType}) and, unless it is the neutral Regulatory Zone,
 * the dice activation number (2-12, never 7) that triggers production.
 */
public class Sector implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;
    private final SectorType type;
    private final int activationNumber; // 0 for Regulatory Zone

    public Sector(int row, int col, SectorType type, int activationNumber) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.activationNumber = activationNumber;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public SectorType getType() { return type; }
    public int getActivationNumber() { return activationNumber; }

    public boolean isActivatedBy(int diceSum) {
        return !type.isNeutral() && activationNumber == diceSum;
    }

    @Override
    public String toString() {
        if (type.isNeutral()) {
            return type.getLabel();
        }
        return type.getLabel() + " (" + activationNumber + ")";
    }
}

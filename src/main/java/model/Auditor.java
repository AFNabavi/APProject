package model;

import java.io.Serializable;

/**
 * The "Regulatory Auditor" piece. While positioned on a sector, that
 * sector is blocked and produces nothing, even if its number is rolled.
 */
public class Auditor implements Serializable {
    private static final long serialVersionUID = 1L;

    private int sectorRow;
    private int sectorCol;

    public Auditor(int startRow, int startCol) {
        this.sectorRow = startRow;
        this.sectorCol = startCol;
    }

    public int getSectorRow() { return sectorRow; }
    public int getSectorCol() { return sectorCol; }

    public void moveTo(int row, int col) {
        this.sectorRow = row;
        this.sectorCol = col;
    }

    public boolean isOn(int row, int col) {
        return sectorRow == row && sectorCol == col;
    }
}

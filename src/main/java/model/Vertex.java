package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An intersection point where up to four sectors meet. Companies
 * ({@link MVP} / {@link Unicorn}) are built on vertices. Each vertex
 * knows which sectors feed it and which edges leave it, so the game
 * engine can resolve production and connectivity rules.
 */
public class Vertex implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int gridRow; // 0..size-2, intersection coordinate
    private final int gridCol;
    private final List<Sector> adjacentSectors = new ArrayList<>();
    private final List<Integer> adjacentVertexIds = new ArrayList<>();
    private CompanyStructure company; // MVP or Unicorn, null if empty

    public Vertex(int id, int gridRow, int gridCol) {
        this.id = id;
        this.gridRow = gridRow;
        this.gridCol = gridCol;
    }

    public int getId() { return id; }
    public int getGridRow() { return gridRow; }
    public int getGridCol() { return gridCol; }
    public List<Sector> getAdjacentSectors() { return adjacentSectors; }
    public List<Integer> getAdjacentVertexIds() { return adjacentVertexIds; }
    public CompanyStructure getCompany() { return company; }
    public void setCompany(CompanyStructure company) { this.company = company; }
    public boolean isOccupied() { return company != null; }
}

package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * A shared border between two neighbouring vertices, running along the
 * common border of two neighbouring sectors. {@link Partnership}
 * structures are built on edges.
 */
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final int vertexA;
    private final int vertexB;
    private Partnership partnership; // null if empty

    public Edge(int id, int vertexA, int vertexB) {
        this.id = id;
        this.vertexA = vertexA;
        this.vertexB = vertexB;
    }

    public int getId() { return id; }
    public int getVertexA() { return vertexA; }
    public int getVertexB() { return vertexB; }
    public Partnership getPartnership() { return partnership; }
    public void setPartnership(Partnership partnership) { this.partnership = partnership; }
    public boolean isOccupied() { return partnership != null; }

    public boolean connectsTo(int vertexId) {
        return vertexA == vertexId || vertexB == vertexId;
    }

    public int theOtherEnd(int vertexId) {
        if (vertexA == vertexId) return vertexB;
        if (vertexB == vertexId) return vertexA;
        throw new IllegalArgumentException("Vertex " + vertexId + " is not an endpoint of this edge");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return (vertexA == edge.vertexA && vertexB == edge.vertexB)
                || (vertexA == edge.vertexB && vertexB == edge.vertexA);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.min(vertexA, vertexB), Math.max(vertexA, vertexB));
    }
}

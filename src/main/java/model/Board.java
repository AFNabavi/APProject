package model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Sector> sectors = new ArrayList<>();

    private List<Vertex> vertices = new ArrayList<>();

    private List<Edge> edges = new ArrayList<>();

    public List<Sector> getSectors() {
        return sectors;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
package model;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private int id;

    private Building building;

    private List<Edge> edges = new ArrayList<>();

    public Vertex(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
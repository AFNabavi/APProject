package model;

// import model.resource.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class Sector {

    private int id;

    private ResourceType resourceType;

    private int number;

    private List<Vertex> vertices = new ArrayList<>();

    public Sector(int id, ResourceType resourceType, int number) {
        this.id = id;
        this.resourceType = resourceType;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public int getNumber() {
        return number;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
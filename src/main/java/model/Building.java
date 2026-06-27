package model;

public class Building {

    private BuildingType type;
    private Player owner;
    private Vertex location;

    public Building(BuildingType type, Player owner, Vertex location) {
        this.type = type;
        this.owner = owner;
        this.location = location;
    }

    public BuildingType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public Vertex getLocation() {
        return location;
    }
}
package model;

// import model.partnership.Partnership;
// import model.resource.ResourceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import config.ResourceType;

public class Player {

    private String username;

    private int score;

    private Map<ResourceType, Integer> resources =
            new EnumMap<>(ResourceType.class);

    private List<Building> buildings = new ArrayList<>();

    private List<Partnership> partnerships = new ArrayList<>();

    public Player(String username) {
        this.username = username;

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public Map<ResourceType, Integer> getResources() {
        return resources;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Partnership> getPartnerships() {
        return partnerships;
    }
}
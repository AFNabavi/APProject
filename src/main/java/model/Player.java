package com.silicontycoon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents one founder participating in the game. Holds the resource
 * hand, the list of built structures and the (optional) founder role.
 * Victory points are always derived dynamically from the current
 * structures rather than stored as mutable state.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String colorHex;

    private final Map<Resource, Integer> resources = new EnumMap<>(Resource.class);
    private final List<CompanyStructure> structures = new ArrayList<>();
    private FounderRole role;
    private boolean human = true;

    public Player(int id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
        for (Resource r : Resource.values()) {
            resources.put(r, 0);
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getColorHex() { return colorHex; }
    public boolean isHuman() { return human; }
    public void setHuman(boolean human) { this.human = human; }

    public FounderRole getRole() { return role; }
    public void setRole(FounderRole role) { this.role = role; }

    public int getResourceCount(Resource resource) {
        return resources.getOrDefault(resource, 0);
    }

    public int getTotalResourceCount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Resource, Integer> getResources() {
        return resources;
    }

    public void addResource(Resource resource, int amount) {
        resources.merge(resource, amount, Integer::sum);
    }

    /**
     * Removes {@code amount} units of {@code resource}. Throws if the
     * player does not have enough.
     */
    public void removeResource(Resource resource, int amount) {
        int have = resources.getOrDefault(resource, 0);
        if (have < amount) {
            throw new IllegalStateException("Not enough " + resource + " to remove " + amount + " (have " + have + ")");
        }
        resources.put(resource, have - amount);
    }

    public boolean canAfford(Map<Resource, Integer> cost) {
        for (Map.Entry<Resource, Integer> entry : cost.entrySet()) {
            if (getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void pay(Map<Resource, Integer> cost) {
        for (Map.Entry<Resource, Integer> entry : cost.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
    }

    public List<CompanyStructure> getStructures() {
        return structures;
    }

    public void addStructure(CompanyStructure structure) {
        structures.add(structure);
    }

    public long countMvps() {
        return structures.stream().filter(s -> s instanceof MVP).count();
    }

    public long countUnicorns() {
        return structures.stream().filter(s -> s instanceof Unicorn).count();
    }

    public long countPartnerships() {
        return structures.stream().filter(s -> s instanceof Partnership).count();
    }

    /**
     * Card limit that applies to this player when the "7" regulatory
     * crisis taxation rule is resolved.
     */
    public int getCrisisCardLimit() {
        if (role == FounderRole.TECH_GURU) {
            return FounderRole.TECH_GURU_CARD_LIMIT;
        }
        return 7;
    }

    /**
     * Total (dynamic) victory points, including structures, the
     * role-selection penalty and any longest-network bonus supplied
     * by the caller (the controller knows who currently holds it).
     */
    public int getVictoryPoints(boolean holdsLongestNetwork) {
        int points = structures.stream().mapToInt(CompanyStructure::getVictoryPoints).sum();
        if (role != null) {
            points += FounderRole.ROLE_SELECTION_PENALTY;
        }
        if (holdsLongestNetwork) {
            points += 2;
        }
        return points;
    }
}

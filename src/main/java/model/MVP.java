package com.silicontycoon.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * The initial product a founder can build on a {@link Vertex}.
 * Produces 1 unit of resource per activation and is worth 1 point.
 * Can later be upgraded in-place to a {@link Unicorn}.
 */
public class MVP extends CompanyStructure {
    private static final long serialVersionUID = 1L;
    public static final int UNITS_PER_ACTIVATION = 1;
    public static final int VICTORY_POINTS = 1;

    public MVP(Player owner) {
        super(owner);
    }

    @Override
    public void produce(Resource resource) {
        owner.addResource(resource, UNITS_PER_ACTIVATION);
    }

    @Override
    public int getVictoryPoints() {
        return VICTORY_POINTS;
    }

    @Override
    public String getLabel() {
        return "MVP";
    }

    @Override
    public Map<Resource, Integer> getCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.CAPITAL, 1);
        cost.put(Resource.TALENT, 1);
        cost.put(Resource.CLOUD, 1);
        cost.put(Resource.DATA, 1);
        return cost;
    }
}

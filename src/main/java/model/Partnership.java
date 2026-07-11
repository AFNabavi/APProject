package com.silicontycoon.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * A cooperation contract built on an {@link Edge}. Does not produce
 * resources and grants no direct victory points, but extends the
 * player's network and may earn the "Longest Partnership Network"
 * bonus (2 points).
 */
public class Partnership extends CompanyStructure {
    private static final long serialVersionUID = 1L;

    public Partnership(Player owner) {
        super(owner);
    }

    @Override
    public void produce(Resource resource) {
        // Partnerships do not produce resources.
    }

    @Override
    public int getVictoryPoints() {
        return 0; // direct points are 0; longest-network bonus handled separately
    }

    @Override
    public String getLabel() {
        return "Partnership";
    }

    @Override
    public Map<Resource, Integer> getCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.CAPITAL, 1);
        cost.put(Resource.PATENT, 1);
        return cost;
    }
}

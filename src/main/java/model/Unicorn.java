package model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Upgraded form of an {@link MVP}. Built in place (never constructed
 * from scratch); doubles resource production and is worth 2 points
 * instead of the MVP's 1.
 */
public class Unicorn extends CompanyStructure {
    private static final long serialVersionUID = 1L;
    public static final int UNITS_PER_ACTIVATION = 2;
    public static final int VICTORY_POINTS = 2;

    /** Upgrade cost when the founder has NOT taken the Tech Guru role. */
    private static final int STANDARD_CLOUD_COST = 2;
    /** Upgrade cost when the founder holds the Tech Guru role (-1 Cloud). */
    private static final int TECH_GURU_CLOUD_COST = 1;

    public Unicorn(Player owner) {
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
        return "Unicorn";
    }

    @Override
    public Map<Resource, Integer> getCost() {
        return getUpgradeCost(false);
    }

    /**
     * Returns the resource cost required to upgrade an MVP into a Unicorn,
     * taking the Tech Guru founder role discount into account.
     */
    public static Map<Resource, Integer> getUpgradeCost(boolean techGuru) {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.DATA, 3);
        cost.put(Resource.CLOUD, techGuru ? TECH_GURU_CLOUD_COST : STANDARD_CLOUD_COST);
        return cost;
    }
}

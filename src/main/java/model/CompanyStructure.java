package model;

import java.io.Serializable;
import java.util.Map;

/**
 * Abstract base for every buildable structure in the game
 * (MVP, Unicorn, Partnership). Encapsulates ownership, the
 * production behaviour and the victory-point contribution of the
 * structure, following the class hierarchy required by the project
 * specification.
 */
public abstract class CompanyStructure implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final Player owner;

    protected CompanyStructure(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Called when a sector adjacent to this structure is activated by the
     * dice roll. Grants the resource produced by that sector to the owner.
     *
     * @param resource resource type produced by the triggering sector
     */
    public abstract void produce(Resource resource);

    /**
     * @return the number of victory points this structure currently grants.
     */
    public abstract int getVictoryPoints();

    /**
     * @return a short human readable label used by the UI.
     */
    public abstract String getLabel();

    /** Cost table required to construct/upgrade this structure. */
    public abstract Map<Resource, Integer> getCost();
}

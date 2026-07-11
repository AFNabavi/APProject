package model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * The dynamic bank market. Tracks a current price (in Capital) for every
 * resource and adjusts it based on purchase history: buying a resource
 * raises its price, three consecutive rounds without a purchase lowers it.
 */
public class Market implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int BASE_PRICE = 4;
    public static final int MIN_PRICE = 2;
    public static final int MAX_PRICE = 6;
    public static final int ROUNDS_UNTOUCHED_FOR_DROP = 3;
    public static final int STANDARD_TRADE_RATE = 4;
    public static final int HACKER_CEO_TRADE_RATE = 3;

    private final Map<Resource, Integer> prices = new EnumMap<>(Resource.class);
    private final Map<Resource, Integer> roundsSinceLastPurchase = new EnumMap<>(Resource.class);

    public Market() {
        for (Resource r : Resource.values()) {
            prices.put(r, BASE_PRICE);
            roundsSinceLastPurchase.put(r, 0);
        }
    }

    public int getPrice(Resource resource) {
        return prices.get(resource);
    }

    public Map<Resource, Integer> getAllPrices() {
        return prices;
    }

    /** Records a purchase of {@code resource}, raising its price. */
    public void recordPurchase(Resource resource) {
        int newPrice = Math.min(MAX_PRICE, prices.get(resource) + 1);
        prices.put(resource, newPrice);
        roundsSinceLastPurchase.put(resource, 0);
    }

    /**
     * Called once per completed turn for every resource that was NOT
     * purchased this turn; after {@link #ROUNDS_UNTOUCHED_FOR_DROP}
     * consecutive untouched turns the price drops by one unit.
     */
    public void tickUnpurchased(Resource resource) {
        int rounds = roundsSinceLastPurchase.merge(resource, 1, Integer::sum);
        if (rounds >= ROUNDS_UNTOUCHED_FOR_DROP) {
            prices.put(resource, Math.max(MIN_PRICE, prices.get(resource) - 1));
            roundsSinceLastPurchase.put(resource, 0);
        }
    }

    /**
     * Executes a full sell -> buy trade: the player pays
     * {@code getPrice(target)} units of Capital and receives 1 unit of
     * {@code target}. The Hacker CEO role uses a discounted 3:1 rate
     * when converting a specific surplus resource instead of the
     * generic 4:1 bank rate (used by the "sell surplus resource"
     * action in the controller).
     */
    public int getPriceInCapital(Resource target) {
        return prices.get(target);
    }
}

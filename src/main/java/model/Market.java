package model;

// import model.resource.ResourceType;

import java.util.EnumMap;
import java.util.Map;

public class Market {

    private Map<ResourceType, Integer> prices =
            new EnumMap<>(ResourceType.class);

    public Market() {

        for (ResourceType type : ResourceType.values()) {
            prices.put(type, 1);
        }
    }

    public Map<ResourceType, Integer> getPrices() {
        return prices;
    }
}
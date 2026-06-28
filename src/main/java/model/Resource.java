package model;

import config.ResourceType;

public class Resource {

    private ResourceType type;
    private int amount;

    public Resource(ResourceType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
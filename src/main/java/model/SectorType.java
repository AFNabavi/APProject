package com.silicontycoon.model;

/**
 * The type of a map sector. Every sector except {@link #REGULATORY_ZONE}
 * produces exactly one {@link Resource} when its activation number is rolled.
 */
public enum SectorType {
    AI_HUB(Resource.TALENT, "AI Hub"),
    FINTECH_DISTRICT(Resource.CAPITAL, "Fintech District"),
    CLOUD_CAMPUS(Resource.CLOUD, "Cloud Campus"),
    IP_QUARTER(Resource.PATENT, "IP Quarter"),
    DATA_VALLEY(Resource.DATA, "Data Valley"),
    REGULATORY_ZONE(null, "Regulatory Zone");

    private final Resource producedResource;
    private final String label;

    SectorType(Resource producedResource, String label) {
        this.producedResource = producedResource;
        this.label = label;
    }

    public Resource getProducedResource() { return producedResource; }
    public String getLabel() { return label; }
    public boolean isNeutral() { return this == REGULATORY_ZONE; }
}

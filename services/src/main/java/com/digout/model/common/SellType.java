package com.digout.model.common;

public enum SellType {
    DIGOUT("DIGOUT"), F2F("FACE TO FACE");

    private final String description;

    private SellType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}

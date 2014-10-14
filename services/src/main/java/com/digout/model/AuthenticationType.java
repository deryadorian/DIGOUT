package com.digout.model;

public enum AuthenticationType {

    DEFAULT("DEFAULT");// , FB("FB"), TW("TW");

    public static AuthenticationType of(final String typeString) {
        for (AuthenticationType type : values()) {
            if (type.type.equals(typeString)) {
                return type;
            }
        }
        return null;
    }

    private final String type;

    private AuthenticationType(final String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}

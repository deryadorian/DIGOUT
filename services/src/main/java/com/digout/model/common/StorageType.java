package com.digout.model.common;

public enum StorageType {

    FS("File system"), CDN("Content Delivery Network");

    private final String description;

    private StorageType(final String description) {
        this.description = description;
    }
}

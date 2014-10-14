package com.digout.model.common;

public enum ImageFormat {
    ORIGINAL(1160, 1160/* 870 */), STANDARD(580, 580/* 435 */), THUMB(180, 180/* 145 */);

    private final int width;
    private final int height;

    private ImageFormat(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
}

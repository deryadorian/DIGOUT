package com.digout.utils;

import java.util.Locale;

public final class Formats {

    private Formats() {
    }

    public static String formatDouble(final Double price, final int digitsAfterPoint) {
        return String.format(Locale.US, "%." + digitsAfterPoint + "f", price);
    }

    public static String toStringWithMultiply(final double value, final int multiply) {
        return String.format("%.0f", value * multiply);
    }
}

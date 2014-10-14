package com.digout.utils;

import java.util.regex.Pattern;

public final class Matchers {

    public static final boolean matches(final String template, final Pattern pattern) {
        return pattern.matcher(template).matches();
    }

    private Matchers() {
    }
}

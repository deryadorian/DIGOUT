package com.digout.utils;

import com.digout.exception.ApplicationException;

public final class Asserts {

    public static final void notNull(final Object obj, final String message) throws ApplicationException {
        if (obj == null) {
            throw new ApplicationException(message);
        }
    }

    private Asserts() {
    }

}

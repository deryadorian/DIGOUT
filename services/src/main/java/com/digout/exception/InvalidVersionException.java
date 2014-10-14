package com.digout.exception;

import com.digout.support.context.MessageContext;

public class InvalidVersionException extends ApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = -4279711678549851538L;

    public InvalidVersionException(final MessageContext messageContext) {
        super(messageContext);
    }

    public InvalidVersionException(final String message) {
        super(message);
    }

    public InvalidVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

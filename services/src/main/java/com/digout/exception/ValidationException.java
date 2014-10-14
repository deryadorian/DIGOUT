package com.digout.exception;

import com.digout.support.context.MessageContext;

public class ValidationException extends ApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = -2854577020635635402L;

    public ValidationException(final MessageContext messageContext) {
        super(messageContext);
    }

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

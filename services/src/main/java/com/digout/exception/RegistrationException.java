package com.digout.exception;

import com.digout.support.context.MessageContext;

public class RegistrationException extends ApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = 2086361039972308417L;

    public RegistrationException(final MessageContext messageContext) {
        super(messageContext);
    }

    public RegistrationException(final String message) {
        super(message);
    }

    public RegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

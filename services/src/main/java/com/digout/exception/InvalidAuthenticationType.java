package com.digout.exception;

import com.digout.support.context.MessageContext;

public class InvalidAuthenticationType extends ApplicationException {
    private static final long serialVersionUID = 2240398689838825543L;

    public InvalidAuthenticationType(final MessageContext messageContext) {
        super(messageContext);
    }

    public InvalidAuthenticationType(final String message) {
        super(message);
    }

    public InvalidAuthenticationType(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "Invalid authentication type";
    // }

}

package com.digout.exception;

import com.digout.support.context.MessageContext;

public class InvalidCredentialsException extends ApplicationException {
    private static final long serialVersionUID = 7085617764642620888L;

    public InvalidCredentialsException(final MessageContext messageContext) {
        super(messageContext);
    }

    public InvalidCredentialsException(final String message) {
        super(message);
    }

    public InvalidCredentialsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "Invalid username or password";
    // }

}

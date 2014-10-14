package com.digout.exception;

import com.digout.support.context.MessageContext;

public class UserNotFoundException extends ApplicationException {
    private static final long serialVersionUID = -722167105963463702L;

    public UserNotFoundException(final MessageContext messageContext) {
        super(messageContext);
    }

    public UserNotFoundException(final String message) {
        super(message);
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "User is not found";
    // }
}

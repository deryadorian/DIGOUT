package com.digout.exception;

import com.digout.support.context.MessageContext;

public class UserTokenNotExistsException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = 8939725661014982543L;

    public UserTokenNotExistsException(final MessageContext messageContext) {
        super(messageContext);
    }

    public UserTokenNotExistsException(final String message) {
        super(message);
    }

    public UserTokenNotExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "No user token provided";
    // }

}

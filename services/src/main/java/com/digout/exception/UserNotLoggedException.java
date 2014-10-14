package com.digout.exception;

import com.digout.support.context.MessageContext;

public class UserNotLoggedException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = -4497410197945762136L;

    public UserNotLoggedException(final MessageContext messageContext) {
        super(messageContext);
    }

    public UserNotLoggedException(final String message) {
        super(message);
    }

    public UserNotLoggedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "User is not logged in or session is invalid";
    // }

}

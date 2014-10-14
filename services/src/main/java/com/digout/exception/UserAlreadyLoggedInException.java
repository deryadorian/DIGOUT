package com.digout.exception;

import com.digout.support.context.MessageContext;

public class UserAlreadyLoggedInException extends ApplicationException {

    private static final long serialVersionUID = -8181117284381896733L;

    public UserAlreadyLoggedInException(final MessageContext messageContext) {
        super(messageContext);
    }

    public UserAlreadyLoggedInException(final String message) {
        super(message);
    }

    public UserAlreadyLoggedInException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "User is already logged in";
    // }

}

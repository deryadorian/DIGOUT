package com.digout.exception;

import com.digout.support.context.MessageContext;

public class FacebookPermissionException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = -5252394958278433724L;

    public FacebookPermissionException(final MessageContext messageContext) {
        super(messageContext);
    }

    public FacebookPermissionException(final String message) {
        super(message);
    }

    public FacebookPermissionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

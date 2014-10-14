package com.digout.exception;

import com.digout.support.context.MessageContext;

public class PermisionDeniedException extends ApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = -9200567389342402806L;

    public PermisionDeniedException(final MessageContext messageContext) {
        super(messageContext);
    }

    public PermisionDeniedException(final String message) {
        super(message);
    }

    public PermisionDeniedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

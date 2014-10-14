package com.digout.exception;

import com.digout.support.context.MessageContext;

public class ItemNotExistsException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = -2149785622136156338L;

    public ItemNotExistsException(final MessageContext messageContext) {
        super(messageContext);
    }

    public ItemNotExistsException(final String message) {
        super(message);
    }

    public ItemNotExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

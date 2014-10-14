package com.digout.exception;

import com.digout.support.context.MessageContext;

public class RecordAlreadyExistsException extends ApplicationException {
    /**
     * 
     */
    private static final long serialVersionUID = 8948204642950381483L;

    public RecordAlreadyExistsException(final MessageContext messageContext) {
        super(messageContext);
    }

    public RecordAlreadyExistsException(final String message) {
        super(message);
    }

    public RecordAlreadyExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "Record already exists";
    // }
}

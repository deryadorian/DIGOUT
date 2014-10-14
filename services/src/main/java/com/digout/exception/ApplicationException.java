package com.digout.exception;

import com.digout.support.context.BasicMessageContext;
import com.digout.support.context.MessageContext;

public class ApplicationException extends Exception {
    private static final long serialVersionUID = -1242087736201907602L;

    private final MessageContext messageContext;

    public ApplicationException(final MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    public ApplicationException(final String message) {
        super(message);
        this.messageContext = new BasicMessageContext(message);
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
        this.messageContext = new BasicMessageContext(message);
    }

    public MessageContext getMessageContext() {
        return this.messageContext;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public boolean withMessageContext() {
        return this.messageContext != null;
    }
}

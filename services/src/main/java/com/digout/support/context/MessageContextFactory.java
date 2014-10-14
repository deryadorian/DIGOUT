package com.digout.support.context;

public final class MessageContextFactory {

    public static final MessageContextFactory create(final MessageContext messageContext) {
        return new MessageContextFactory(messageContext);
    }

    public static MessageContext newContext() {
        return new MessageContextFactory(new BasicMessageContext()).getContext();
    }

    private final MessageContext messageContext;

    private MessageContextFactory(final MessageContext context) {
        this.messageContext = context;
    }

    public MessageContext getContext() {
        return this.messageContext;
    }

}

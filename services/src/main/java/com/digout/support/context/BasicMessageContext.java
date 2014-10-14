package com.digout.support.context;

import java.util.ArrayList;
import java.util.List;

public class BasicMessageContext implements MessageContext {

    private final List<String> messages;

    public BasicMessageContext() {
        this.messages = new ArrayList<String>();
    }

    public BasicMessageContext(final List<String> messages) {
        this.messages = new ArrayList<String>();
        this.messages.addAll(messages);
    }

    public BasicMessageContext(final String message) {
        this.messages = new ArrayList<String>();
        this.messages.add(message);
    }

    @Override
    public void addMessage(final String message) {
        this.messages.add(message);
    }

    @Override
    public void addMessages(final List<String> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public List<String> getMessages() {
        return this.messages;
    }

    @Override
    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    @Override
    public void merge(final MessageContext messageContext) {
        if (messageContext != null && !messageContext.isEmpty()) {
            this.messages.addAll(messageContext.getMessages());
        }
    }
}

package com.digout.support.context;

import java.util.List;

public interface MessageContext {

    void addMessage(String message);

    void addMessages(List<String> messages);

    List<String> getMessages();

    boolean isEmpty();

    void merge(MessageContext messageContext);
}

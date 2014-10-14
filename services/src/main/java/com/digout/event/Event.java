package com.digout.event;

import org.springframework.context.ApplicationEvent;

public abstract class Event<T> extends ApplicationEvent {
    private static final long serialVersionUID = 3409088987663686514L;

    private final T value;

    public Event(final T source) {
        super(source);
        this.value = source;
    }

    @Override
    public T getSource() {
        return this.value;
    }
}

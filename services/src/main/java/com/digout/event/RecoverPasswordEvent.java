package com.digout.event;

import com.digout.event.source.RecoverPasswordEventSource;

public class RecoverPasswordEvent extends Event<RecoverPasswordEventSource> {
    private static final long serialVersionUID = -2531416822644297101L;

    public RecoverPasswordEvent(final RecoverPasswordEventSource source) {
        super(source);
    }
}

package com.digout.event;

import com.digout.event.source.WelcomeEmailEventSource;

public class WelcomeEmailEvent extends Event<WelcomeEmailEventSource> {
    private static final long serialVersionUID = -6190868781285503929L;

    public WelcomeEmailEvent(final WelcomeEmailEventSource source) {
        super(source);
    }
}

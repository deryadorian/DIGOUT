package com.digout.event;

import com.digout.event.source.InappropriateProductEmailSource;

public final class InappropriateProductEmailEvent extends Event<InappropriateProductEmailSource>{
    private static final long serialVersionUID = 8850735687165047274L;

    public InappropriateProductEmailEvent(final InappropriateProductEmailSource source) {
        super(source);
    }

    
}

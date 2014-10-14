package com.digout.event;

import com.digout.event.source.ApprovalEmailEventSource;

public class ApprovalEmailEvent extends Event<ApprovalEmailEventSource> {

    private static final long serialVersionUID = 3385004509124680561L;

    public ApprovalEmailEvent(final ApprovalEmailEventSource source) {
        super(source);
    }
}

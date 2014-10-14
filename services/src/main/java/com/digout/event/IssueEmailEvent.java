package com.digout.event;

import com.digout.event.source.IssueEmailSource;

public class IssueEmailEvent extends Event<IssueEmailSource> {
    private static final long serialVersionUID = -724036062944010561L;

    public IssueEmailEvent(final IssueEmailSource source) {
        super(source);
    }
}

package com.digout.converter;

import com.digout.artifact.Issue;
import com.digout.model.entity.user.IssueEntity;

public class IssueConverter extends SimpleConverterFactory<Issue, IssueEntity> {
    @Override
    protected IssueEntity initEntity(final Issue issue) {
        return null;
    }

    @Override
    protected Issue initTO(final IssueEntity entity) {
        return null;
    }
}

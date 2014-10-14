package com.digout.model.common;

public enum IssueType {
    DAMAGED(0), NOT_MY_PRODUCT(1), OTHER(2);

    public static IssueType getIssueType(final int order) {
        IssueType res = null;
        for (IssueType type : IssueType.values()) {
            if (type.getOrder() == order) {
                res = type;
            }
        }
        return res;
    }

    private final int order;

    private IssueType(final int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

}

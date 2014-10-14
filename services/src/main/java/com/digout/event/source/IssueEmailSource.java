package com.digout.event.source;

import lombok.Value;
import lombok.experimental.Builder;

@Value
@Builder
public class IssueEmailSource {
    private String email;
    private String productName;
    private String username;
    private String issueType;
    private String description;
}

package com.digout.event.source;

import lombok.Value;
import lombok.experimental.Builder;

import org.joda.time.DateTime;

@Value
@Builder
public class ApprovalEmailEventSource {
    private String buyerName;
    private String buyerEmail;
    private String buyerMobile;

    private String sellerName;
    private String sellerEmail;
    private String sellerMobile;
    private String sellerIban;
    
    private String productName;
    private Double price;
    private Double commision;
    private DateTime approvalTime;
}

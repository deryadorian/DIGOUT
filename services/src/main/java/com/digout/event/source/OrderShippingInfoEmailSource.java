package com.digout.event.source;

import org.joda.time.DateTime;

import lombok.Value;
import lombok.experimental.Builder;

@Value
@Builder
public class OrderShippingInfoEmailSource {
    private String productId;
    private String productName;
    private Double price;
    private String shippedUserName;
    private DateTime shipped;
    private String carrierName;
    private String trackingCode;
    private String carrierWebsite;
    private String buyerEmail;
}

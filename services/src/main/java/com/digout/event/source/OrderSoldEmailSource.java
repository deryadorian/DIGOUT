package com.digout.event.source;

import lombok.Value;
import lombok.experimental.Builder;

import org.joda.time.DateTime;

import com.digout.support.money.CurrencyUnit;

@Value
@Builder
public class OrderSoldEmailSource {
    private String buyerName;
    private String buyerEmail;
    private String buyerMobile;
    
    private String sellerEmail;
    private String sellerName;
    private String sellerMobile;
    
    private String productName;
    private Double price;
    
    private String uniqueOrderId;
    private DateTime orderTime;
    private CurrencyUnit currency;
}

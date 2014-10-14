package com.digout.event;

import com.digout.event.source.OrderSoldEmailSource;

public class OrderSoldEmailEvent extends Event<OrderSoldEmailSource> {

    private static final long serialVersionUID = 4120378732387414414L;

    public OrderSoldEmailEvent(final OrderSoldEmailSource source) {
        super(source);
    }
}

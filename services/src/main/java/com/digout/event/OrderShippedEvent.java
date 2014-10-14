package com.digout.event;

import com.digout.event.source.OrderShippingInfoEmailSource;

public class OrderShippedEvent extends Event<OrderShippingInfoEmailSource>{
    private static final long serialVersionUID = 4079233428750496106L;

    public OrderShippedEvent(final OrderShippingInfoEmailSource source) {
        super(source);
    }

}

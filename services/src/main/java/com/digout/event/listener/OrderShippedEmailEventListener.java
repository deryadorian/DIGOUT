package com.digout.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.digout.event.OrderShippedEvent;
import com.digout.support.mail.BuySellProcessMailer;

public class OrderShippedEmailEventListener implements ApplicationListener<OrderShippedEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    @Override
    public void onApplicationEvent(final OrderShippedEvent event) {
        buySellProcessMailer.sendOrderShippedEmail(event.getSource());
    }

}

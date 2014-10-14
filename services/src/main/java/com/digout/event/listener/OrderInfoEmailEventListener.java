package com.digout.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.digout.event.OrderSoldEmailEvent;
import com.digout.support.mail.BuySellProcessMailer;

public class OrderInfoEmailEventListener implements ApplicationListener<OrderSoldEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    @Override
    public void onApplicationEvent(final OrderSoldEmailEvent orderInfoEmailEvent) {
        final BuySellProcessMailer mailer = buySellProcessMailer;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendItemSoldEmail(orderInfoEmailEvent.getSource());
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendItemSoldEmailSystem(orderInfoEmailEvent.getSource());
            }
        }).start();
    }
}

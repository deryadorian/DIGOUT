package com.digout.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;

import com.digout.event.InappropriateProductEmailEvent;
import com.digout.event.source.InappropriateProductEmailSource;
import com.digout.support.mail.BuySellProcessMailer;

public class InappropriateProductEmailEventListener implements ApplicationListener<InappropriateProductEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;
    
    @Value("${mail.notifications.system}")
    private String email;

    @Override
    public void onApplicationEvent(final InappropriateProductEmailEvent event) {
        final InappropriateProductEmailSource source = event.getSource();
        buySellProcessMailer.sendInappropriateProductEmail(new String[] { email }, source.getProductId(),
                source.getReporterId(), source.getProductOwnerId());
    }

}

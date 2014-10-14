package com.digout.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.digout.event.InappropriateProductEmailEvent;
import com.digout.event.source.InappropriateProductEmailSource;
import com.digout.support.mail.BuySellProcessMailer;

public class InappropriateProductEmailEventListener implements ApplicationListener<InappropriateProductEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    private static final String EMAIL_TO = "destek@thedigout.com";

    @Override
    public void onApplicationEvent(final InappropriateProductEmailEvent event) {
        final InappropriateProductEmailSource source = event.getSource();
        buySellProcessMailer.sendInappropriateProductEmail(new String[] { EMAIL_TO }, source.getProductId(),
                source.getReporterId(), source.getProductOwnerId());
    }

}

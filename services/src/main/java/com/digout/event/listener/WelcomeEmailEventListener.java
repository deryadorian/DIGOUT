package com.digout.event.listener;

import com.digout.event.WelcomeEmailEvent;
import com.digout.event.source.WelcomeEmailEventSource;
import com.digout.support.mail.BuySellProcessMailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class WelcomeEmailEventListener implements ApplicationListener<WelcomeEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    @Override
    public void onApplicationEvent(final WelcomeEmailEvent welcomeEmailEvent) {
        final WelcomeEmailEventSource source = welcomeEmailEvent.getSource();
        this.buySellProcessMailer.sendWelcomeEmail(new String[] { source.getEmail() }, source.getUsername());
    }
}

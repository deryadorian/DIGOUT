package com.digout.event.listener;

import com.digout.event.IssueEmailEvent;
import com.digout.event.source.IssueEmailSource;
import com.digout.support.mail.BuySellProcessMailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class IssueEmailEventListener implements ApplicationListener<IssueEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    @Override
    public void onApplicationEvent(final IssueEmailEvent issueEmailEvent) {
        final IssueEmailSource source = issueEmailEvent.getSource();

        final BuySellProcessMailer mailer = buySellProcessMailer;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendReportIssueEmail(source);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendReportIssueEmailSystem(source);
            }
        }).start();
    }
}

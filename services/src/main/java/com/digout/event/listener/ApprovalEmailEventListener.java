package com.digout.event.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import com.digout.event.ApprovalEmailEvent;
import com.digout.support.mail.BuySellProcessMailer;

public class ApprovalEmailEventListener implements ApplicationListener<ApprovalEmailEvent> {

    @Autowired
    private BuySellProcessMailer buySellProcessMailer;

    @Override
    public void onApplicationEvent(final ApprovalEmailEvent approvalEmailEvent) {
        final BuySellProcessMailer mailer = buySellProcessMailer;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendApprovalEmailToSeller(approvalEmailEvent.getSource());
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mailer.sendApprovalEmailToSystem(approvalEmailEvent.getSource());
            }
        }).start();
    }
}

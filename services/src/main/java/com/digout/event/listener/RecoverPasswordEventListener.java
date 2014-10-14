package com.digout.event.listener;

import com.digout.event.RecoverPasswordEvent;
import com.digout.event.source.RecoverPasswordEventSource;
import com.digout.support.mail.PasswordRecoveryMailer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class RecoverPasswordEventListener implements ApplicationListener<RecoverPasswordEvent> {

    @Autowired
    private PasswordRecoveryMailer mailer;

    @Override
    public void onApplicationEvent(final RecoverPasswordEvent event) {
        final RecoverPasswordEventSource source = event.getSource();
        this.mailer.sendPassword(source.getEmail(), source.getFullname(), source.getGeneratedPassword());
    }
}

package com.digout.support.mail;

import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

public final class PasswordRecoveryMailer extends Mailer {
    private static final String TEMPLATE_ID = PasswordRecoveryMailer.class.getPackage().getName().replace(".", "/")
            + "/recover-password-email-%s.vm";

    private String from;
    private String goToDigoutUrl;

    @Autowired
    private I18nMessageSource i18n;

    public void sendPassword(final String emailTo, final String userFullname, final String password) {
        final String subj = this.i18n.getMessage("password.change.email.subject");
        final String template = String.format(TEMPLATE_ID, this.i18n.getLocale().getLanguage());
        final String[] to = new String[] { emailTo };
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("fullName", userFullname);
        model.put("email", emailTo);
        model.put("password", password);
        model.put("goToDigoutUrl", this.goToDigoutUrl);
        sendEmail(this.from, to, subj, template, model);
    }

    @Required
    public void setFrom(final String from) {
        this.from = from;
    }

    @Required
    public void setGoToDigoutUrl(final String goToDigoutUrl) {
        this.goToDigoutUrl = goToDigoutUrl;
    }

}

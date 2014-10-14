package com.digout.support.mail;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public abstract class Mailer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mailer.class);

    private MailPropertiesHolder mailPropertiesHolder;
    private VelocityEngine velocityEngine;

    private HtmlEmail prepareHtmlEmail(final String from, final String[] to, final String[] cc, final String subject,
            final String templateName, final Map<String, Object> model) throws EmailException {
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(this.mailPropertiesHolder.getHost());
        htmlEmail.setSmtpPort(this.mailPropertiesHolder.getPort()); // working port value 465
        htmlEmail.setCharset("UTF-8");
        htmlEmail.setAuthenticator(new DefaultAuthenticator(this.mailPropertiesHolder.getUsername(),
                this.mailPropertiesHolder.getPassword()));
        htmlEmail.setSSLOnConnect(true);
        htmlEmail.setFrom(from);
        htmlEmail.setSubject(subject);
        String text = VelocityEngineUtils.mergeTemplateIntoString(this.velocityEngine, templateName, "UTF-8", model);
        htmlEmail.setHtmlMsg(text);
        htmlEmail.addTo(to);
        return htmlEmail;
    }

    protected void sendEmail(final String from, final String[] to, final String subject, final String templateName,
            final Map<String, Object> model) {
        try {
            prepareHtmlEmail(from, to, null, subject, templateName, model).send();
        } catch (EmailException e) {
            LOGGER.error("SendMailError", e);
        }
    }

    @Required
    public void setMailPropertiesHolder(final MailPropertiesHolder mailPropertiesHolder) {
        this.mailPropertiesHolder = mailPropertiesHolder;
    }

    @Required
    public void setVelocityEngine(final VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}

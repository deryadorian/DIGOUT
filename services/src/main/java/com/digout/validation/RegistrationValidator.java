package com.digout.validation;

import com.digout.artifact.Registration;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;

import static com.digout.utils.Matchers.matches;
import static com.digout.validation.Patterns.PHONE_PATTERN;
import static com.digout.validation.Patterns.USERNAME_PATTERN;
import static com.digout.validation.Patterns.FULLNAME_PATTERN;
import static com.digout.validation.Patterns.EMAIL_PATTERN;

import com.digout.support.i18n.I18nMessageSource;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.springframework.beans.factory.annotation.Autowired;

public class RegistrationValidator implements ExceptionAwareValidator<Registration> {

    @Autowired
    private I18nMessageSource i18n;

    @Autowired
    private PasswordValidator passwordValidator;

    private MessageContext preValidate(final Registration registration) {
        MessageContext messageContext = MessageContextFactory.newContext();

        if (registration == null) {
            messageContext.addMessage(this.i18n.getMessage("registration.data.required"));
            return messageContext;
        }

        String login = registration.getUsername();
        if (isNullOrEmpty(login)) {
            messageContext.addMessage(this.i18n.getMessage("username.field.empty"));
        } else if (login.length() < 2) {
            messageContext.addMessage(this.i18n.getMessage("username.min.2.chars"));
        } else if (!matches(login, USERNAME_PATTERN)) {
            messageContext.addMessage(this.i18n.getMessage("login.format.invalid"));
        }

        messageContext.merge(this.passwordValidator.validateAndGet(registration.getPassword()));

        String fullname = registration.getFullname();
        if (isNullOrEmpty(fullname) ? true : fullname.length() < 2) {
            messageContext.addMessage(this.i18n.getMessage("fullname.field.empty"));
        } else if (!matches(fullname, FULLNAME_PATTERN)) {
            messageContext.addMessage(this.i18n.getMessage("fullname.format.invalid"));
        }

        String email = registration.getEmail();
        if (isNullOrEmpty(email)) {
            messageContext.addMessage(this.i18n.getMessage("email.required"));
        } else if (!matches(email, EMAIL_PATTERN)) {
            messageContext.addMessage(this.i18n.getMessage("email.not.valid"));
        }
        
        final String phoneNumber = registration.getMobileNumber();
        if(!isNullOrEmpty(phoneNumber) && !matches(phoneNumber, PHONE_PATTERN)) {
            messageContext.addMessage(this.i18n.getMessage("profile.number.invalid.format"));
        }
        
        return messageContext;
    }

    @Override
    public void validateAndRaise(final Registration registration) throws ValidationException {
        MessageContext messageContext = preValidate(registration);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }

}

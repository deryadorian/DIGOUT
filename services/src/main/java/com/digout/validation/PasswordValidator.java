package com.digout.validation;

import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;

import static com.digout.utils.Matchers.matches;
import static com.digout.validation.Patterns.PASSWORD_PATTERN;
import static com.google.common.base.Strings.isNullOrEmpty;

public class PasswordValidator implements ExceptionAwareValidator<String>, SimpleValidator<String> {

    @Autowired
    private I18nMessageSource i18n;

    private MessageContext preValidate(final String password) {
        MessageContext messageContext = MessageContextFactory.newContext();
        if (isNullOrEmpty(password)) {
            messageContext.addMessage(this.i18n.getMessage("password.required"));
        } else if (!matches(password, PASSWORD_PATTERN)) {
            messageContext.addMessage(this.i18n.getMessage("password.format.invalid"));
        }
        return messageContext;
    }

    @Override
    public MessageContext validateAndGet(final String password) {
        return preValidate(password);
    }

    @Override
    public void validateAndRaise(final String password) throws ValidationException {
        MessageContext messageContext = preValidate(password);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }
}

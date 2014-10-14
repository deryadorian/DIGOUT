package com.digout.validation;

import com.digout.artifact.PasswordChange;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;

import static com.digout.utils.Matchers.matches;
import static com.digout.validation.Patterns.PASSWORD_PATTERN;
import static com.google.common.base.Strings.isNullOrEmpty;

public class PasswordChangeValidator implements ExceptionAwareValidator<PasswordChange> {

    @Autowired
    private I18nMessageSource i18n;

    private MessageContext preValidate(final PasswordChange passwordChange) {
        MessageContext mc = MessageContextFactory.newContext();

        if (passwordChange == null) {
            mc.addMessage(this.i18n.getMessage("password.change.required"));
            return mc;
        }

        String newPassword = passwordChange.getNewPassword();
        if (isNullOrEmpty(newPassword)) {
            mc.addMessage(this.i18n.getMessage("password.change.new.required"));
            return mc;
        } else if (!matches(newPassword, PASSWORD_PATTERN)) {
            mc.addMessage(this.i18n.getMessage("password.change.new.format.invalid"));
            return mc;
        }

        String confirmPassword = passwordChange.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            mc.addMessage(this.i18n.getMessage("password.change.confirm.password.not.matches"));
        }

        return mc;
    }

    @Override
    public void validateAndRaise(final PasswordChange passwordChange) throws ValidationException {
        MessageContext messageContext = preValidate(passwordChange);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }
}

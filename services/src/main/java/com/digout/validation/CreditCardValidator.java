package com.digout.validation;

import com.digout.artifact.Order;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import static com.digout.utils.Matchers.matches;
import static com.digout.validation.Patterns.EXPIRATION_YEAR_MONTH_PATTERN;
import static com.digout.validation.Patterns.CVV_PATTERN;

public class CreditCardValidator implements ExceptionAwareValidator<Order> {

    @Autowired
    private I18nMessageSource i18n;

    // todo: use i18n
    private MessageContext preValidate(final Order order) {
        MessageContext messageContext = MessageContextFactory.newContext();

        // todo:write universal regex pattern for credit card number
        if (Strings.isNullOrEmpty(order.getCreditCardNumber())) {
            /*
             * if (!matches(order.getCreditCardNumber(), Patterns.CREDIT_CARD_NUMBER)) {
             * messageContext.addMessage("Credit Card Number not valid");
             * 
             * }
             */
            messageContext.addMessage(this.i18n.getMessage("credit.card.number.required"));
        } else {
            /*
             * if (!matches(order.getCreditCardNumber(), CREDIT_CARD_NUMBER)) {
             * messageContext.addMessage("invalid credit card number format"); }
             */
        }

        if (Strings.isNullOrEmpty(order.getExpirationMonth())) {
            messageContext.addMessage(this.i18n.getMessage("expiration.month.required"));
        } else {
            if (!matches(order.getExpirationMonth(), EXPIRATION_YEAR_MONTH_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("expiration.month.invalid.format"));
            }
        }

        if (Strings.isNullOrEmpty(order.getExpirationYear())) {
            messageContext.addMessage(this.i18n.getMessage("expiration.year.required"));
        } else {
            if (!matches(order.getExpirationYear(), EXPIRATION_YEAR_MONTH_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("expiration.year.invalid.format"));
            }
        }

        if (Strings.isNullOrEmpty(order.getSecurityCode())) {
            messageContext.addMessage(this.i18n.getMessage("security.number.required"));
        } else {
            if (!matches(order.getSecurityCode(), CVV_PATTERN)) {
                messageContext.addMessage(this.i18n.getMessage("security.number.invalid.format"));
            }
        }

        return messageContext;

    }

    @Override
    public void validateAndRaise(final Order order) throws ValidationException {
        MessageContext messageContext = preValidate(order);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }

}

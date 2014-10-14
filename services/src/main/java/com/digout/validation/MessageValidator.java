package com.digout.validation;

import com.digout.artifact.Message;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageValidator implements ExceptionAwareValidator<Message> {
    @Autowired
    private I18nMessageSource i18nMessageSource;

    private MessageContext preValidate(final Message message) {
        MessageContext messageContext = MessageContextFactory.newContext();

        if (message == null) {
            messageContext.addMessage(this.i18nMessageSource.getMessage("message.data.empty"));
            return messageContext;
        }

        if (message.isSetData()) {
            String text = message.getData();
            if (text.isEmpty()) {
                messageContext.addMessage(this.i18nMessageSource.getMessage("message.data.empty"));
            }
            if (text.length() > 140) {
                messageContext.addMessage(this.i18nMessageSource.getMessage("message.overflow.data"));
            }
        } else {
            messageContext.addMessage(this.i18nMessageSource.getMessage("message.data.empty"));
        }

        if (!message.isSetReceiverId()) {
            messageContext.addMessage(this.i18nMessageSource.getMessage("message.receiver.id.required"));
        }

        return messageContext;
    }

    @Override
    public void validateAndRaise(final Message message) throws ValidationException {
        MessageContext messageContext = preValidate(message);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }
}

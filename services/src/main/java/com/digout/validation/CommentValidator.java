package com.digout.validation;

import com.digout.artifact.Comment;
import com.digout.exception.ValidationException;
import com.digout.support.context.MessageContext;
import com.digout.support.context.MessageContextFactory;
import com.digout.support.i18n.I18nMessageSource;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentValidator implements ExceptionAwareValidator<Comment> {
    @Autowired
    private I18nMessageSource i18nMessageSource;

    private MessageContext preValidate(final Comment comment) {
        MessageContext messageContext = MessageContextFactory.newContext();

        if (comment == null) {
            messageContext.addMessage(this.i18nMessageSource.getMessage("comment.data.empty"));
            return messageContext;
        }

        if (comment.isSetText()) {
            String text = comment.getText();
            if (text.isEmpty()) {
                messageContext.addMessage(this.i18nMessageSource.getMessage("comment.data.empty"));
            }
            if (text.length() > 140) {
                messageContext.addMessage(this.i18nMessageSource.getMessage("comment.overflow.data"));
            }
        } else {
            messageContext.addMessage(this.i18nMessageSource.getMessage("comment.data.empty"));
        }

        if (!comment.isSetProductId()) {
            messageContext.addMessage(this.i18nMessageSource.getMessage("comment.product.id.required"));
        }

        return messageContext;
    }

    @Override
    public void validateAndRaise(final Comment comment) throws ValidationException {
        MessageContext messageContext = preValidate(comment);
        if (!messageContext.isEmpty()) {
            throw new ValidationException(messageContext);
        }
    }
}

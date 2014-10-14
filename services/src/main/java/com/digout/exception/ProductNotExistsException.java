package com.digout.exception;

import com.digout.support.context.MessageContext;

public class ProductNotExistsException extends ApplicationException {

    private static final long serialVersionUID = -7820586747234370796L;

    public ProductNotExistsException(final MessageContext messageContext) {
        super(messageContext);
    }

    public ProductNotExistsException(final String message) {
        super(message);
    }

    public ProductNotExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // @Override
    // public String getMessage() {
    // return "Product does not exist";
    // }

}

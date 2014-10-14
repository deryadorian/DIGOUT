package com.digout.validation;

import com.digout.support.context.MessageContext;

public interface SimpleValidator<T> {
    MessageContext validateAndGet(T value);
}

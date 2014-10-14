package com.digout.validation;

import com.digout.exception.ValidationException;

public interface ExceptionAwareValidator<T> {

    void validateAndRaise(T t) throws ValidationException;
}

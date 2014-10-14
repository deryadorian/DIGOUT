package com.digout.validation;

import com.digout.exception.ApplicationException;

public interface Validator<T> {
    boolean validate(T value) throws ApplicationException;
}

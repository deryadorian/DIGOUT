package com.digout.validation;

import com.digout.artifact.UserCredentials;
import com.digout.exception.ApplicationException;

public class UserCredentialsValidator implements Validator<UserCredentials> {
    @Override
    public boolean validate(final UserCredentials credentials) throws ApplicationException {
        return false;
    }
}

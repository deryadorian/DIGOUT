package com.digout.validation;

import com.digout.artifact.User;
import com.digout.exception.ApplicationException;

import java.util.regex.Pattern;

// get rid of this validator
public class UserValidator implements Validator<User> {
    static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d+");
    static final Pattern NAME_PATTERN = Pattern.compile("[\\D&&[\\S]]+");
    static final Pattern PHONE_PATTERN = Pattern.compile("(\\+)[0-9]{12}");
    static final Pattern EMAIL_PATTERN = Pattern.compile("");

    @Override
    public boolean validate(final User user) throws ApplicationException {
        // if(user==null) return false;
        // if(user.isSetFullname() && !NAME_PATTERN
        // .matcher(user.getFullname()).matches()) return false;
        // if(user.isSetMobileNumber() && !PHONE_PATTERN
        // .matcher(user.getMobileNumber()).matches()) return false;
        return true;
    }
}

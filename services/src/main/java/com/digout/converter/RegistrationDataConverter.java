package com.digout.converter;

import org.springframework.util.Assert;

import com.digout.artifact.Registration;
import com.digout.model.entity.user.UserCredentialsEntity;
import com.digout.model.entity.user.UserEntity;

public class RegistrationDataConverter extends SimpleConverterFactory<Registration, UserEntity> {

    @Override
    protected UserEntity initEntity(final Registration registration) {
        Assert.notNull(registration);
        UserEntity userEntity = new UserEntity();
        userEntity.setFullname(registration.getFullname());
        UserCredentialsEntity credentialsEntity = new UserCredentialsEntity(registration.getUsername(),
                registration.getEmail(), registration.getPassword());
        userEntity.setUserCredentials(credentialsEntity);
        userEntity.setMobileNumber(registration.getMobileNumber());
        return userEntity;
    }

    @Override
    protected Registration initTO(final UserEntity entity) {
        throw new UnsupportedOperationException("Convertion to Registration model is not reqiured!");
    }

}

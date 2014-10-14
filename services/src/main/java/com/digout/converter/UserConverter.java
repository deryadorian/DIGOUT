package com.digout.converter;

import com.google.common.base.Strings;
import org.springframework.util.Assert;

import com.digout.artifact.User;
import com.digout.artifact.UserCredentials;
import com.digout.model.entity.user.UserCredentialsEntity;
import com.digout.model.entity.user.UserEntity;

public class UserConverter extends SimpleConverterFactory<User, UserEntity> {

    @Override
    protected UserEntity initEntity(final User user) {
        Assert.notNull(user);
        UserEntity userEntity = new UserEntity();
        userEntity.setFullname(user.getFullname());
        UserCredentials userCredentials = user.getCredentials();
        UserCredentialsEntity credentialsEntity = new UserCredentialsEntity(userCredentials.getUsername(),
                userCredentials.getEmail(), userCredentials.getPassword());
        userEntity.setUserCredentials(credentialsEntity);
        userEntity.setRating(user.getRank());
        return userEntity;
    }

    @Override
    protected User initTO(final UserEntity entity) {
        Assert.notNull(entity);
        User user = new User();
        user.setFullname(entity.getFullname());
        user.setUserId(entity.getId());
        UserCredentials credentials = new UserCredentials();
        final String username = entity.getUserCredentials().getUsername();
        credentials.setUsername(Strings.isNullOrEmpty(username) ? entity.getFullname() : username);
        credentials.setEmail(entity.getUserCredentials().getEmail());
        user.setCredentials(credentials);
        // user.setMobileNumber(value)
        return user;
    }
}

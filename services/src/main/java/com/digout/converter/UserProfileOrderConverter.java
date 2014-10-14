package com.digout.converter;

import com.digout.artifact.UserProfile;
import com.digout.model.entity.user.UserEntity;

public class UserProfileOrderConverter extends SimpleConverterFactory<UserProfile, UserEntity> {
    @Override
    protected UserEntity initEntity(final UserProfile userProfile) {
        return null;
    }

    @Override
    protected UserProfile initTO(final UserEntity entity) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(entity.getId());
        userProfile.setFullname(entity.getFullname());
        userProfile.setUsername(entity.getUserCredentials().getUsername());
        return userProfile;
    }
}

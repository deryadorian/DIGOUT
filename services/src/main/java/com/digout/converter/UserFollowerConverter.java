package com.digout.converter;

import com.digout.artifact.UserFollower;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.user.UserCredentialsEntity;
import com.digout.model.entity.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class UserFollowerConverter extends SimpleConverterFactory<UserFollower, UserEntity> {
    @Autowired
    private UserPhotoConverter userPhotoConverter;

    public UserFollower createTO(final UserEntity entity, final Boolean following) {
        UserFollower userFollower = new UserFollower();
        userFollower.setId(entity.getId());
        UserCredentialsEntity credentials = entity.getUserCredentials();
        userFollower.setUsername(credentials.getUsername());
        userFollower.setFullname(entity.getFullname());
        userFollower.setFollowing(following);
        userFollower.setUserThumbImage(this.userPhotoConverter.convertUserImageEntity(entity.getImages(),
                ImageFormat.THUMB));
        return userFollower;
    }

    @Override
    protected UserEntity initEntity(final UserFollower userFollower) {
        return null;
    }

    @Override
    protected UserFollower initTO(final UserEntity entity) {
        UserFollower userFollower = new UserFollower();
        userFollower.setId(entity.getId());
        UserCredentialsEntity credentials = entity.getUserCredentials();
        userFollower.setUsername(credentials.getUsername());
        userFollower.setFullname(entity.getFullname());
        userFollower.setUserThumbImage(this.userPhotoConverter.convertUserImageEntity(entity.getImages(),
                ImageFormat.THUMB));
        return userFollower;
    }
}

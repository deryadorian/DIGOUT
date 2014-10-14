package com.digout.converter;

import com.digout.artifact.UserFollow;
import com.digout.model.common.ImageFormat;
import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserFollowerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class UserFollowConverter extends SimpleConverterFactory<UserFollow, UserFollowerEntity> {

    @Autowired
    private UserPhotoConverter userPhotoConverter;

    @Override
    protected UserFollowerEntity initEntity(final UserFollow userFollow) {
        return null;
    }

    public UserFollow initTo(final UserFollowerEntity entity, final Long userId) {
        UserFollow userFollow = new UserFollow();
        UserEntity follower = entity.getFollower();
        UserEntity followed = entity.getFollowed();
        userFollow.setFollowerId(follower.getId());
        userFollow.setFollowedId(followed.getId());
        userFollow.setFollowerUserName(follower.getUserCredentials().getUsername());
        userFollow.setFollowedUserName(followed.getUserCredentials().getUsername());
        userFollow.setFollowedFullName(followed.getFullname());
        userFollow.setFollowerFullName(follower.getFullname());
        if (entity.getAdditionalFollowing() != null) {
            userFollow.setFollowing(entity.getAdditionalFollowing());
        }
        return userFollow;
    }

    @Override
    protected UserFollow initTO(final UserFollowerEntity entity) {
        Assert.notNull(entity);
        UserFollow userFollow = new UserFollow();
        UserEntity follower = entity.getFollower();
        UserEntity followed = entity.getFollowed();
        userFollow.setFollowerId(follower.getId());
        userFollow.setFollowedId(followed.getId());
        userFollow.setFollowerUserName(follower.getUserCredentials().getUsername());
        userFollow.setFollowedUserName(followed.getUserCredentials().getUsername());
        userFollow.setFollowedFullName(followed.getFullname());
        userFollow.setFollowerFullName(follower.getFullname());
        userFollow.setFollowerThumbImage(this.userPhotoConverter.convertUserImageEntity(follower.getImages(),
                ImageFormat.THUMB));
        userFollow.setFollowedThumbImage(this.userPhotoConverter.convertUserImageEntity(followed.getImages(),
                ImageFormat.THUMB));
        if (entity.getAdditionalFollowing() != null) {
            userFollow.setFollowing(entity.getAdditionalFollowing());
        } else {
            userFollow.setFollowing(entity.isFollowing());
        }
        return userFollow;
    }
}

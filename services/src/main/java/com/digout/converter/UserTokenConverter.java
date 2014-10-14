package com.digout.converter;

import org.springframework.beans.factory.annotation.Autowired;

import com.digout.artifact.UserSession;
import com.digout.model.entity.user.UserTokenEntity;

public class UserTokenConverter extends SimpleConverterFactory<UserSession, UserTokenEntity> {

    @Autowired
    private UserConverter userConverter;

    @Override
    protected UserTokenEntity initEntity(final UserSession to) {
        UserTokenEntity entity = new UserTokenEntity();
        entity.setTokenId(to.getSessionToken());
        entity.setUser(this.userConverter.createEntity(to.getUser()));
        return entity;
    }

    @Override
    protected UserSession initTO(final UserTokenEntity entity) {
        UserSession userSession = new UserSession();
        userSession.setSessionToken(entity.getTokenId());
        userSession.setUser(this.userConverter.createTO(entity.getUser()));
        return userSession;
    }

}

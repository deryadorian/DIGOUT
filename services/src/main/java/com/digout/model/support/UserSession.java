package com.digout.model.support;

import com.digout.model.entity.user.UserTokenEntity;

public final class UserSession {
    private final UserTokenEntity userToken;

    public UserSession(final UserTokenEntity userToken) {
        this.userToken = userToken;
    }

    public UserTokenEntity getUserToken() {
        return this.userToken;
    }

    // public boolean isLoggedIn() {
    // return userToken != null && userToken.getUser() != null
    // && isTokenAlive();
    // }
    //
    // public boolean isTokenAlive() {
    // return userToken.isAlive();
    // }
}

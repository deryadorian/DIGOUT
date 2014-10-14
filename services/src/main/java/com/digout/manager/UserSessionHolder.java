package com.digout.manager;

import com.digout.model.entity.user.UserEntity;
import com.digout.model.entity.user.UserTokenEntity;
import com.digout.model.support.UserSession;

public class UserSessionHolder {
    private static final ThreadLocal<UserSession> threadLocalUserSession = new InheritableThreadLocal<UserSession>();

    public void clean() {
        threadLocalUserSession.set(null);
    }

    public UserSession getSession() {
        return threadLocalUserSession.get();
    }

    public UserSession getSession(final UserTokenEntity userToken) {
        UserSession userSession = new UserSession(userToken);
        return userSession;
    }

    public UserEntity getUser() {
        UserSession userSession = getSession();
        return userSession != null ? userSession.getUserToken().getUser() : null;
    }

    public Long getUserId() {
        UserSession userSession = getSession();
        return userSession != null ? userSession.getUserToken().getUser().getId() : null;
    }

    public String getUsername() {
        UserSession userSession = getSession();
        return userSession != null ? userSession.getUserToken().getUser().getUserCredentials().getUsername() : null;
    }

    public void init(final UserSession userSession) {
        threadLocalUserSession.set(userSession);
    }

}

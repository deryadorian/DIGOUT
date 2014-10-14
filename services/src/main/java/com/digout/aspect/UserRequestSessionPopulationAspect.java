package com.digout.aspect;

import com.digout.support.i18n.I18nMessageSource;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.google.common.base.Strings;
import com.digout.exception.UserTokenNotExistsException;
import com.digout.manager.RequestSessionHolder;
import com.digout.manager.UserSessionHolder;
import com.digout.model.entity.user.UserTokenEntity;
import com.digout.model.support.UserSession;
import com.digout.repository.UserSessionRepository;

@Aspect
@Order(SystemPointcut.ORDER_POPULATE_USER_SESSION)
public final class UserRequestSessionPopulationAspect {

    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private I18nMessageSource messageSource;

    @After("com.digout.aspect.SystemPointcut.authenticatedEndpoint()")
    public void populateUserSessionAfterAdvice() {
        this.userSessionHolder.clean();
    }

    @Before("com.digout.aspect.SystemPointcut.authenticatedEndpoint()")
    public void populateUserSessionBeforeAdvice() throws Throwable {
        String tokenId = this.requestSessionHolder.getSession().getTokenId();
        if (Strings.isNullOrEmpty(tokenId)) {
            throw new UserTokenNotExistsException(this.messageSource.getMessage("user.token.not.exists"));
        }

        UserTokenEntity userToken = this.userSessionRepository.findLiveTokenById(tokenId, DateTime.now());
        this.userSessionHolder.init(new UserSession(userToken));
    }
}

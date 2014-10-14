package com.digout.aspect;

import com.digout.config.ConfigurationProvider;
import com.digout.exception.PermisionDeniedException;
import com.digout.manager.RequestSessionHolder;
import com.digout.manager.UserSessionHolder;
import com.digout.model.UserRole;
import com.digout.model.meta.Authenticated;
import com.digout.support.i18n.I18nMessageSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.digout.exception.UserNotLoggedException;
import com.digout.model.entity.user.UserTokenEntity;
import com.digout.repository.UserSessionRepository;
import org.springframework.util.Assert;

import java.util.Arrays;

@org.aspectj.lang.annotation.Aspect
@Order(SystemPointcut.ORDER_AUTH)
public class AuthorizationAspect {

    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private I18nMessageSource messageSource;
    @Autowired
    private RequestSessionHolder requestSessionHolder;
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Around("com.digout.aspect.SystemPointcut.authenticated()")
    @Transactional
    public Object authenticatedAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        UserTokenEntity token = this.userSessionHolder.getSession().getUserToken();

        if (token != null) {
            checkPriveledgies(joinPoint, token.getUser().getRole());
            DateTime now = DateTime.now();
            token.setLastActionTime(now);
            DateTime futureTimeout = now.plusMinutes(configurationProvider.getUserSessionTimeoutMins());
            if (!token.getExpireTime().isAfter(futureTimeout)) {
                token.setExpireTime(futureTimeout);
            }
            token.getUser().setLocale(this.requestSessionHolder.getLocale());
            this.userSessionRepository.save(token);
            return joinPoint.proceed();
        }
        throw new UserNotLoggedException(this.messageSource.getMessage("user.not.authenticated"));
    }

    private void checkPriveledgies(final ProceedingJoinPoint join, final UserRole userRole)
            throws PermisionDeniedException {
        MethodSignature methodSignature = (MethodSignature) join.getSignature();
        Authenticated auth = methodSignature.getMethod().getAnnotation(Authenticated.class);
        Assert.notNull(auth, Authenticated.class.getName() + " is absent");
        UserRole[] methodRoles = auth.value();
        if (!Arrays.asList(methodRoles).contains(userRole)) {
            throw new PermisionDeniedException(this.messageSource.getMessage("user.has.no.permission"));
        }

    }
}

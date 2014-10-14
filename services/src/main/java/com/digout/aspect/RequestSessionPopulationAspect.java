package com.digout.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.digout.manager.RequestSessionHolder;
import com.digout.model.support.RequestSession;

@Aspect
@Order(SystemPointcut.ORDER_POPULATE_REQUEST_SESSION)
public final class RequestSessionPopulationAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestSessionPopulationAspect.class);

    @Autowired
    private RequestSessionHolder requestSessionHolder;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @After("com.digout.aspect.SystemPointcut.endpoint()")
    public void populateRequestSessionAfterAdvice() {
        this.requestSessionHolder.clean();
    }

    @Before("com.digout.aspect.SystemPointcut.endpoint()")
    public void populateRequestSessionBeforeAdvice() {
        this.requestSessionHolder.init(new RequestSession(this.httpServletRequest));
    }
}

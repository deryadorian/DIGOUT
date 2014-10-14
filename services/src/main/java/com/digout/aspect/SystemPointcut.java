package com.digout.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public final class SystemPointcut {

    public static final int ORDER_POPULATE_REQUEST_SESSION = 1;
    public static final int ORDER_AUDIT = 2;
    public static final int ORDER_POPULATE_USER_SESSION = 3;
    public static final int ORDER_AUTH = 4;
    public static final int ORDER_TRANSACTION = 200;

    @Pointcut("publicMethod() && auditableAnnotation()")
    public void auditable() {
    }

    @Pointcut("@within(com.digout.model.meta.Auditable) || @annotation(com.digout.model.meta.Auditable)")
    public void auditableAnnotation() {
    }

    @Pointcut("publicMethod() && authenticatedAnnotation()")
    public void authenticated() {
    }

    @Pointcut("@within(com.digout.model.meta.Authenticated) || @annotation(com.digout.model.meta.Authenticated)")
    public void authenticatedAnnotation() {
    }

    @Pointcut("authenticatedAnnotation() && endpoint()")
    public void authenticatedEndpoint() {
    }

    @Pointcut("restfullEndpoint()")
    public void endpoint() {
    }

    @Pointcut("publicMethod() && exceptionableAnnotation()")
    public void exceptionable() {
    }

    @Pointcut("@within(com.digout.model.meta.MarshallException) || @annotation(com.digout.model.meta.MarshallException)")
    public void exceptionableAnnotation() {
    }

    @Pointcut("execution(private * *(..))")
    public void privateMethod() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Pointcut("publicMethod() && restfullPathAnnotation()")
    public void restfullEndpoint() {
    }

    @Pointcut("@within(javax.ws.rs.Path) || @annotation(javax.ws.rs.Path)")
    public void restfullPathAnnotation() {
    }
}

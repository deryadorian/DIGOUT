package com.digout.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.digout.manager.UserSessionHolder;
import com.digout.model.entity.common.AuditLogEntity;
import com.digout.repository.AuditRepository;

@Aspect
@Order(SystemPointcut.ORDER_AUDIT)
public final class AuditAspect {

    @Autowired
    private UserSessionHolder userSessionHolder;
    @Autowired
    private AuditRepository auditRepository;

    @Around("com.digout.aspect.SystemPointcut.auditable()")
    @Transactional
    public Object auditableAdvice(final ProceedingJoinPoint joinPoint) throws Throwable {
        AuditLogEntity.Builder builder = AuditLogEntity.newBuilder();
        Throwable throwable = null;
        MethodSignature methodSign = (MethodSignature) joinPoint.getSignature();
        Object response = null;
        try {
            Method method = methodSign.getMethod();
            Object[] args = joinPoint.getArgs();
            StringBuilder stringBuilder = new StringBuilder();
            for (Object arg : args) {
                stringBuilder.append(arg.toString()).append(";");
            }
            builder.withUsername(this.userSessionHolder.getUsername()).withOperation(method.getName())
                    .withInput(stringBuilder.toString());
            response = joinPoint.proceed();
            return response;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            builder.withOutput(throwable != null ? throwable.getMessage() : response.toString());
            try {
                this.auditRepository.save(builder.build());
            } catch (Throwable e) {
                // TODO: add slf4j
                System.out.println(e);
            }
        }
    }
}

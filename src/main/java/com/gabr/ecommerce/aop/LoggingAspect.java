package com.gabr.ecommerce.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Before("execution(* com.gabr.ecommerce.controllers..*(..)) || execution(* com.gabr.ecommerce.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("➡️ Method Start: {} | Args: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "execution(* com.gabr.ecommerce.controllers..*(..)) || execution(* com.gabr.ecommerce.service..*(..))",returning = "result")
    public void logAfter(JoinPoint joinPoint,Object result) {
        log.info("✅ Method Success: {} | Result: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(value = "execution(* com.gabr.ecommerce.controllers..*(..)) || execution(* com.gabr.ecommerce.service..*(..))",throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.error("🔥 Exception in {} | Message: {}", joinPoint.getSignature().getName(), ex.getMessage());
    }
}

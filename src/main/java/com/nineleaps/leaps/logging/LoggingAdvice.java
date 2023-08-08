//package com.nineleaps.leaps.logging;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Aspect
//@Component
//public class LoggingAdvice {
//
//    Logger log = LoggerFactory.getLogger(LoggingAdvice.class);
//
//    @Pointcut(value="execution(* com.nineleaps.leaps.controller.*.*(..)) && execution(* com.nineleaps.leaps.service.implementation.*.*(..)) && execution(* com.nineleaps.leaps.service.UserServiceInterface.*.*(..))")
//    public void myPointcut() {
//
//    }
//
//    @Around("myPointcut()")
//    public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {
//        ObjectMapper mapper = new ObjectMapper();
//        String methodName = pjp.getSignature().getName();
//        String className = pjp.getTarget().getClass().toString();
//        Object[] array = pjp.getArgs();
//        log.info(String.format("method invoked %s : %s()arguments : %s", className, methodName, mapper.writeValueAsString(array)));
//        Object object = pjp.proceed();
//        log.info(String.format("%s : %s()Response : %s", className, methodName, mapper.writeValueAsString(object)));
//        return object;
//    }
//
//}
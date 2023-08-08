//package com.nineleaps.leaps.logging;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.hibernate.mapping.Join;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Aspect
//@Component
//public class ControllerAspect {
//    Logger log = LoggerFactory.getLogger(ControllerAspect.class);
//
//    @Before(value = "execution(* com.nineleaps.leaps.controller.*.*(..))")
//    public void beforeAdvice(JoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        String argsAsString = Arrays.deepToString(args); // Convert arguments to a readable string
//
//        log.info("request to " + joinPoint.getSignature().getName() + " arguments are " + argsAsString);
//    }
//
//
//}

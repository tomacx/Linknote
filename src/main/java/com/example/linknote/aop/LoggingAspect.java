//package com.example.linknote.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//public class LoggingAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
//
//    /**
//     * 拦截所有Service层方法并记录日志
//     */
//    @Around("execution(* com.example.linknote.service.*.*(..))")
//    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//        String className = joinPoint.getTarget().getClass().getSimpleName();
//
//        logger.info("Entering {}.{}", className, methodName);
//
//        try {
//            Object result = joinPoint.proceed(); // 执行原方法
//            logger.info("Exiting {}.{} with result: {}", className, methodName, result);
//            return result;
//        } catch (Exception e) {
//            logger.error("Error in {}.{}: {}", className, methodName, e.getMessage());
//            throw e;
//        }
//    }
//}
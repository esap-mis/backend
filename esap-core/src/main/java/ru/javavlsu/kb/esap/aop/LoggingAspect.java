package ru.javavlsu.kb.esap.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

/**
 * LoggingAspect 20.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* ru.javavlsu.kb.esap.controller.*.*(..))")
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(Level.INFO, joinPoint);
    }

    @Around("execution(* ru.javavlsu.kb.esap.service.*.*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) return joinPoint.proceed();
        return logExecutionTime(Level.DEBUG, joinPoint);
    }

    @Around("execution(* ru.javavlsu.kb.esap.repository.*.*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) return joinPoint.proceed();
        return logExecutionTime(Level.DEBUG, joinPoint);
    }

    public Object logExecutionTime(Level level, ProceedingJoinPoint joinPoint) throws Throwable {
        final StopWatch stopWatch = new StopWatch();
        final String className = joinPoint.getSignature().getDeclaringTypeName();
        final String methodName = joinPoint.getSignature().getName();
        final String args = Arrays.toString(joinPoint.getArgs());
        stopWatch.start();

        try {
            final Object result = joinPoint.proceed();
            stopWatch.stop();
            log.atLevel(level)
                .log("Class: {}. Method: {}. Args: {}. Execution time: {} ms",
                    className, methodName, args, stopWatch.getTotalTimeMillis());
            return result;

        } catch (Throwable throwable) {
            stopWatch.stop();
            log.error("Class: {}. Method: {}. Args: {}. Execution time: {} ms. Exception: {}",
                    className, methodName, args, stopWatch.getTotalTimeMillis(),
                    throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}

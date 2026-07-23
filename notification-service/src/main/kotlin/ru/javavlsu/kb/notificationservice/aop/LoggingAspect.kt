package ru.javavlsu.kb.notificationservice.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

/**
 * LoggingAspect 20.07.2026 thewyolar
 * Copyright (c) 2026.
 */
@Aspect
@Component
class LoggingAspect {

    private val log: Logger = LoggerFactory.getLogger(LoggingAspect::class.java)

    @Around("execution(* ru.javavlsu.kb.notificationservice.*.*(..))")
    @Throws(Throwable::class)
    fun logServiceExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        return logExecutionTime(Level.DEBUG, joinPoint)
    }

    @Around("execution(* ru.javavlsu.kb.notificationservice.repository.*.*(..))")
    @Throws(Throwable::class)
    fun logRepositoryExecutionTime(joinPoint: ProceedingJoinPoint): Any {
        if (!log.isDebugEnabled) return joinPoint.proceed()
        return logExecutionTime(Level.DEBUG, joinPoint)
    }

    @Throws(Throwable::class)
    fun logExecutionTime(level: Level, joinPoint: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()
        val className: String = joinPoint.signature.declaringTypeName
        val methodName: String = joinPoint.signature.name
        val args: String = joinPoint.args.contentToString()
        stopWatch.start()

        try {
            val result: Any = joinPoint.proceed()
            stopWatch.stop()
            log.atLevel(level)
                .log(
                    "Class: {}. Method: {}. Args: {}. Execution time: {} ms",
                    className, methodName, args, stopWatch.totalTimeMillis
                )
            return result
        } catch (throwable: Throwable) {
            stopWatch.stop()
            log.error(
                "Class: {}. Method: {}. Args: {}. Execution time: {} ms. Exception: {}",
                className, methodName, args, stopWatch.totalTimeMillis,
                throwable.message, throwable
            )
            throw throwable
        }
    }
}

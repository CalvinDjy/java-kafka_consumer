package com.calvin.service.aop;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *
 * Created by Calvin.Ding on 2016/11/8.
 */
@Component
@Aspect
@Order(1)
public class ServiceAspect extends AbstractAspect {

    @Around("execution(* com.calvin.service.service.impl..*.*(..))")
    public Object processAround(ProceedingJoinPoint jp) {
        try {
            return jp.proceed(jp.getArgs());
        } catch (Throwable throwable) {
            //记录日志
            printLogMethodException(jp, throwable);
            return null;
        }
    }

    /**
     * 打印方法异常日志。
     *
     * @param jp 切入点
     * @param e  异常
     */
    protected void printLogMethodException(JoinPoint jp, Throwable e) {
        LOG.error("-----------------printLogMethodException start--------------------------");
        LOG.error(getLogHeader(jp) + " exception## arguments: " + JSON.toJSONString(jp.getArgs()));
        LOG.error(getLogHeader(jp) + " exception## " + e.getMessage(), e);
        LOG.error("-----------------printLogMethodException end---------------------------");
    }

}

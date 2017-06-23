package com.calvin.service.aop;

import com.calvin.service.util.TimeUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * AspectJ框架切面抽象类，封装了一些切面通用的方法，如获取方法对象、方法名称等。
 *
 * @author lijing
 */
public class AbstractAspect {

    /**
     * 结果打印阀值，当结果JSON串的长度超过3000时，不打印结果。
     */
    protected static final int RESULT_TO_PRINT_THRESHHOLD = 3000;

    /**
     * 保存每个线程在方法中特定点的时间戳，用于获取线程在某一块方法的耗时。
     */
    protected final ThreadLocal<Long> threadTimeRecorder = new ThreadLocal<>();

    /**
     * 日志对象。
     */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractAspect.class);

    /**
     * 根据连接点对象，获取日志的头部信息。
     *
     * @param joinPoint 连接点对象。
     * @return 返回以类权限定名称.方法名格式为固定格式的日志头部信息，
     * 例如com.jinfuzi.shumifund.thrift.serviceIpl.ThriftStockFundProductServiceProxy.batchGetStockFundProduct。
     */
    protected String getLogHeader(JoinPoint joinPoint) {
        return getClassName(joinPoint) + "." + getMethodName(joinPoint);
    }

    /**
     * 根据连接点对象，获取方法名。
     *
     * @param joinPoint 连接点对象。
     * @return 方法名称，如batchGetStockFundProduct。
     */
    protected String getMethodName(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
    }

    /**
     * 根据连接点对象，获取类全限定名。
     * @param joinPoint 连接点对象。
     * @return 类全限定名称，如com.jinfuzi.shumifund.thrift.serviceIpl.ThriftStockFundProductServiceProxy。
     */
    protected String getClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName();
    }

    /**
     * 根据连接点对象，获取简单类名。
     * @param joinPoint 连接点对象。
     * @return 简单类名，如com.jinfuzi.fundapi.mapper.UserMapper应返回UserMapper。
     */
    protected String getSimpleClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    /**
     * 根据连接点对象，获取方法。
     *
     * @param joinPoint 连接点对象。
     * @return 获取方法对象。
     */
    protected Method getMethod(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    /**
     * 记录下当前线程在调用此方法时的时间。
     * @return 调用此方法时的时间。
     */
    protected long recordthreadTime() {
        long currentTime = TimeUtil.getMillisTimestamp();
        threadTimeRecorder.set(currentTime);
        return currentTime;
    }

    /**
     * 获取当前线程在threadTimeRecorder首次记录的时间与当前时间的差值。
     * 若线程在没有记录时间之前调用此方法，返回-1。
     * @return 线程从某个时间点开始，到调用此方法时的耗时。
     */
    protected long getConsumeMilliTimestamp() {
        Long recordTime = threadTimeRecorder.get();
        return recordTime != null ? TimeUtil.getMillisTimestamp() -  recordTime : -1;
    }

    /**
     * 获取实际打印的日志内容。
     * @param template 日志模板。
     * @param valueMap 模板中变量与值的映射。
     * @return 实际的日志内容。
     */
    protected String getActualLogContent(String template, Map<String, String> valueMap) {
        String actualContent = template;
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            actualContent = actualContent.replace(entry.getKey(), entry.getValue());
        }
        return actualContent;
    }

    /**
     * 根据接口返回值，获取打印在日志里的字符串。
     * 当接口返回值是集合或者Map时，返回容器容量，当返回值是对象时。
     * 当该对象JSON串字符超过3000时，返回字符数量。
     * 不满足以上两种情况时，返回该JSON串。
     * @param rtn 接口返回值。
     * @return 该次接口调用的日志里的打印出来的返回结果。
     */
    protected String getPrintResult(Object rtn) {
        if (rtn instanceof Collection) {
            return "Return list size: " + Integer.toString(((Collection) rtn).size());
        }
        if (rtn instanceof Map) {
            return "Return map size: " + Integer.toString(((Collection) rtn).size());
        }
        if (rtn != null) {
            if (rtn.toString().length() > RESULT_TO_PRINT_THRESHHOLD) {
                return "Return object string length: " + rtn.toString().length();
            } else {
                return rtn.toString();
            }
        }
        return "";
    }
}

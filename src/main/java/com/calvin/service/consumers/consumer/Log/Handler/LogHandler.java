package com.calvin.service.consumers.consumer.Log.Handler;

import com.calvin.service.consumers.consumer.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 日志处理器
 * Created by Calvin.Ding on 2017/6/17.
 */
public class LogHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LogHandler.class);

    /**
     * 处理消息类型
     */
    @Value("${subtype.topic.print}")
    protected String handlerType;

    /**
     * 消息处理
     *
     * @param data 消息内容
     */
    @Override
    public void handle(String data) {
        LOG.info("[============   LogHandler   ============] deal with data" + data);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOG.info("sleep over");
    }

    @Override
    public String getHandlerType() {
        return this.handlerType;
    }

}

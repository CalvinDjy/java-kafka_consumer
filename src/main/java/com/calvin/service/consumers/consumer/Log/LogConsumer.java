package com.calvin.service.consumers.consumer.Log;

import com.calvin.service.consumers.consumer.AbstractConsumer;
import com.calvin.service.consumers.consumer.AbstractHandler;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日志消费者
 * Created by Calvin.Ding on 2016/9/7.
 */
@Service
public class LogConsumer extends AbstractConsumer {

    public LogConsumer() {

    }

    public LogConsumer(List<AbstractHandler> handlerList) {
        this.handlerList = handlerList;
    }

}

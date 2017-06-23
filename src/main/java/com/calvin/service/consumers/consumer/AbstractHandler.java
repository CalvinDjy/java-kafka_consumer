package com.calvin.service.consumers.consumer;

/**
 *
 * Created by Calvin.Ding on 2016/10/14.
 */
public abstract class AbstractHandler {

    protected String handlerType;

    public boolean canHandle(String type) {
        return this.getHandlerType().equals(type);
    }

    /**
     * 消息处理
     * @param data 消息内容
     */
    public abstract void handle(String data);

    public abstract String getHandlerType();

}

package com.calvin.service.consumers.groups.Log;

import com.calvin.service.consumers.consumer.AbstractConsumer;
import com.calvin.service.consumers.consumer.Log.LogConsumer;
import com.calvin.service.consumers.groups.ConsumerGroup;

/**
 *
 * Created by Calvin.Ding on 2016/9/22.
 */
public class LogConsumerGroup extends ConsumerGroup {

    @Override
    public AbstractConsumer castConsumer(Object obj) {
        return (LogConsumer)obj;
    }

    public LogConsumerGroup(String beanName, String zookeeper, String groupId, String topic, int threadNum) {
        super(beanName, zookeeper, groupId, topic, threadNum);
    }

}

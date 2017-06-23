package com.calvin.service;


import com.calvin.service.consumers.groups.ConsumerGroup;

import java.util.List;

/**
 *
 * Created by Calvin.Ding on 2016/9/7.
 */
public class ConsumerExecutor {

    protected List<ConsumerGroup> consumerGroupList;

    public ConsumerExecutor(List<ConsumerGroup> consumerGroupList) {
        this.consumerGroupList = consumerGroupList;
    }

    public void execute() {
        for (ConsumerGroup consumerGroup : consumerGroupList) {
            consumerGroup.run();
        }
    }

}

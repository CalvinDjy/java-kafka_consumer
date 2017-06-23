package com.calvin.service.consumers.groups;

import com.calvin.service.consumers.consumer.AbstractConsumer;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Created by Calvin.Ding on 2016/9/7.
 */
public abstract class ConsumerGroup implements ApplicationContextAware{

    protected ApplicationContext context;

    /**
     * 日志组件
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ConsumerGroup.class);

    /**
     * 消费者类名称
     */
    protected String beanName;

    /**
     * zookeeper
     */
    protected String zookeeper;

    /**
     * 消费者组标识
     */
    protected String groupId;

    /**
     * 消息topic
     */
    protected String topic;

    /**
     * 消费者组线程数
     */
    protected int threadNum;

    private ConsumerConnector consumerConnector;

    abstract public AbstractConsumer castConsumer(Object obj);

    public ConsumerGroup() {

    }

    public ConsumerGroup(String beanName, String zookeeper, String groupId, String topic, int threadNum) {
        this.beanName = beanName;
        this.zookeeper = zookeeper;
        this.groupId = groupId;
        this.topic = topic;
        this.threadNum = threadNum;
        consumerConnector = Consumer.createJavaConsumerConnector(this.createConfig());
    }

    public void run() {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(this.topic, this.threadNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(this.topic);
        ExecutorService executor = Executors.newFixedThreadPool(this.threadNum);
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            AbstractConsumer consumer = this.castConsumer(this.context.getBean(this.beanName));
            consumer.setGroupId(this.groupId);
            consumer.setStream(stream);
            consumer.setThreadNumber(threadNumber);
            executor.submit(consumer);
            System.out.println(
                "BeanName : [" + this.beanName + "], " +
                "Consumer : [" + consumer + "], " +
                "ThreadNum : [" + threadNumber + "]"
            );
            threadNumber++;
        }
    }

    protected ConsumerConfig createConfig() {
        Properties props = new Properties();
        props.put("zookeeper.connect", this.zookeeper);
        props.put("group.id", this.groupId);
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        return new ConsumerConfig(props);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}

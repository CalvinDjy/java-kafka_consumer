package com.calvin.service.consumers.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.calvin.service.util.JedisUtils;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 *
 * Created by Calvin.Ding on 2016/9/8.
 */
public abstract class AbstractConsumer implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConsumer.class);

    protected KafkaStream stream;

    protected int threadNumber;

    protected String groupId;

    @Value("${redis.kafka.topic.last.id}")
    protected String topicLastId;

    /**
     *
     */
    protected String messageIdListKey = "messageIdList_";

    @Autowired
    protected JedisUtils jedisUtils;

    /**
     * 消息处理器列表
     */
    protected List<AbstractHandler> handlerList;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            String message;
            MessageAndMetadata<byte[], byte[]> mdata = it.next();
            try {
                message = new String(mdata.message(), "UTF-8");
                LOG.info("[AbstractConsumer][" + this.groupId + "][" + threadNumber + "] receive message from kafka : " + message);

                JSONObject jsonMsg = JSON.parseObject(message);
                if (!jsonMsg.containsKey("msgId") || !jsonMsg.containsKey("type") ||
                        !jsonMsg.containsKey("data") || !jsonMsg.containsKey("time")) {
                    throw new Exception("lack of params!");
                }

                //重复消费检查，用zookeeper的offset(有风险，重启zookeeper有可能会重置offset)
//                Long offset = mdata.offset();
//                if (isDuplicateMessage(offset)) {
//                    throw new Exception("[============   Duplicate Message   ============]");
//                }
//                jedisUtils.set(topicLastId + this.groupId, String.valueOf(mdata.offset()));

                //重复消费检查，用生产者分配的msgId检查(msgId分配和已消费列表维护尚存在问题)
                if (!judgeDuplicateMessage(jsonMsg.getLong("msgId"))) {
                    throw new Exception("[============   Duplicate Message   ============]");
                }

                String type = jsonMsg.getString("type");
                for (AbstractHandler handler : this.handlerList) {
                    if (handler.canHandle(type)) {
                        String data = jsonMsg.getString("data");
                        handler.handle(data);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重复消费检查(利用offset)
     * @param offset 当前消息offset
     * @return boolean
     */
    protected boolean isDuplicateMessage(Long offset) {
        String lastOffset = jedisUtils.get(topicLastId + this.groupId);
        if (lastOffset != null) {
            Long lastOffsetOfGroup = Long.parseLong(lastOffset);
            if (offset <= lastOffsetOfGroup) {
                LOG.info("[============   Duplicate Message   ============] : [group:" + this.groupId +
                        ", offset:" + offset + ", last:" + lastOffsetOfGroup + "]");
                return true;
            }
        }
        return false;
    }

    /**
     * 重读消费检查(利用自定义的messageId)
     * 利用redis有序集合
     * @param messageId 消息id
     * @return boolean
     */
    protected boolean judgeDuplicateMessage(Long messageId) {
        if (jedisUtils.zadd(messageIdListKey, messageId, String.valueOf(messageId)) == 1) {
            //成功写入有序集合，表示消息未被消费过
            //刷新消息id集合
            this.refreshMessageIdSet();
            return true;
        }
        return false;
    }

    /**
     * 消息id集合刷新
     * 1、以滑动窗口(5)形式删除连续消息id
     * 2、若遇到不连续消息id，则待集合大小达到设定阈值(10000)后开始丢弃(阈值足够大后假定缺失的消息已被丢失)
     */
    protected void refreshMessageIdSet() {
        int msgIdSize = (int) jedisUtils.zcard(messageIdListKey);
        Set<Tuple> msgIdSet = jedisUtils.zrange(messageIdListKey, 0, 5);
        int last_element = -1;
        double last_score = 0;
        for (Tuple item : msgIdSet) {
            int element = Integer.parseInt(item.getElement());
            if (element == (last_element + 1) || last_element < 0) {
                last_element = element;
                last_score = item.getScore();
            }
        }
        jedisUtils.zremrangeByScore(messageIdListKey, "0", (msgIdSize > 10000 ? "" : "(") + String.valueOf(last_score));
    }

    public void setStream(KafkaStream stream) {
        this.stream = stream;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        this.messageIdListKey += this.groupId;
    }

}

package com.ehutong.report.mq.demo07Broadcast;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class BroadcastProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("TopicTestGroup");
        producer.setNamesrvAddr("121.199.58.19:9876");
        producer.setVipChannelEnabled(false);
        producer.start();

        for (int i = 0; i < 11; i++){
            Message msg = new Message("TopicTest",
                "TagA",
                "OrderID188",
                "Hello world 1".getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }

}

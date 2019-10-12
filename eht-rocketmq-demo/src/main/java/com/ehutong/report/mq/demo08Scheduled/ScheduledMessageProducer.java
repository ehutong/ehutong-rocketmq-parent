package com.ehutong.report.mq.demo08Scheduled;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class ScheduledMessageProducer {
    public static void main(String[] args) throws Exception {
        // Instantiate a producer to send scheduled messages
        DefaultMQProducer producer = new DefaultMQProducer("TopicTestGroup");
        producer.setNamesrvAddr("121.199.58.19:9876");
        producer.setVipChannelEnabled(false);
        // Launch producer
        producer.start();
        int totalMessagesToSend = 12;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("TopicTest", ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 10 seconds later.
            message.setDelayTimeLevel(3);
            // Send the message
            producer.send(message);
        }
   
        // Shutdown producer after use.
        producer.shutdown();
    }

}

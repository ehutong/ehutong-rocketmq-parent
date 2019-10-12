
package org.apache.rocketmq.samples.springboot;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.samples.springboot.domain.OrderPaidEvent;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Producer, using RocketMQTemplate sends a variety of messages
 */
@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

    private static final String TX_PGROUP_NAME = "myTxProducerGroup";
    // private static final String TX_PGROUP_NAME = "my-group1";

    // rocketMQTemplate , API 方式发送
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    // topic 的名称
    @Value("${demo.rocketmq.transTopic}")
    private String springTransTopic;
    @Value("${demo.rocketmq.topic}")
    private String stringTopic;
    @Value("${demo.rocketmq.orderTopic}")
    private String orderPaidTopic;
    @Value("${demo.rocketmq.msgExtTopic}")
    private String msgExtTopic;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.printf("========== rocketMQTemplate.syncSend  ... <%s> .%n " , stringTopic );
        // Send string, 同步方式发送
        SendResult sendResult = rocketMQTemplate.syncSend(stringTopic, "Hello, World! The 1st msg");
        System.out.printf("run() syncSend <%s> %n", stringTopic);
        System.out.printf("run() sendResult = %s %n", sendResult);

        // Send string with spring Message
        sendResult = rocketMQTemplate.syncSend(stringTopic, "Hello, World! The 2nd msg" );
        System.out.printf("run() syncSend <%s> %n", stringTopic);
        System.out.printf("run() sendResult = %s %n", sendResult);
        System.out.printf(".... rocketMQTemplate.syncSend <%s> end ...%n ", stringTopic );

        System.out.printf("============  rocketMQTemplate.asyncSend ....<%s>  %n " , orderPaidTopic );
        // Send user-defined object，异步方式发送
        rocketMQTemplate.asyncSend(orderPaidTopic, new OrderPaidEvent("T_001", new BigDecimal("88.00")), new SendCallback() {
            public void onSuccess(SendResult var1) {
                System.out.printf("onSuccess() async send, SendResult=%s %n", var1);
            }

            public void onException(Throwable var1) {
                System.out.printf("onException() async send Throwable=%s %n", var1);
            }
        });
        System.out.printf(".... rocketMQTemplate.asyncSend <%s> end ...%n ", orderPaidTopic );

        System.out.printf("==============  rocketMQTemplate.convertAndSend ...<%s>  %n " , msgExtTopic );
        // 指定topic的同时，设置tag值，以便消费端可以根据tag值进行选择性消费
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag0", "I'm tag0");  // tag0 will not be consumer-selected
        System.out.printf("run() syncSend <%s> tag: %s %n", msgExtTopic, "tag0");
        rocketMQTemplate.convertAndSend(msgExtTopic + ":tag1", "I'm tag1");
        System.out.printf("run() syncSend <%s> tag: %s %n", msgExtTopic, "tag1");

        System.out.printf(".... rocketMQTemplate.convertAndSend <%s> end ... %n" , msgExtTopic);

        System.out.println();
        System.out.println();

        // Send transactional messages
        testTransaction();
    }

    private void testTransaction() throws MessagingException {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            try {
                Message msg = MessageBuilder.withPayload("Hello RocketMQ " + i).
                    setHeader(RocketMQHeaders.KEYS, "KEY_" + i).build();
                SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(TX_PGROUP_NAME,
                    springTransTopic + ":" + tags[i % tags.length], msg, null);
                System.out.printf("testTransaction() %d send Transactional msg, body = %s %n",
                        (i+1), msg.getPayload());
                System.out.printf("testTransaction() send result = %s %n", sendResult.getSendStatus());
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当RocketMQ发现`Prepared消息`时，会根据这个Listener实现的策略来决断事务
     */
    @RocketMQTransactionListener(txProducerGroup = TX_PGROUP_NAME)
    class TransactionListenerImpl implements RocketMQLocalTransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);

        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<String, Integer>();

        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            String transId = (String)msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            System.out.printf("executeLocalTransaction() is executed, tid=%s %n", transId);
            int value = transactionIndex.getAndIncrement();

            int status = value % 3;
            System.out.printf("testTransaction() transactionIndex = %d, status = %d %n", value, status );
            localTrans.put(transId, status);
            if (status == 0) {
                // Return local transaction with success(commit), in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # COMMIT # Simulating msg %s related local transaction exec succeeded! ### %n", msg.getPayload());
                System.out.println("----------------- end .... ");
                return RocketMQLocalTransactionState.COMMIT;
            }

            if (status == 1) {
                // Return local transaction with failure(rollback) , in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # ROLLBACK # Simulating %s related local transaction exec failed! %n", msg.getPayload());
                System.out.println("----------------- end .... ");
                return RocketMQLocalTransactionState.ROLLBACK;
            }

            System.out.printf("    # UNKNOW # Simulating %s related local transaction exec UNKNOWN! \n");
            System.out.println("----------------- end .... ");
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            String transId = (String)msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
            Integer status = localTrans.get(transId);
            if (null != status) {
                switch (status) {
                    case 0:
                        retState = RocketMQLocalTransactionState.UNKNOWN;
                        break;
                    case 1:
                        retState = RocketMQLocalTransactionState.COMMIT;
                        break;
                    case 2:
                        retState = RocketMQLocalTransactionState.ROLLBACK;
                        break;
                }
            }
            System.out.printf("checkLocalTransaction() executed once, tid=%s, state=%s status=%s %n",
                transId, retState, status);
            return retState;
        }
    }

}
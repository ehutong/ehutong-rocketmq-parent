/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.samples.springboot.consumer;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * RocketMQMessageListener
 */
@Service
@RocketMQMessageListener(topic = "${demo.rocketmq.topic}", consumerGroup = "string_consumer")
public class StringConsumer implements RocketMQListener<MessageExt>{
    @Override
    public void onMessage(MessageExt messageExt) {
        System.out.printf("Consumer: string-topic received \n" );
        System.out.println(messageExt );
        String msg_id = messageExt.getMsgId();
        byte[] msg_body = messageExt.getBody() ;
        String msg_body_str = new String(msg_body);
        System.out.println("msgId: " + msg_id );
        System.out.println("msgBody: " + msg_body_str );
        // System.out.printf("msgId: %s, body:%s \n", messageExt.getMsgId(), new String(messageExt.getBody()));
    }
}

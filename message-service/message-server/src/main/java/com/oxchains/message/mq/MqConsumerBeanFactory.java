package com.oxchains.message.mq;

import com.oxchains.message.domain.Message;
import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.service.MqService;
import com.oxchains.message.utils.JsonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-05 10:32
 **/
@Configuration
public class MqConsumerBeanFactory {

    private final Logger LOG = LoggerFactory.getLogger(MqConsumerBeanFactory.class);

    @Value("${rocket.nameserver}") private String nameServer;
    @Value("${rocket.consumer.group}") private String consumerGroup;
    @Value("${rocket.message.topic}") private String topic;
    private MqService mqService;
    public MqConsumerBeanFactory(@Autowired MqService mqService){
        this.mqService = mqService;
    }

    @Bean
    public DefaultMQPushConsumer getConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, "message-tag");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                list.stream().forEach(messageExt -> {
                    MessageVO messageVO = JsonUtil.jsonToEntity(new String(messageExt.getBody()), MessageVO.class);
                    mqService.save(messageVO);
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    try {
                        consumer.start();
                    } catch (Exception e) {
                        LOG.error("RocketMq pushConsumer start failure!");
                    }
                    LOG.info("RocketMq pushConsumer started.");
                } catch (InterruptedException e) {
                    LOG.error("RocketMq run failure!");
                }


            }
        }).start();
        return consumer;
    }

}

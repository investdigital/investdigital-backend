package com.oxchains.message.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.oxchains.message.common.MqConts.nameServer;
import static com.oxchains.message.common.MqConts.producerGroup;

/**
 * @author luoxuri
 * @create 2018-03-05 10:32
 **/
@Configuration
public class MqProducerBeanFactory {

//    @Bean
    public DefaultMQProducer getProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setVipChannelEnabled(false);
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);// 设置消费失败时，再次消费的时间间隔
        return producer;
    }

    @Bean
    public TransactionMQProducer transactionMQProducer() throws  MQClientException{
        TransactionMQProducer producer = new TransactionMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setCheckThreadPoolMinSize(2);
        producer.setCheckThreadPoolMaxSize(2);
        producer.setCheckRequestHoldMax(2000);
        producer.start();
        return producer;
    }
}

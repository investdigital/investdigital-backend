package com.oxchains.message.service;

import com.oxchains.message.common.MqConts;
import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.utils.JsonUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import static com.oxchains.message.common.MqConts.*;

/**
 * @author luoxuri
 * @create 2018-03-05 14:19
 **/
@Service
public class MqClientService {

    @Resource
    private DefaultMQProducer producer;

    public void saveMQ(MessageVO messageVO) throws Exception {
        String message = JsonUtil.toJson(messageVO);
        Message msg = new Message(topic, tag, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.err.println("发送失败");
            }
        });

    }

}

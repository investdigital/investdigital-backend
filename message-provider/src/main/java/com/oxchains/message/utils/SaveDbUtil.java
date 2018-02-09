package com.oxchains.message.utils;

import com.oxchains.message.dao.MessageDao;
import com.oxchains.message.dao.MessageTextDao;
import com.oxchains.message.domain.Message;
import com.oxchains.message.domain.MessageText;
import com.oxchains.message.domain.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.oxchains.message.common.MessageReadStatus.READ;

/**
 * 在SaveMessageImpl中添加事务注解dubbo无法注册zookeeper
 * 保存消息到数据库，增加事务注解
 *
 * @author luoxuri
 * @create 2018-02-08 14:29
 **/
@Transactional(rollbackFor = Exception.class)
public class SaveDbUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SaveDbUtil.class);

    public static void saveMessage(MessageDao messageDao, MessageVO messageVO, MessageText messageText) {
        try {
            Message message = new Message();
            message.setMessageTextId(messageText.getId());
            message.setMessageType(messageVO.getMessageType());
            message.setReceiverId(messageVO.getReceiverId());
            message.setReadStatus(READ);
            message.setLogo(messageVO.getLogo());
            message.setReceiverId(messageVO.getReceiverId());
            messageDao.save(message);
        } catch (Exception e) {
            LOG.error("dubbo provider save message error", e);
        }
    }

    public static MessageText saveMessageText(MessageTextDao messageTextDao, MessageVO messageVO) {
        try {
            MessageText messageText = new MessageText();
            messageText.setMessageType(messageVO.getMessageType());
            messageText.setMessage(messageVO.getMessage());
            messageText.setSenderId(messageVO.getSendId());
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            messageText.setPostDate(currentTime);
            messageText.setLogo(messageVO.getLogo());
            MessageText saveMt = messageTextDao.save(messageText);
            return saveMt;
        } catch (Exception e) {
            LOG.error("dubbo provider save message_text error", e);
        }
        return null;
    }
}

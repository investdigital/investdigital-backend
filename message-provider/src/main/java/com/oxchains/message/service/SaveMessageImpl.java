package com.oxchains.message.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.oxchains.message.dao.MessageDao;
import com.oxchains.message.dao.MessageTextDao;
import com.oxchains.message.domain.*;
import com.oxchains.message.exception.SaveMessageException;
import com.oxchains.message.utils.SaveDbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import static com.oxchains.message.common.InformType.*;

/**
 * 保存站内信save方法，
 *
 * @author luoxuri
 * @create 2018-02-07 11:21
 **/
@Service(version = "1.0.0")
public class SaveMessageImpl implements SaveMessageService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MessageDao messageDao;
    @Resource
    private MessageTextDao messageTextDao;
    @Resource
    private PushService pushService;

    @Override
    public boolean saveMessage(MessageVO messageVO) throws SaveMessageException {
        try {
            PushData pushData = setPushData(messageVO);
            if (messageVO.getInformType().equals(NO_PUSH)) {
                // 保存数据，不给移动端推送消息
                MessageText messageText = SaveDbUtil.saveMessageText(messageTextDao, messageVO);
                SaveDbUtil.saveMessage(messageDao, messageVO, messageText);
                return true;
            }
            if (messageVO.getInformType().equals(PUSH_REG_ID)) {
                // 保存数据，推送给指定设备
                boolean result = pushService.pushToRegistrationId(pushData);
                if (result) {
                    MessageText messageText = SaveDbUtil.saveMessageText(messageTextDao, messageVO);
                    SaveDbUtil.saveMessage(messageDao, messageVO, messageText);
                    return true;
                }
            }
            if (messageVO.getInformType().equals(PUSH_ALL_PHONE)) {
                // 保存数据，推送给所有移动端数据
                boolean result = pushService.pushToAndroidAndIos(pushData);
                if (result) {
                    MessageText messageText = SaveDbUtil.saveMessageText(messageTextDao, messageVO);
                    SaveDbUtil.saveMessage(messageDao, messageVO, messageText);
                    return true;
                }
            }
            if (messageVO.getInformType().equals(PUSH_ANRDOID)) {
                // 保存数据，推送给所有android端数据
                boolean result = pushService.pushToAllAndroid(pushData);
                if (result) {
                    MessageText messageText = SaveDbUtil.saveMessageText(messageTextDao, messageVO);
                    SaveDbUtil.saveMessage(messageDao, messageVO, messageText);
                    return true;
                }
            }
            if (messageVO.getInformType().equals(PUSH_IOS)) {
                // 保存数据，推送给所有ios端数据
                boolean result = pushService.pushToAllIos(pushData);
                if (result) {
                    MessageText messageText = SaveDbUtil.saveMessageText(messageTextDao, messageVO);
                    SaveDbUtil.saveMessage(messageDao, messageVO, messageText);
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error("save message error", e);
        }
        return false;
    }

    private PushData setPushData(MessageVO messageVO) {
        PushData pushData = new PushData();
        pushData.setRegistrationId(messageVO.getRegistrationId());
        pushData.setNotificationTitle(messageVO.getNotificationTitle());
        pushData.setNotificationSummary(messageVO.getNotificationSummary());
        pushData.setMsgTitle(messageVO.getMsgTitle());
        pushData.setMsgContent(messageVO.getMsgContent());
        pushData.setExtrasParam(messageVO.getExtrasParam());
        return pushData;
    }
/*
    private void saveMessage(MessageVO messageVO, MessageText messageText) {
        Message message = new Message();
        message.setMessageTextId(messageText.getId());
        message.setMessageType(messageVO.getMessageType());
        message.setReceiverId(messageVO.getReceiverId());
        message.setReadStatus(2);
        message.setLogo(messageVO.getLogo());
        message.setReceiverId(messageVO.getReceiverId());
        messageDao.save(message);
    }

    private MessageText saveMessageText(MessageVO messageVO) {
        MessageText messageText = new MessageText();
        messageText.setMessageType(messageVO.getMessageType());
        messageText.setMessage(messageVO.getMessage());
        messageText.setSenderId(messageVO.getSendId());
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        messageText.setPostDate(currentTime);
        messageText.setLogo(messageVO.getLogo());
        MessageText saveMt = messageTextDao.save(messageText);
        return saveMt;
    }*/
}

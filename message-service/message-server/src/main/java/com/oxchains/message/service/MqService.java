package com.oxchains.message.service;

import com.oxchains.message.common.MapCacheManager;
import com.oxchains.message.dao.MessageDao;
import com.oxchains.message.dao.MessageTextDao;
import com.oxchains.message.domain.Message;
import com.oxchains.message.domain.MessageText;
import com.oxchains.message.domain.MessageVO;
import com.oxchains.message.domain.PushData;
import com.oxchains.message.grpc.lib.Request;
import com.oxchains.message.grpc.lib.Response;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.oxchains.message.common.InformType.*;
import static com.oxchains.message.common.InformType.PUSH_IOS;
import static com.oxchains.message.common.MessageReadStatus.READ;

/**
 * @author luoxuri
 * @create 2018-03-05 13:30
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class MqService {

    private final Logger LOG = LoggerFactory.getLogger(MqService.class);

    @Resource
    private MessageDao messageDao;
    @Resource
    private MessageTextDao messageTextDao;
    @Resource
    private PushService pushService;
    @Resource
    private MapCacheManager mapCacheManager;

    public void save(MessageVO messageVO) {
        PushData pushData = setPushData(messageVO);

        if (messageVO.getInformType().equals(NO_PUSH)) {
            // 保存数据，不给移动端推送消息
            saveDb(messageVO);
        }

        if (messageVO.getInformType().equals(PUSH_REG_ID)) {
            // 保存数据，推送给指定设备
            boolean success = pushService.pushToRegistrationId(pushData);
            if (success) {
                saveDb(messageVO);
            }
        }

        if (messageVO.getInformType().equals(PUSH_ALL_PHONE)) {
            // 保存数据，推送给所有移动端数据
            boolean success = pushService.pushToAndroidAndIos(pushData);
            if (success) {
                saveDb(messageVO);
            }
        }
        if (messageVO.getInformType().equals(PUSH_ANRDOID)) {
            // 保存数据，推送给所有android端数据
            boolean success = pushService.pushToAllAndroid(pushData);
            if (success) {
                saveDb(messageVO);
            }
        }
        if (messageVO.getInformType().equals(PUSH_IOS)) {
            // 保存数据，推送给所有ios端数据
            boolean success = pushService.pushToAllIos(pushData);
            if (success) {
                saveDb(messageVO);
            }
        }

        // 有新的消息添加到数据库，更改缓存中是否有新消息的值(和未读消息关联)
        int[] allUnReadArr = mapCacheManager.getCountMap().get(messageVO.getReceiverId());
        if (allUnReadArr != null) {
            allUnReadArr[4] = 1;
            mapCacheManager.getCountMap().put(messageVO.getReceiverId(), allUnReadArr);
        }

    }

    private void saveDb(MessageVO messageVO) {
        MessageText messageText = saveMessageText(messageTextDao, messageVO);
        saveMessage(messageDao, messageVO, messageText);
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

    public void saveMessage(MessageDao messageDao, MessageVO messageVO, MessageText messageText) {
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
            LOG.error("save db-table:message error", e);
        }
    }

    public MessageText saveMessageText(MessageTextDao messageTextDao, MessageVO messageVO) {
        try {
            MessageText messageText = new MessageText();
            messageText.setMessageType(messageVO.getMessageType());
            messageText.setMessage(messageVO.getMessage());
            messageText.setSenderId(messageVO.getSendId());
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            messageText.setPostDate(currentTime);
            messageText.setLogo(messageVO.getLogo());
            messageText.setOrderId(messageVO.getOrderId());
            MessageText saveMt = messageTextDao.save(messageText);
            return saveMt;
        } catch (Exception e) {
            LOG.error("save db-table:message_text error", e);
        }
        return null;
    }

}

package com.oxchains.message.service;

import com.oxchains.message.domain.PushData;
import com.oxchains.message.utils.JPushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author luoxuri
 * @create 2018-02-01 14:31
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class PushService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     * 推送给指定设备
     * @param pushData
     * @return
     */
    public boolean pushToRegistrationId(PushData pushData) {
        try {
            int result = JPushUtils.sendToRegistrationId(
                    pushData.getRegistrationId(),
                    pushData.getNotificationTitle(),
                    pushData.getNotificationSummary(),
                    pushData.getMsgTitle(),
                    pushData.getMsgContent(),
                    pushData.getExtrasParam());
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            LOG.error("push to registrationId error", e);
        }
        return false;
    }

    /**
     * 推送给所有Android用户
     * @param pushData
     * @return
     */
    public boolean pushToAllAndroid(PushData pushData) {
        try {
            int result = JPushUtils.sendToAllAndroid(
                    pushData.getNotificationTitle(),
                    pushData.getNotificationSummary(),
                    pushData.getMsgTitle(),
                    pushData.getMsgContent(),
                    pushData.getExtrasParam());
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            LOG.error("push to all android error", e);
        }
        return false;
    }

    /**
     * 推送给所有Ios用户
     * @param pushData
     * @return
     */
    public boolean pushToAllIos(PushData pushData) {
        try {
            int result = JPushUtils.sendToAllIos(
                    pushData.getNotificationTitle(),
                    pushData.getMsgTitle(),
                    pushData.getMsgContent(),
                    pushData.getExtrasParam());
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            LOG.error("push to all ios error", e);
        }
        return false;
    }

    /**
     * 推送给所有Android和Ios用户
     * @param pushData
     * @return
     */
    public boolean pushToAndroidAndIos(PushData pushData) {
        try {
            int result = JPushUtils.sendToAndroidAndIos(
                    pushData.getNotificationTitle(),
                    pushData.getMsgTitle(),
                    pushData.getMsgContent(),
                    pushData.getExtrasParam());
            if (result == 1) {
                return true;
            }
        } catch (Exception e) {
            LOG.error("push to android and ios error", e);
        }
        return false;
    }

}

package com.oxchains.message.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author luoxuri
 * @create 2018-02-01 17:19
 **/
@Entity
@Table(name = "push_message")
public class PushData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String registrationId;      // 设备标识
    private String notificationTitle;   // 通知内容标题
    private String notificationSummary; // 通知内容摘要
    private String msgTitle;            // 消息内容标题
    private String msgContent;          // 消息内容
    private String extrasParam;         // 扩展字段

    public PushData() {
    }

    public PushData(String registrationId, String notificationTitle, String notificationSummary, String msgTitle, String msgContent, String extrasParam) {
        this.registrationId = registrationId;
        this.notificationTitle = notificationTitle;
        this.notificationSummary = notificationSummary;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
        this.extrasParam = extrasParam;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationSummary() {
        return notificationSummary;
    }

    public void setNotificationSummary(String notificationSummary) {
        this.notificationSummary = notificationSummary;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getExtrasParam() {
        return extrasParam;
    }

    public void setExtrasParam(String extrasParam) {
        this.extrasParam = extrasParam;
    }
}

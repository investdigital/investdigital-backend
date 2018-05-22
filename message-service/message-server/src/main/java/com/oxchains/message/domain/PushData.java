package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author luoxuri
 * @create 2018-02-01 17:19
 **/
@Data
public class PushData implements Serializable {

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

}

package com.oxchains.message.domain;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-02-07 11:54
 **/
@Data
public class MessageVO {

    // 这是要保存到数据库的字段

    private Long informType;            // 消息通知类型
    private Integer messageType;        // 消息类型
    private Long receiverId;            // 接受者Id
    private String message;             // 消息主体
    private String orderId;             // 订单ID
    private Long sendId;                // 发送者Id，如果是系统就是0
    private String logo;                // 项目标识

    // =====================================
    // 以下是如果要同时通知移动端需要的

    private String registrationId;      // 设备标识
    private String notificationTitle;   // 通知内容标题
    private String notificationSummary; // 通知内容摘要
    private String msgTitle;            // 消息内容标题
    private String msgContent;          // 消息内容
    private String extrasParam;         // 扩展字段

    public MessageVO() {
    }

    public MessageVO(Long informType, Integer messageType, Long receiverId,
                     String message, String orderId, Long sendId, String logo,
                     String registrationId, String notificationTitle,
                     String notificationSummary, String msgTitle, String msgContent, String extrasParam) {
        this.informType = informType;
        this.messageType = messageType;
        this.receiverId = receiverId;
        this.message = message;
        this.orderId = orderId;
        this.sendId = sendId;
        this.logo = logo;
        this.registrationId = registrationId;
        this.notificationTitle = notificationTitle;
        this.notificationSummary = notificationSummary;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
        this.extrasParam = extrasParam;
    }
}

package com.oxchains.message.rest.dto;

import com.oxchains.message.domain.Message;
import lombok.Data;

import java.io.Serializable;

/**
 * 站内信DTO
 *
 * @author luoxuri
 * @create 2017-11-07 10:30
 **/
@Data
public class MessageDTO implements Serializable {
    private Long messageId;

    private Long receiverId;     // 接受者编号

    private Long messageTextId; // 站内信编号

    private Integer readStatus; // 站内信的查看状态 1.未读 2.已读 3.删除

    private Long senderId;    // 发送者编号

    private String content; // 站内信的内容

    private Integer messageType;   // 信息类型 1.global(系统消息) 2.public(公告) 3.private(私信)

    private Long userGroup;     // 用户组ID 1.admin 2.仲裁 3.客服 4.普通用户

    private String postDate;   // 站内信发送时间

    private String orderId;     // 订单id

    public MessageDTO(Message message) {
        receiverId = message.getReceiverId();
        messageTextId = message.getMessageTextId();
        readStatus = message.getReadStatus();
        senderId = message.getMessageText().getSenderId();
        content = message.getMessageText().getMessage();
        messageType = message.getMessageType();
        userGroup = message.getMessageText().getUserGroup();
        postDate = message.getMessageText().getPostDate();
        orderId = message.getMessageText().getOrderId();
        messageId = message.getId();
    }

    public MessageDTO() {
    }
}

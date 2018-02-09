package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 站内信文本
 *
 * @author luoxuri
 * @create 2017-11-06 14:56
 **/
@Entity
@Data
@Table(name = "message_text")
public class MessageText implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;        // 编号

    private Long senderId;    // 发送者编号

    private String message; // 站内信的内容

    private Integer messageType;   // 信息类型 1.global(系统消息) 2.public(公告) 3.private(私信)

    private Long userGroup;     // 用户组ID 1.admin 2.仲裁 3.客服 4.普通用户

    private String postDate;   // 站内信发送时间

    private String orderId;     // 订单id

    private String logo;        // 项目请求标识

    @Transient
    private Long partnerId;

    @Transient
    private String friendUsername; // 交易伙伴

    @Transient
    private String imageName;   // 头像名

    public MessageText(Long senderId, String message, Integer messageType, Long userGroup, String postDate, String orderId, String logo) {
        this.senderId = senderId;
        this.message = message;
        this.messageType = messageType;
        this.userGroup = userGroup;
        this.postDate = postDate;
        this.orderId = orderId;
        this.logo = logo;
    }

    public MessageText() {
    }
}

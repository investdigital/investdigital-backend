package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 站内信
 *
 * @author luoxuri
 * @create 2017-11-06 14:42
 **/
@Entity
@Data
@Table(name = "message")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;        // 编号

    private Long receiverId;     // 接受者编号

    private Long messageTextId; // 站内信编号

    private Integer readStatus; // 站内信的查看状态 1.未读 2.已读 3.删除

    private Integer messageType;   // 信息类型 1.global(系统消息) 2.public(公告) 3.private(私信)

    private Integer del = 0;    // 逻辑删除标识，默认0：未删除，1：删除

    @Transient
    private MessageText messageText;

    private String logo; // 用于辨识那个项目的请求

    public Message(Long receiverId, Long messageTextId, Integer readStatus, Integer messageType, String logo) {
        this.receiverId = receiverId;
        this.messageTextId = messageTextId;
        this.readStatus = readStatus;
        this.messageType = messageType;
        this.logo = logo;
    }

    public Message() {
    }
}

package com.oxchains.message.common;

/**
 * 消息类型
 * 1：私信
 * 2：公共消息（公告信息）
 * 3：系统消息
 * @author luoxuri
 * @create 2018-02-05 17:17
 **/
public interface MessageType {
    Integer PRIVATE_LETTET = 1;
    Integer PUBLIC_LETTET = 2;
    Integer GLOBAL_LETTET = 3;
}

package com.oxchains.message.common;

/**
 * 消息阅读状态
 * 1：未读
 * 2：已读
 * 3：删除
 * @author luoxuri
 * @create 2018-02-05 17:35
 **/
public interface MessageReadStatus {
    Integer UN_READ = 1;
    Integer READ = 2;
    Integer DELETE = 3;
}

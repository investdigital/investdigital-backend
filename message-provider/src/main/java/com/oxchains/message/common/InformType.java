package com.oxchains.message.common;

/**
 * @author luoxuri
 * @create 2018-02-07 17:57
 **/
public interface InformType {
    /**
     * 保存数据，不给移动端推动消息
     */
    Long NO_PUSH = 1L;

    /**
     * 保存数据，推送给指定设备
     */
    Long PUSH_REG_ID = 2L;

    /**
     * 保存数据，推送给所有移动端数据
     */
    Long PUSH_ALL_PHONE = 3L;

    /**
     * 保存数据，推送给所有android端数据
     */
    Long PUSH_ANRDOID = 4L;

    /**
     * 保存数据，推送给所有ios端数据
     */
    Long PUSH_IOS = 5L;
}

package com.oxchains.message.rest.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author luoxuri
 * @create 2017-12-05 11:39
 **/
@Data
public class UnReadSizeDTO implements Serializable {

    /**
     * 所有未读信息数量
     */
    private Integer allUnRead;

    /**
     * 公告未读信息数量
     */
    private Integer noticeUnRead;

    /**
     * 系统未读信息数量
     */
    private Integer globalUnRead;

    /**
     * 私信未读信息数量
     */
    private Integer privateUnRead;
}

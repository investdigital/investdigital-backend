package com.oxchains.message.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 轮询查未读使用这个实体
 * @author luoxuri
 * @create 2018-02-05 14:45
 **/
@Data
public class PollData implements Serializable {

    private String logo;
    private Long userId;
    private Integer pageNum;
    private Integer pageSize;
    private Integer tip;
    private Long userGroup;

    public PollData() {
    }

    public PollData(String logo, Long userId, Integer pageNum, Integer pageSize, Integer tip, Long userGroup) {
        this.logo = logo;
        this.userId = userId;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.tip = tip;
        this.userGroup = userGroup;
    }
}

package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-10-30 19:00
 * @nameUserTxDetail
 * @desc:
 */
@Entity
@Table(name = "user_tx_detail")
@Data
public class UserTxDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Integer txNum;     //交易次数
    private Integer goodDesc;   //好评次数
    private Integer badDesc;    //差评次数
    private String firstBuyTime;  //第一次购买时间
    private Integer believeNum;    // 信任次数

    private Double successCount;

    public UserTxDetail(){}

    public UserTxDetail(boolean init){
        if(init){
            this.txNum = 0;
            this.goodDesc = 0;
            this.badDesc = 0;
            this.believeNum = 0;
            this.successCount = 0.0d;
        }
    }
    /**
     * 交总量
     */
    @Transient
    private Double buyAmount;

    @Transient
    private Double sellAmount;

    public void setFirstBuyTime(String firstBuyTime) {
        if(null == firstBuyTime){
            this.firstBuyTime = "暂未交易";
        }
    }

    public String getFirstBuyTime() {
        return firstBuyTime==null?"暂未交易":firstBuyTime;
    }
}

package com.oxchains.message.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by huohuo on 2017/10/23.
 * @author huohuo
 */

@Entity
@Table(name = "tbl_biz_orders")
@Data
public class Orders implements Serializable{
    @Id
    @Column(name = "id",length = 255)
    private String id;         //订单编号
    private BigDecimal money;  //订单金额
    private String createTime;  //下单时间
    private String finishTime;//完成时间
    private BigDecimal amount; //交易数量
    private Long paymentId; //支付方式编号  1 现金 2 转账 3 支付宝 4 微信 5 Apple Pay
    private Long vcurrencyId; //数字货币币种 1 比特币
    private Long currencyId;  //纸币币种    1  人民币 2  美元
    private Long buyerId;     // 买家id
    private Long sellerId;    //卖家id
    private Long orderStatus; // 订单状态    1  待确认 2 代付款  3 待收货 4  待评价 5 完成 6  已取消 7等待卖家退款 8 仲裁中
    private Long noticeId;
    private int arbitrate;   //是否在仲裁中 默认 0： 不在仲裁中 1： 在仲裁中 2:仲裁结束
    private String uri;
    private Integer K;
    private Integer N;

    public Orders(String id, BigDecimal money, String createTime, BigDecimal amount, Long paymentId, Long vcurrencyId, Long currencyId, Long buyerId, Long sellerId, Long orderStatus, Long noticeId, int arbitrate, Integer K, Integer N) {
        this.id = id;
        this.money = money;
        this.createTime = createTime;
        this.amount = amount;
        this.paymentId = paymentId;
        this.vcurrencyId = vcurrencyId;
        this.currencyId = currencyId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.orderStatus = orderStatus;
        this.noticeId = noticeId;
        this.arbitrate = arbitrate;
        this.K = K;
        this.N = N;
    }

    public Orders() {
    }


}

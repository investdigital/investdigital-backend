package info.investdigital.AssetsAPI.huobi.entity.order;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-03-14 17:01
 **/
@Data
public class OrderData {


    private String id;              // 订单ID
    private String symbol;          // 交易对
    private String accountid;       // 账户ID
    private String amount;          // 订单数量
    private String price;           // 订单价格
    private String createdat;       // 创建订单时间
    private String type;            // 订单类型
    private String fieldamount;     // 已成交数量
    private String fieldcashamount; // 已成交总金额
    private String fieldfees;       // 已成交书续费（买入为币，卖出为钱）
    private String finishedat;      // 最后成交时间
    private String source;          // 订单时间
    private String state;           // 订单状态
    private String canceledat;      // 订单撤销时间

}

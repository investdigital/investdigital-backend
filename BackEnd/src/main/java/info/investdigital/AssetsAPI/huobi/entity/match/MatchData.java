package info.investdigital.AssetsAPI.huobi.entity.match;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-03-14 18:24
 **/
@Data
public class MatchData {
    private String id;          // 订单成交记录Id
    private String orderid;     // 订单id
    private String matchid;     // 撮合id
    private String symbol;
    private String type;        // 订单类型，buy-market:市价买，sell-market:市价卖，buy-limit：限价买
    private String source;      // 订单来源：api
    private String price;       // 成交价格
    private String filledamount;// 成交数量
    private String filledfees;  // 成交手续费
    private String filledpoints;//
    private String createdat;   // 成交时间
}

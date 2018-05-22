package info.investdigital.AssetsAPI.huobi.entity.support;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-03-13 11:20
 **/
@Data
public class Tick {
    private Long id;        // 消息id
    private String amount;  // 24小时成交量
    private String open;    // 前推24小时成交价
    private String close;   // 当前成交价
    private String high;    // 近24小时最高价
    private String count;   // 近24小时累积成交数
    private String low;     // 近24小时最低价
    private String version; //
    private String vol;     // 近24小时累积成交额, 即 sum(每一笔成交价 * 该笔的成交量)
}

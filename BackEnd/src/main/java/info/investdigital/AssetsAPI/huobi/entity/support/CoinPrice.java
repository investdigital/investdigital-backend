package info.investdigital.AssetsAPI.huobi.entity.support;

import lombok.Data;


/**
 * @author luoxuri
 * @create 2018-03-13 11:18
 **/
@Data
public class CoinPrice {
    private String status;  // 状态ok、error
    private String ch;      // 数据所属channel
    private String ts;      // 响应生成时间点，单位：毫秒
    private Tick tick;      // Detail数据
}

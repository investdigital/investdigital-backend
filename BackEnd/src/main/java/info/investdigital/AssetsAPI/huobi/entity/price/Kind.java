package info.investdigital.AssetsAPI.huobi.entity.price;

import lombok.Data;

/**
 * @author luoxuri
 * @create 2018-03-13 10:12
 **/
@Data
public class Kind {
    private String currency;    // 币种
    private String type;        // trade:交易余额，frozen：冻结余额
    private String balance;     // 余额
}

package info.investdigital.AssetsAPI.binance.entity;

import lombok.Data;

/**
 * @author anonymity
 * @create 2018-04-20 18:18
 **/
@Data
public class DepositWithDraw {
    /**
     * 将提币实体和充币实体整合到一个实体类中，
     */
    private String id;
    private String amount;
    private String address;
    private String asset;
    private String txId;
    private String applyTime;
    private Integer status;

    private String insertTime;
    private String addressTag;
}

package info.investdigital.AssetsAPI.huobi.entity.depositwithdraw;

import lombok.Data;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-04-12 10:53
 **/
@Data
public class DepositWithdraw {
    private String status;
    private List<DepositWithdrawData> data;
}

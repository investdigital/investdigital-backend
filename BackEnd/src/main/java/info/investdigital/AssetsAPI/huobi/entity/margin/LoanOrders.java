package info.investdigital.AssetsAPI.huobi.entity.margin;

import lombok.Data;

/**
 * @author anonymity
 * @create 2018-05-06 17:50
 **/
@Data
public class LoanOrders {
    private String loanbalance;
    private String interestbalance;
    private String interestrate;
    private String loanamount;
    private String accruedat;
    private String interestamount;
    private String symbol;
    private String currency;
    private String id;
    private String state;
    private String accountid;
    private String userid;
    private String createdat;
}

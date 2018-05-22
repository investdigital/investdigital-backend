package info.investdigital.AssetsAPI.huobi.entity.balance;

/**
 * @author luoxuri
 * @create 2018-03-13 10:07
 **/
@lombok.Data
public class AccountData {

    private Long id;        // accountId
    private String type;    // spot：现货账户
    private String state;   // working:正常，lock:账户被锁定
    private String subtype;
}

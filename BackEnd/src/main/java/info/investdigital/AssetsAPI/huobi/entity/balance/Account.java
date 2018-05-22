package info.investdigital.AssetsAPI.huobi.entity.balance;

import lombok.Data;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-13 19:05
 **/
@Data
public class Account {
    private String status;
    private List<AccountData> data;

    private String errmsg;
}

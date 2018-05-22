package info.investdigital.AssetsAPI.huobi.entity.price;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-13 10:07
 **/
@lombok.Data
public class Data {

    private Long id;        // 账户id
    private String type;    // spot：现货账户
    private String state;   // 账户状态：working：正常，lock：锁定
    private List list;      // 子账户数组
}

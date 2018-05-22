package info.investdigital.entity.asset;

import lombok.Data;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-07 15:18
 **/
@Data
public class Currencys {
    private String status;
    private List<String> data; // 货币数组
}

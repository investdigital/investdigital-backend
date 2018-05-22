package info.investdigital.AssetsAPI.huobi.entity.support;

import lombok.Data;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-13 11:06
 **/
@Data
public class CoinKind {
    private String status;  // 状态ok
    private List data;      // 数据数组
}

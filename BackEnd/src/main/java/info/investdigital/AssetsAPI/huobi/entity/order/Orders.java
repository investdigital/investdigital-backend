package info.investdigital.AssetsAPI.huobi.entity.order;

import lombok.Data;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-14 16:53
 **/
@Data
public class Orders {
    private String status;          // 状态ok
    private List<OrderData> data;   // 数据数组
}

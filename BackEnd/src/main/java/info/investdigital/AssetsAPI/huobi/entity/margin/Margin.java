package info.investdigital.AssetsAPI.huobi.entity.margin;

import lombok.Data;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-06 17:49
 **/
@Data
public class Margin {
    private String status;
    private List<LoanOrders> data;
}

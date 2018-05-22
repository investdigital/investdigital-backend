package info.investdigital.AssetsAPI.huobi.entity.merged;

import info.investdigital.AssetsAPI.huobi.entity.support.Tick;
import lombok.Data;

/**
 * @author anonymity
 * @create 2018-05-06 8:58
 **/
@Data
public class Merged {
    private String status;
    private String ch;
    private Long ts;
    private Tick tick;
}

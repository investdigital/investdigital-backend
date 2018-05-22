package info.investdigital.AssetsAPI.huobi.entity.match;

import lombok.Data;

import java.util.List;

/**
 * @author luoxuri
 * @create 2018-03-14 18:25
 **/
@Data
public class Match {
    private String status;          // 状态ok
    private List<MatchData> data;   // 数据数组
}

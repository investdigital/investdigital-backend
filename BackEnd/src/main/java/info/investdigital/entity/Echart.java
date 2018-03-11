package info.investdigital.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ccl
 * @time 2017-12-16 16:45
 * @name Echart
 * @desc:
 */
@Data
public class Echart {
    private List<String> xAxis;
    private List<Map<String,Object>> yAxis;
}

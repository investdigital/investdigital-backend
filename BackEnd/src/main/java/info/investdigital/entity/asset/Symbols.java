package info.investdigital.entity.asset;

import lombok.Data;

import java.util.List;

/**
 * @author anonymity
 * @create 2018-05-04 11:13
 **/
@Data
public class Symbols {

    private String status;
    private List<HuobiSymbol> data;
}

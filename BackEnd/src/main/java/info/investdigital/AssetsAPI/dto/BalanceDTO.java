package info.investdigital.AssetsAPI.dto;

import lombok.Data;

import java.util.List;

/**
 * 总资产和各个有货币DTO
 *
 * @author anonymity
 * @create 2018-04-16 10:17
 **/
@Data
public class BalanceDTO <T> {
    private String[] totalAsset;
    private List<T> notEmptyBalance;
}

package info.investdigital.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author oxchains
 * @time 2017-12-15 13:42
 * @name FundInfo
 * @desc:
 */
@Entity
@Table(name = "fund_info")
@Data
public class FundInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long fundId;
    private Float perFee;
    private Float volume;
    private Float price;
    private Float purchaseAmount;
    private Long deadline;
    private Integer closePeriod;
    private String openday;
    private String custody_user;
    private String brokerCompany;
    private String investAdviserCompany;
    private String investAdviserRegion;

}

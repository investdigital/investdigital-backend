package info.investdigital.entity;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-12-13 17:03
 * @name FundTag
 * @desc:
 */
@Entity
@Table(name = "fund_of_tag")
public class FundOfTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long fundId;
    private Long tagId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}

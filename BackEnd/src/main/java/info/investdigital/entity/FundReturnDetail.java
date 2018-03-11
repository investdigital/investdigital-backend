package info.investdigital.entity;

import info.investdigital.common.DateUtil;

import javax.persistence.*;

/**
 * @author ccl
 * @time 2017-12-14 11:35
 * @name FundReturnDetail
 * @desc:
 */
@Entity
@Table(name = "fund_return_detail")
public class FundReturnDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long fundId;
    private String fundCode;
    @Column(name = "date_")
    private Long date;

    @Transient
    private String dateStr;

    private Float fundReturn;
    private Float csi300;
    private Float shcompositeIndex;

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

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Float getFundReturn() {
        return fundReturn;
    }

    public void setFundReturn(Float fundReturn) {
        this.fundReturn = fundReturn;
    }

    public Float getCsi300() {
        return csi300;
    }

    public void setCsi300(Float csi300) {
        this.csi300 = csi300;
    }

    public Float getShcompositeIndex() {
        return shcompositeIndex;
    }

    public void setShcompositeIndex(Float shcompositeIndex) {
        this.shcompositeIndex = shcompositeIndex;
    }

    @Override
    public String toString() {
        return "FundReturnDetail{" +
                "id=" + id +
                ", fundId=" + fundId +
                ", fundCode='" + fundCode + '\'' +
                ", date=" + date +
                ", fundReturn=" + fundReturn +
                ", csi300=" + csi300 +
                ", shcompositeIndex=" + shcompositeIndex +
                '}';
    }

    public String getDateStr() {
        if(date!=null){
           return DateUtil.longToString(date,"yyyy-MM-dd");
        }
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}

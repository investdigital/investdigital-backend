package info.investdigital.entity;

import info.investdigital.common.DictionaryUtils;

import java.util.List;

/**
 * @author oxchains
 * @time 2017-12-13 14:50
 * @name FundIssuanceVO
 * @desc:
 */
public class FundIssuanceVO  extends FundIssuance{
    private String assetManageScaleStr;
    private String privateIssuanceTimeStr;
    private String fundQualificationStr;
    private String privateIssuanceStageStr;
    private String fundAssociationRecordStr;
    private String productDistributionStr;
    private Integer assetManageScale;

    public String getAssetManageScaleStr() {
        return assetManageScaleStr;
    }

    public void setAssetManageScaleStr(String assetManageScaleStr) {
        this.assetManageScaleStr = assetManageScaleStr;
    }

    public String getPrivateIssuanceTimeStr() {
        return privateIssuanceTimeStr;
    }

    public void setPrivateIssuanceTimeStr(String privateIssuanceTimeStr) {
        this.privateIssuanceTimeStr = privateIssuanceTimeStr;
    }

    public String getFundQualificationStr() {
        return fundQualificationStr;
    }

    public void setFundQualificationStr(String fundQualificationStr) {
        this.fundQualificationStr = fundQualificationStr;
    }

    public String getPrivateIssuanceStageStr() {
        return privateIssuanceStageStr;
    }

    public void setPrivateIssuanceStageStr(String privateIssuanceStageStr) {
        this.privateIssuanceStageStr = privateIssuanceStageStr;
    }

    public String getFundAssociationRecordStr() {
        return fundAssociationRecordStr;
    }

    public void setFundAssociationRecordStr(String fundAssociationRecordStr) {
        this.fundAssociationRecordStr = fundAssociationRecordStr;
    }

    public String getProductDistributionStr() {
        return productDistributionStr;
    }

    public void setProductDistributionStr(String productDistributionStr) {
        this.productDistributionStr = productDistributionStr;
    }

    public FundIssuanceVO(){}
    public FundIssuanceVO(FundIssuance fundIssuance){
        List<Dictionary> dictionaries = DictionaryUtils.getDictionarys();

        setId(fundIssuance.getId());
        setUsername(fundIssuance.getUsername());
        setInvestDigitalNo(fundIssuance.getInvestDigitalNo());
        setMobilephone(fundIssuance.getMobilephone());
        setAssetManageScale(fundIssuance.getAssetManageScale());
        setPrivateIssuanceTime(fundIssuance.getPrivateIssuanceTime());
        setFundQualification(fundIssuance.getFundQualification());
        setPrivateIssuanceStage(fundIssuance.getPrivateIssuanceStage());
        setFundAssociationRecord(fundIssuance.getFundAssociationRecord());
        setProductDistribution(fundIssuance.getProductDistribution());

        for(Dictionary dictionary : dictionaries){
            for(DictionaryItem dictionaryItem : dictionary.getDictionaryItems()){
                if("assetManageScale".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getAssetManageScale()){
                        setAssetManageScaleStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
                if("privateIssuanceTime".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getPrivateIssuanceTime()){
                        setPrivateIssuanceTimeStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
                if("fundQualification".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getFundQualification()){
                        setFundQualificationStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
                if("privateIssuanceStage".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getPrivateIssuanceStage()){
                        setPrivateIssuanceStageStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
                if("fundAssociationRecord".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getFundAssociationRecord()){
                        setFundAssociationRecordStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
                if("productDistribution".equals(dictionary.getColName())){
                    if(dictionaryItem.getId().intValue() == fundIssuance.getProductDistribution()){
                        setProductDistributionStr(dictionaryItem.getDictItemName());
                        break;
                    }
                }
            }
        }
    }
}

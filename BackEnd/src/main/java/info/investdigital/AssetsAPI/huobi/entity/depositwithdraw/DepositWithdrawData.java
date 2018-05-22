package info.investdigital.AssetsAPI.huobi.entity.depositwithdraw;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author anonymity
 * @create 2018-04-12 10:55
 **/
@Data
public class DepositWithdrawData {
    private Long id;
    private String type;        // 类型：deposit、withdraw
    private String currency;    // 币种
    private String txhash;      // 交易hash
    private BigDecimal amount;  // 个数
    private String address;     // 地址
    private String addresstag;  // 地址标签
    private BigDecimal fee;     // 手续费
    private String state;       // 虚拟币提现状态
    private Long createdat;     // 发起时间
    private Long updatedat;     // 最后更新时间
    /**
     * 虚拟币提现状态定义
     *
     * submitted:已提交
     * reexamine：审核中
     * canceled：已撤销
     * pass：审批通过
     * reject：审批拒绝
     * pre-transfer:处理中
     * wallet-transfer：已汇出
     * wallet-reject：钱包拒绝
     * confirmed:区块已确认
     * confirm-error:区块确认错误
     * repealed:已撤销
     */
    /**
     * 虚拟币充值状态定义
     *
     * unknown：状态未知
     * confirming：确认中
     * confrimed:确认中
     * safe：已完成
     * orphan：待确认
     */
}

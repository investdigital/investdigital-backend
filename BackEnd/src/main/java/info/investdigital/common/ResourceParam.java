package info.investdigital.common;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: huohuo
 * Created in 14:04  2018/4/2.
 */
@Component
@Data
public class ResourceParam {
    //所有基金 明星基金 的 funddetalVo
    @Value("${redis.fund.cache.fundDetalVo.hashkey}")
    private String fundDetalVoHashKey;
    //基金详情的
    @Value("${redis.fund.cache.fundInfoVo.hashkey}")
    private String fundInfoVoHashKey;
    //基金指数数据 hash
    @Value("${redis.fund.cache.eth.index.hashkey}")
    private String ethIndexHashKey;
    //基金指数数据 hash
    @Value("${redis.fund.cache.fund.index.hashkey}")
    private String fundIndexHashKey;
    //几条精英策略的funddetail
    @Value("${redis.fund.cache.starFund.key}")
    private String starFundKey;
    //几条精英策略的funddetail
    @Value("${redis.fund.participating.fund}")
    private String participatingFund;
    @Value("${redis.fund.noparticipating.fund}")
    private String noParticipatingFund;
    //几条精英策略的funddetail
    @Value("${redis.fund.cache.hashkey}")
    private String fundDetailHashkey;
    @Value("${redis.fund.rankMove.hashKey}")
    private String rankMoveHashKey;
    @Value("${redis.user.cache.hashkey}")
    private String userHashKey;
    @Value("${redis.fund.return.hashkey}")
    private String fundReturnHashKey;
    @Value("${redis.fund.earing.yield.hashKey}")
    private String fundEaringYieldHashKey;
    @Value("${redis.fund.basic.hashKey}")
    private String fundBasicHashKey;
    //交易的费用
    @Value("${fund.officialFee}")
    private int officialFee;
    //基准线显示的名字
    @Value("${fund.benchmark.name}")
    private String benchmarkName;
    /*@Value("${fund.huobi.key}")
    private String huobiKey;
    @Value("${fund.bian.key}")
    private String bianKey;*/

    @Value("${fund.BTC_RATIO}")
    private Double BTC_RATIO;
    @Value("${fund.ETH_RATIO}")
    private Double ETH_RATIO;
    @Value("${fund.BCH_RATIO}")
    private Double BCH_RATIO;
    @Value("${fund.LTC_RATIO}")
    private Double LTC_RATIO;
    @Value("${fund.XRP_RATIO}")
    private Double XRP_RATIO;
    @Value("${AES.CLASS.PATH}")
    private String aesClassPath;
    @Value("${password.txt.path}")
    private String passwordPath;

    @Value("${redis.binance.market.hashKey}")
    private String binanceMarketKey;
    @Value("${redis.huobi.market.hashKey}")
    private String huobiMarketKey;
    @Value("${redis.fund.accountId.hashkey}")
    private String accountIdHashKey;
    @Value("${redis.fund.otcAccountId.hashkey}")
    private String otcAccountIdHashKey;
    @Value("${redis.fund.rank.hashKey}")
    private String rankHashKey;

    // 火币支持的所有的货币的hashKey
    @Value("${redis.huobi.currency.hashKey}")
    private String redisHuobiCurrencyHashKey;

}

package info.investdigital.AssetsAPI.huobi.common;

import java.math.BigDecimal;

/**
 * @author luoxuri
 * @create 2018-03-15 14:26
 **/
public class HuoBiConst {

    public static final BigDecimal ZERO = new BigDecimal(0);

    public static final String HUOBI_URL = "https://api.huobi.pro";
    public static String SIGN_URL = "api.huobi.pro";
    public static String SIGNATURE_METHOD = "HmacSHA256";
    public static String SIGNATURE_VERSION = "2";

    public static String USDT = "usdt";
    public static String BTC = "btc";
    public static String ETH = "eth";

    public static String OK = "ok";
    public static String ERROR = "error";

    public static String ACCOUNT_SPOT = "spot";
    public static String ACCOUNT_OTC = "otc";
    public static String ACCOUNT_MARGIN = "margin";

    public static String BTC_USDT_PRICE_URL = "/market/detail?symbol=btcusdt";
    public static String ETH_USDT_PRICE_URL = "/market/detail?symbol=ethusdt";
    public static String SUPPORT_COIN_URL = "/v1/common/currencys";
    public static String SUPPORT_COIN_TO_JOINT_PRICE_URL_PRE = "/market/detail?symbol=";
    public static String ACCOUNT_BALANCE = "/v1/account/accounts/%s/balance";
    public static String ACCOUNT_URL = "/v1/account/accounts";
    public static String MATCH_RESULT_URL = "/v1/order/matchresults";
    public static String ORDERS_URL = "/v1/order/orders";
    public static String OEDERS_BY_ID = "/v1/order/orders/%s";
    public static String WITHDRAW_URL = "/v1/dw/withdraw/api/create";
    public static String DEPOSIT_WITHDRAW = "/v1/query/deposit-withdraw";

    public static String KLINE_URL = "/market/history/kline";
    public static String SUPPORT_SYMBOL_URL = "/v1/common/symbols";
    public static String MARKET_URL = "/market/detail";

    public static String METHOD_GET = "GET";
    public static String METHOD_POST = "POST";

    public static String SIGN_MAP_SIGNATURE = "Signature";
    public static String SIGN_MAP_SYMBOL = "symbol";
    public static String SIGN_MAP_TYPES = "types";
    public static String SIGN_MAP_START_DATE = "start-date";
    public static String SIGN_MAP_END_DATE = "end-date";
    public static String SIGN_MAP_STATES = "states";
    public static String SIGN_MAP_FROM = "from";
    public static String SIGN_MAP_DIRECT = "direct";
    public static String SIGN_MAP_SIZE = "size";
    public static String SIGN_MAP_ADDRESS = "address";
    public static String SIGN_MAP_AMOUNT = "amount";
    public static String SIGN_MAP_CURRENCY = "currency";
    public static String SIGN_MAP_FEE = "fee";
    public static String SIGN_MAP_ADDR_TAG = "addrTag";
    public static String SIGN_MAP_TYPE = "type";


    public static String SIGN_MAP_ACCESS_KEY = "AccessKeyId";
    public static String SIGN_MAP_SIGNATURE_METHOD = "SignatureMethod";
    public static String SIGN_MAP_SIGNATURE_VERSION = "SignatureVersion";
    public static String SIGN_MAP_TIMESTAMP = "Timestamp";

    public static String SUCCESS = "操作成功";
    public static String FAILED = "操作失败";

}

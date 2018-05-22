package info.investdigital.AssetsAPI.binance.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luoxuri
 * @create 2018-03-28 17:05
 **/
public class BinanceConst {

    public final String[] ALL_COIN = {"BTC", "LTC", "ETH", "BNC", "ICO", "NEO", "BNB", "123", "456", "QTUM", "EOS", "SNT", "BNT", "GAS", "BCC", "BTM", "USDT", "HCC", "HSR", "OAX", "DNT", "MCO", "ICN", "ELC", "PAY", "ZRX", "OMG", "WTC", "LRX", "YOYO", "LRC", "LLT", "TRX", "FID", "SNGLS", "STRAT", "BQX", "FUN", "KNC", "CDT", "XVG", "IOTA", "SNM", "LINK", "CVC", "TNT", "REP", "CTR", "MDA", "MTL", "SALT", "NULS", "SUB", "STX", "MTH", "CAT", "ADX", "PIX", "ETC", "ENG", "ZEC", "AST", "1ST", "GNT", "DGD", "BAT", "DASH", "POWR", "BTG", "REQ", "XMR", "EVX", "VIB", "ENJ", "VEN", "CAG", "EDG", "ARK", "XRP", "MOD", "AVT", "STORJ", "KMD", "RCN", "EDO", "QASH", "SAN", "DATA", "DLT", "GUP", "MCAP", "MANA", "PPT", "OTN", "CFD", "RDN", "GXS", "AMB", "ARN", "BCPT", "CND", "GVT", "POE", "ALIS", "BTS", "FUEL", "XZC", "QSP", "LSK", "BCD", "TNB", "GRX", "STAR", "ADA", "LEND", "IFT", "KICK", "UKG", "VOISE", "XLM", "CMT", "WAVES", "WABI", "SBTC", "BCX", "GTO", "ETF", "ICX", "OST", "ELF", "AION", "WINGS", "BRD", "NEBL", "NAV", "VIBE", "LUN", "TRIG", "APPC", "CHAT", "RLC", "INS", "PIVX", "IOST", "STEEM", "NANO", "AE", "VIA", "BLZ", "SYS", "RPX", "NCASH", "POA", "ONT", "ZIL", "STORM", "XEM", "WAN", "WPR", "QLC"};

//    public static String[] SUPPORT_COIN_USDT = {"BTC", "ETH", "BNB", "NEO", "LTC", "QTUM", "BCC"};
//    public static String[] SUPPORT_COIN_ETH = {"EOS", "ONT", "TRX", "NEO", "XVG", "ADA", "ICX", "XRP", "BNB", "POA", "QLC", "STORM", "WAN", "ZRX", "NANO", "QTUM", "LTC", "DGD", "NCASH", "IOST", "XLM", "BAT", "VEN", "SYS", "ELF", "ZIL", "OMG", "IOTA", "WTC", "CMT", "ETC", "AION", "PIVX", "REQ", "LEND", "BLZ", "BNT", "BCC", "SUB", "POWR", "DASH", "FUN", "AE", "DNT", "GTO", "LSK", "ENJ", "BQX", "XMR", "NEBL", "POE", "XEM", "CTR", "LRC", "YOYO", "KNC", "GVT", "TNT", "FUEL", "APPC", "PPT", "MTL", "LINK", "ENG", "QSP", "OST", "BCPT", "NULS", "SNT", "VIB", "HSR", "RCN", "RDN", "OAX", "LUN", "ZEC", "MTH", "BTG", "GXS", "SALT", "VIBE", "TRIG", "ICN", "STRAT", "MOD", "BTS", "CHAT", "CND", "BRD", "MANA", "RPX", "WABI", "AMB", "CDT", "DLT", "WAVES", "STEEM", "MDA", "TNB", "BCD", "ARK", "SNGLS", "AST", "ARN", "EVX", "MCO", "RLC", "XZC", "INS", "KMD", "ADX", "EDO", "VIA", "NAV", "WINGS", "SNM", "STORJ"};
//    public static String[] SUPPORT_COIN_BTC = {"ONT", "TRX", "ETH", "EOS", "XVG", "ICX", "BNB", "STORM", "QLC", "NEO", "XRP", "ADA", "ZRX", "LTC", "IOST", "NANO", "DGD", "XLM", "NEBL", "NCASH", "IOTA", "QTUM", "BCC", "GVT", "ENJ", "SYS", "AION", "BAT", "MTL", "WAN", "DASH", "SUB", "NULS", "ETC", "OMG", "POWR", "XMR", "BQX", "LINK", "WAVES", "POA", "PIVX", "VEN", "ELF", "EDO", "STRAT", "SNT", "CTR", "TRIG", "INS", "BCPT", "WTC", "LEND", "MCO", "TNT", "ENG", "SALT", "LSK", "CMT", "ZIL", "FUN", "BTG", "RCN", "POE", "DNT", "YOYO", "AE", "XEM", "AST", "REQ", "PPT", "LUN", "GTO", "BLZ", "APPC", "LRC", "BNT", "VIBE", "VIB", "HSR", "ZEC", "OST", "FUEL", "TNB", "RPX", "BCD", "QSP", "ICN", "CHAT", "OAX", "MTH", "CND", "GXS", "KNC", "MOD", "BRD", "BTS", "ADX", "WABI", "ARN", "GAS", "DLT", "EVX", "MDA", "CDT", "RDN", "MANA", "KMD", "AMB", "STEEM", "XZC", "WINGS", "ARK", "SNGLS", "STORJ", "NAV", "VIA", "SNM", "RLC"};

    public static Map<String, String> BA_MAP = new ConcurrentHashMap<>();

    // 交易员大赛开始之前没有交易对的货币map,key:交易对，value：交易对
//    public static Map<String, String> BA_NO_VALUE_MAP = new HashMap<>();
    /**
     * 币安支持的所有货币
     */
    public static String[] ALL_CURRENCY = {"BTC", "LTC", "ETH", "BNC", "ICO", "NEO", "BNB", "123", "456", "QTUM", "EOS", "SNT", "BNT", "GAS", "BCC", "BTM", "USDT", "HCC", "HSR", "OAX", "DNT", "MCO", "ICN", "ELC", "PAY", "ZRX", "OMG", "WTC", "LRX", "YOYO", "LRC", "LLT", "TRX", "FID", "SNGLS", "STRAT", "BQX", "FUN", "KNC", "CDT", "XVG", "IOTA", "SNM", "LINK", "CVC", "TNT", "REP", "CTR", "MDA", "MTL", "SALT", "NULS", "SUB", "STX", "MTH", "CAT", "ADX", "PIX", "ETC", "ENG", "ZEC", "AST", "1ST", "GNT", "DGD", "BAT", "DASH", "POWR", "BTG", "REQ", "XMR", "EVX", "VIB", "ENJ", "VEN", "CAG", "EDG", "ARK", "XRP", "MOD", "AVT", "STORJ", "KMD", "RCN", "EDO", "QASH", "SAN", "DATA", "DLT", "GUP", "MCAP", "MANA", "PPT", "OTN", "CFD", "RDN", "GXS", "AMB", "ARN", "BCPT", "CND", "GVT", "POE", "ALIS", "BTS", "FUEL", "XZC", "QSP", "LSK", "BCD", "TNB", "GRX", "STAR", "ADA", "LEND", "IFT", "KICK", "UKG", "VOISE", "XLM", "CMT", "WAVES", "WABI", "SBTC", "BCX", "GTO", "ETF", "ICX", "OST", "ELF", "AION", "WINGS", "BRD", "NEBL", "NAV", "VIBE", "LUN", "TRIG", "APPC", "CHAT", "RLC", "INS", "PIVX", "IOST", "STEEM", "NANO", "AE", "VIA", "BLZ", "SYS", "RPX", "NCASH", "POA", "ONT", "ZIL", "STORM", "XEM", "WAN", "WPR", "QLC"};
}

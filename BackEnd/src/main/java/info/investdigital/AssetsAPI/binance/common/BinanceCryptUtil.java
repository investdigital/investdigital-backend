package info.investdigital.AssetsAPI.binance.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zy on 2017/9/19.
 */
public class BinanceCryptUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String hmacSHA512(String data, String key) {
        return hmacHash(data, key, HMAC_SHA512);
    }

    public static String hmacSHA256(String data, String key) {
        return hmacHash(data, key, HMAC_SHA256);
    }

    private static String hmacHash(String data, String key, String algo) {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, algo);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA256);
            mac.init(signingKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        byte[] rawHmac = mac.doFinal(data.getBytes());

        String hexBytes = byte2hex(rawHmac);
        return hexBytes;
    }

    /**
     * byte array to hex string
     * @param bytes
     * @return
     */
    private static String byte2hex(final byte[] bytes) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = (Integer.toHexString(bytes[n] & 0xFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

}

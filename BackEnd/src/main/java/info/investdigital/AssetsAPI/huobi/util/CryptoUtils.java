package info.investdigital.AssetsAPI.huobi.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class CryptoUtils {
	public static byte[] hmacSHA256(String secretKey, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
			mac.init(secretKeySpec);
			byte[] digest = mac.doFinal(data.getBytes());
			return digest;
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid key exception while converting to HMac SHA256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String buildSign(String method, String baseUrl, String bizUrl, String params,String secretKey) {
		StringBuilder sb = new StringBuilder();
		sb.append(method).append("\n");
		sb.append(baseUrl).append("\n");
		sb.append(bizUrl).append("\n");
		sb.append(params);
		String ret = "";
		try {
			ret = Base64.encodeBase64String(hmacSHA256(secretKey, sb.toString()));
			ret = URLEncoder.encode(ret,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String buildSign(String method, String baseUrl, String bizUrl, Map<String, String> params,String secretKey) {
		return buildSign(method, baseUrl, bizUrl, ParamUtils.createLinkString(params), secretKey);
	}
}

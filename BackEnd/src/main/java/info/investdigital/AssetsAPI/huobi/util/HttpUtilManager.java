package info.investdigital.AssetsAPI.huobi.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtilManager {

	private static HttpClient client;
	private static long startTime = System.currentTimeMillis();
	public static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

	private static ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

		@Override
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			long keepAlive = super.getKeepAliveDuration(response, context);

			if (keepAlive == -1) {
				keepAlive = 5000;
			}
			return keepAlive;
		}

	};

	private HttpUtilManager() {
			client = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(keepAliveStrat).build();
	}
	public static void IdleConnectionMonitor() {

		if (System.currentTimeMillis() - startTime > 30000) {
			startTime = System.currentTimeMillis();
			cm.closeExpiredConnections();
			cm.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}

	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000)
			.setConnectionRequestTimeout(20000).build();

	public static HttpUtilManager getInstance() {
		return new HttpUtilManager();
	}

	public HttpClient getHttpClient() {
		return client;
	}

	private HttpPost httpPostMethod(String url) {
		return new HttpPost(url);
	}

	private HttpRequestBase httpGetMethod(String url) {
		return new HttpGet(url);
	}

	public String requestHttpGet(String urlPrex, String url) throws HttpException, IOException {

		return requestHttpGet(urlPrex, url, null);
	}

	public String requestHttpGet(String urlPrex, String url, Map<String, String> param)
			throws HttpException, IOException {

		IdleConnectionMonitor();
		if (param != null) {
			url = urlPrex + url + "?" + ParamUtils.getGetParam(param);
		} else {
			url = urlPrex + url;
		}

		HttpRequestBase method = this.httpGetMethod(url);
		method.setConfig(requestConfig);
		method.addHeader("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse response = client.execute(method);
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			throw new NullPointerException();
		}
		InputStream is = null;
		String responseData = "";
		try {
			is = entity.getContent();
			responseData = IOUtils.toString(is, "UTF-8");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return responseData;
	}

	public String requestHttpPost(String urlPrex, String url, Map<String, String> signMap)
			throws HttpException, IOException {
		return this.requestHttpPost(urlPrex, url, signMap, null);
	}

	public String requestHttpPost(String urlPrex, String url, Map<String, String> signMap, Map params)
			throws HttpException, IOException {

		IdleConnectionMonitor();
		url = urlPrex + url + "?" + ParamUtils.getGetParam(signMap);
		HttpPost method = this.httpPostMethod(url);
		method.setConfig(requestConfig);
		String json = this.convertMap2Json(params);
		StringEntity strEntity = new StringEntity(json,"utf-8");
		strEntity.setContentEncoding("UTF-8");
		strEntity.setContentType("application/json");

		method.setEntity(strEntity);

		HttpResponse response = client.execute(method);

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return "";
		}
		InputStream is = null;
		String responseData = "";
		try {
			is = entity.getContent();
			responseData = IOUtils.toString(is, "UTF-8");
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return responseData;

	}
	private String convertMap2Json(Map params) {

		if (params == null) {
			return "{}";
		}

		List<String> keys = new ArrayList<String>(params.keySet());
		int keySize = keys.size();
		JSONObject jsonParam = new JSONObject();
		for (int i = 0; i < keySize; i++) {
			String key = keys.get(i);
			jsonParam.put(key, params.get(key));
		}
		return jsonParam.toString();
	}

}

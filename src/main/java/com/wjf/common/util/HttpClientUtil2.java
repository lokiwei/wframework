package com.wjf.common.util;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

//import com.jd.official.core.exception.BusinessException;


public class HttpClientUtil2 {

	public static String CHARSET_ENCODING = "UTF-8";

	// 连接超时
	private final static int CONNECTION_TIMEOUT = 10000;
	// 读取超时
	private final static int SOCKET_TIMEOUT = 10000;
	// 每个HOST的最大连接数量
	private final static int MAX_PER_ROUTE = 20;
	// 连接池的最大连接数
	private final static int MAX_TOTAL = 60;
	// 连接池
	private final static PoolingClientConnectionManager pccm;

	private static HttpParams params;

	static {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		// 设置连接超时时间
		// HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
		// HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				CONNECTION_TIMEOUT);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT,
 SOCKET_TIMEOUT);

		// 设置访问协议
		SchemeRegistry schreg = new SchemeRegistry();
		schreg.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		schreg.register(new Scheme("https", 443, SSLSocketFactory
				.getSocketFactory()));

		// 多连接的线程安全的管理器
		pccm = new PoolingClientConnectionManager(schreg);
		pccm.setDefaultMaxPerRoute(MAX_PER_ROUTE); // 每个主机的最大并行链接数
		pccm.setMaxTotal(MAX_TOTAL); // 客户端总并行链接最大数
	}

	public static DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient(pccm, params);
		return httpClient;
	}

	/**
	 * 使用post方法获取相关的数据
	 * 
	 * @param url
	 * @param paramsList
	 * @return
	 */
	public static String post(String url, List<NameValuePair> paramsList) {
		return httpRequest(url, paramsList, "POST", null);
	}

	/**
	 * 使用post方法并且通过代理获取相关的数据
	 * 
	 * @param url
	 * @param para
msList
	 * @param proxy
	 * @return
	 */
	public static String post(String url, List<NameValuePair> paramsList,
			HttpHost proxy) {
		return httpRequest(url, paramsList, "POST", proxy);
	}

	/**
	 * 使用get方法获取相关的数据
	 * 
	 * @param url
	 * @param paramsList
	 * @return
	 */
	public static String get(String url, List<NameValuePair> paramsList) {
		return httpRequest(url, paramsList, "GET", null);
	}

	/**
	 * 使用get方法并且通过代理获取相关的数据
	 * 
	 * @param url
	 * @param paramsList
	 * @param proxy
	 * @return
	 */
	public static String get(String url, List<NameValuePair> paramsList,
			HttpHost proxy) {
		return httpRequest(url, paramsList, "GET", proxy);
	}

	/**
	 * 使用ResponseHandler接口处理响应，HttpClient使用ResponseHandler会自动管理连接的释放，解决了对连接的释放管理
	 */
	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		// 自定义响应处理
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				ContentType.getOrDefault(entity);
				String charset = (String) (ContentType.getOrDefault(entity)
						.getCharset() == null ? CHARSET_ENCODING
						: ContentType.getOrDefault(entity).getCharset()
								.toString());
				return EntityUtils.toString(entity,charset);
			} else {
				return null;
			}
		}
	};

	/**
	 * 提交数据到服务器
	 * 
	 * @param url
	 * @param params
	 * @param authenticated
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String httpRequest(String url,
			List<NameValuePair> paramsList, String method, HttpHost proxy) {
		String responseStr = null;
		// 判断输入的值是是否为空
		if (null == url || "".equals(url)) {
			return null;
		}

		// 创建HttpClient实例
		DefaultHttpClient httpclient = getHttpClient();

		String formatParams = null;
		// 将参数进行utf-8编码
		if (null != paramsList && paramsList.size() > 0) {
			formatParams = URLEncodedUtils.format(paramsList, CHARSET_ENCODING);
		}
		// 如果代理对象不为空则设置代理
		if (null != proxy) {
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
		try {
			// 如果方法为Get
			if ("GET".equalsIgnoreCase(method)) {
				if (formatParams != null) {
					url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams)
							: (url.substring(0, url.indexOf("?") + 1) + formatParams);
				}
				HttpGet hg = new HttpGet(url);
				responseStr = httpclient.execute(hg, responseHandler);

				// 如果方法为Post
			} else if ("POST".equalsIgnoreCase(method)) {
				HttpPost hp = new HttpPost(url);
				if (formatParams != null) {
					StringEntity entity = new StringEntity(formatParams);
					entity.setContentType("application/x-www-form-urlencoded");
					hp.setEntity(entity);
				}
				responseStr = httpclient.execute(hp, responseHandler);
			}

		} catch (ClientProtocolException e) {
//			throw new BusinessException("HttpClient调用异常", e);
		} catch (IOException e) {
//			throw new BusinessException("HttpClient调用异常", e);
		}
		return responseStr;
	}

}


/**
 * 
 */
package com.vdlm.spider.http;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.utils.Logs;

/**
 * <pre>
 * 封装了一个 HttpClient 执行类
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:07:40 AM Jul 19, 2014
 */
public class HttpClientInvoker {

	private static final Logger log = LoggerFactory.getLogger(HttpClientInvoker.class);

	private final static ResponseHandler<HttpInvokeResult> RESPONSE_HANDLER = new DefaultResponseHandler();

	private HttpClientProvider provider;
	private HttpClientHolder httpClient;
	private ShopType shopType;
	private String ip;
	private String userAgent;
	private String refer;

	private String url;
	private boolean invoked = true;
	private CookieStoreProvider cookieStoreProvider;

	public HttpInvokeResult invoke() {
		return invoke(this.url);
	}
	
	HttpInvokeResult invoke(final String url) {
		try {
			// httpClient 是否可用
			if (!this.httpClient.isUsable()) {
				return new HttpInvokeResult();
			}

			// 执行 get 请求
			final HttpGet httpGet = new HttpGet(url);
			//		httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5 * 1000);
			httpGet.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);// 手动处理自动转向.
			httpGet.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.TRUE);
			httpGet.getParams().setParameter(ClientPNames.MAX_REDIRECTS, 10);
			
			final HttpInvokeResult result = this.invoke(httpGet);
			
			// result 不会为 null
			Logs.monitorLogger.info("request:{},statusCode:{},exception:{}", result.getUrl(),
					result.getStatusCode(), ExceptionUtils.getMessage(result.getException()));
			
			return result;
		}
		finally {
			// 仅能执行一次
			this.invoked = false;
			this.httpClient.shutdown();
		}
	}
	
	HttpInvokeResult invoke(final HttpRequestBase request) {
		final String url = request.getURI().toString();

		if (log.isDebugEnabled()) {
			log.debug("invoke url:" + url);
		}

		HttpInvokeResult result;
		try {
			final CookieStore cookieStore = this.cookieStoreProvider.provide(this.ip);
	        final HttpContext context = new BasicHttpContext();
	        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	        
	        if (log.isDebugEnabled()) {
	        	log.debug("before request, cookieStore: {}", JSON.toJSONString(cookieStore, true));
	        }
	        
			result = this.httpClient.getHttpClient().execute(request, RESPONSE_HANDLER, context);
			if (result.getException() != null) {
				request.abort();
				log.error("请求失败,statusCode=" + result.getStatusCode() + ",url=" + url + ","
						+ result.getException().getMessage());
			}
			result.setUrl(request.getURI().toString());
			
			// boring
//	        if (log.isDebugEnabled()) {
//	        	log.debug("after request, cookieStore: {}", JSON.toJSONString(cookieStore, true));
//	        }
			
			request.releaseConnection();
			return result;
		}
		catch (final Throwable e) {
			request.abort();
			log.error("请求失败,url=" + url + "," + e.getMessage());
			result = new HttpInvokeResult();
			result.setUrl(url);
			result.setException(e);
			result.setReason(e.getMessage());
			return result;
		}
		finally {
			request.reset();
		}
	}

	void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	void setHttpClient(HttpClientHolder httpClient) {
		this.httpClient = httpClient;
	}

	void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	void setIp(String ip) {
		this.ip = ip;
	}

	void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	void setRefer(String refer) {
		this.refer = refer;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public String getIp() {
		return ip;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getRefer() {
		return refer;
	}

	public String getUrl() {
		return url;
	}

	void setUrl(String url) {
		this.url = url;
	}

	public boolean isInvoked() {
		return invoked;
	}

	void setInvoked(boolean invoked) {
		this.invoked = invoked;
	}

	void setCookieStoreProvider(CookieStoreProvider cookieStoreProvider) {
		this.cookieStoreProvider = cookieStoreProvider;
	}

	/**
	 * <pre>
	 * 做流控规则限制
	 * </pre>
	 */
	public void disable() {
		this.provider.disable(this);
	}

	/**
	 * <pre>
	 * 判断当前invoker是否可用
	 * </pre>
	 * @return
	 */
	public boolean isUsable() {
		return this.httpClient != null && this.httpClient.isUsable();
	}
}

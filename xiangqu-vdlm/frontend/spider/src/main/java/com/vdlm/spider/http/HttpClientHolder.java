/**
 * 
 */
package com.vdlm.spider.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.spider.Shutdownable;
import com.vdlm.spider.utils.AuthUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:32:37 AM Jul 22, 2014
 */
class HttpClientHolder extends HttpStatics implements Shutdownable {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final HttpClient httpClient;

	public HttpClientHolder(String ip, String userAgent, String refer) {
		this.httpClient = this.createHttpClient(ip, userAgent, refer);
	}

	HttpClient createHttpClient(final String ip, final String userAgent, final String refer) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		final boolean useProxy = !isLocalIp(ip);

		if (log.isDebugEnabled()) {
			log.debug("use ip:" + ip);
		}

		final HttpClient httpClient = this.createHttpClient();
		
		HttpConnectionParams.setSoKeepalive(httpClient.getParams(), true);

		HttpConnectionParams.setStaleCheckingEnabled(httpClient.getParams(), true);

		HttpConnectionParams.setTcpNoDelay(httpClient.getParams(), true);


		if (useProxy) {
			final String[] arr = StringUtils.split(ip, ":");
			final HttpHost proxy = new HttpHost(arr[0], Integer.parseInt(arr[1]));

			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		//		httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "GBK");

		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 90000);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 50000);

//				if (useProxy) {
//					httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
//				}
		if (httpClient instanceof DefaultHttpClient) {
			((DefaultHttpClient) httpClient).setRedirectStrategy(new DefaultRedirectStrategy() {
				@Override
				public boolean isRedirected(HttpRequest request, HttpResponse response,
						HttpContext context) throws ProtocolException {
					if (AuthUtils.isAuthResponse(response)) {
						// 需要鉴权，redirect过去也没意义
						return false;
					}
					return super.isRedirected(request, response, context);
				}
			});
		}

		final List<Header> headers = new ArrayList<Header>(5);
		headers.add(new BasicHeader("User-Agent", userAgent));
		if (userAgent.contains("Chrome")) {  // chrome
			headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
			headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4"));
		} else { // defautl firefox
			headers.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
			headers.add(new BasicHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
		}
		if (StringUtils.isNotBlank(refer)) {
			headers.add(new BasicHeader("Referer", refer));
		}
		
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headers);
//		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
		//((List<Header>)httpclient.getParams().getParameter(ClientPNames.DEFAULT_HEADERS)).add(new BasicHeader("Referer", refer));
		return httpClient;
	}

	HttpClient createHttpClient() {
		return new DefaultHttpClient();
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public void shutdown() {
	}

	public boolean isUsable() {
		return this.httpClient != null;
	}
}

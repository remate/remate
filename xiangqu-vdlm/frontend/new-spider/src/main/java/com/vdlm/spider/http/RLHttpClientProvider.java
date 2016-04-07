package com.vdlm.spider.http;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.limiter.RateLimiterProvider;
import com.vdlm.spider.ShopType;

/**
 *
 * @author: chenxi
 */

public class RLHttpClientProvider extends HttpClientProvider {

	@Autowired
	private RateLimiterProvider rlProvider;
	
	@Override
	public HttpClientInvoker provide(final ShopType shopType, final String url, final String refer, final String ip, final String userAgent) {
		final HttpClientHolder httpClient = new PoolingHttpClientHolder(ip, userAgent, refer);

		final HttpClientInvoker invoker = new RLHttpClientInvoker(rlProvider);
		invoker.setProvider(this);
		invoker.setHttpClient(httpClient);
		invoker.setShopType(shopType);
		invoker.setUrl(url);
		invoker.setRefer(refer);
		invoker.setIp(ip);
		invoker.setUserAgent(userAgent);
		invoker.setCookieStoreProvider(cookieStoreProvider);

		return invoker;
	}
	
	@Override
	public void disable(HttpClientInvoker invoker) {
		rlProvider.destory(invoker.getIp());
		super.disable(invoker);
	}
}

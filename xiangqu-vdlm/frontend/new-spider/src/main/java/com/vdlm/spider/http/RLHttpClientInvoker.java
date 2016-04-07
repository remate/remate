package com.vdlm.spider.http;

import org.apache.http.client.methods.HttpRequestBase;

import com.google.common.util.concurrent.RateLimiter;
import com.vdlm.limiter.RateLimiterProvider;

/**
 *
 * @author: chenxi
 */

public class RLHttpClientInvoker extends HttpClientInvoker {

	private final RateLimiterProvider provider;
	
	private RateLimiter rateLimiter;
	
	public RLHttpClientInvoker(RateLimiterProvider provider) {
		super();
		this.provider = provider;
	}
	
	@Override
	void setIp(String ip) {
		super.setIp(ip);
		rateLimiter = provider.provide(ip);
	}

	@Override
	HttpInvokeResult invoke(final HttpRequestBase request) {
		if (rateLimiter != null) {
			rateLimiter.acquire();
		}
		return super.invoke(request);
	}
	
	@Override
	public void disable() {
		provider.destory(getIp());
		rateLimiter = null;
		super.disable();
	}
}

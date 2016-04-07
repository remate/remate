package com.vdlm.spider.http.error;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public abstract class StatusCodeErrorBefore extends AbstractStatusCodeErrorHandler {

	private final StatusCodeErrorHandler component;
	
	public StatusCodeErrorBefore(boolean needRetry, StatusCodeErrorHandler component) {
		super(needRetry);
		this.component = component;
	}
	
	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		before(provider, invoker, result, task);
		component.errorHandling(provider, invoker, result, task);
	}

	protected abstract void before(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task);
	
}

package com.vdlm.spider.http.error;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public abstract class StatusCodeErrorAfter extends AbstractStatusCodeErrorHandler {

	private final StatusCodeErrorHandler component;
	
	public StatusCodeErrorAfter(boolean needRetry, StatusCodeErrorHandler component) {
		super(needRetry);
		this.component = component;
	}
	
	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		component.errorHandling(provider, invoker, result, task);
		after(provider, invoker, result, task);
	}

	protected abstract void after(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task);
	
}

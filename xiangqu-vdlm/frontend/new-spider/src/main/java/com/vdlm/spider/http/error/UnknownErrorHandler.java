package com.vdlm.spider.http.error;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public class UnknownErrorHandler extends AbstractStatusCodeErrorHandler {

	public UnknownErrorHandler() {
		super(true);
	}

	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		LOG.error("Error to invoke:" + result + ", proxy ip:"
				+ invoker.getIp(), result.getException());
	}

}

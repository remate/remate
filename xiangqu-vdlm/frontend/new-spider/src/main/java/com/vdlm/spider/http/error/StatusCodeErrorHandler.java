package com.vdlm.spider.http.error;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public interface StatusCodeErrorHandler 
{
	public void errorHandling(HttpClientProvider provider, HttpClientInvoker invoker,
			HttpInvokeResult result, ItemListTaskBean task);
	
	public boolean needRetry();
	
	public boolean toContinue();
}

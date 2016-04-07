package com.vdlm.spider.http.error;

import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public class CBegin400ErrorHandler extends AbstractStatusCodeErrorHandler {

	public CBegin400ErrorHandler() {
		super(true);
	}

	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		LOG.warn("Error to invoke:" + result + ",will disable proxy ip:"
				+ invoker.getIp(), result.getException());
		// image missing is acceptable
		if (task instanceof ImgTaskBean) {
			this.setToContinue(true);
		} else {
			provider.disable(invoker);
		}
	}

}

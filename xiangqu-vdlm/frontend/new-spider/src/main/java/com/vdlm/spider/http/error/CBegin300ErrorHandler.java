package com.vdlm.spider.http.error;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;

/**
 *
 * @author: chenxi
 */

public class CBegin300ErrorHandler extends AbstractStatusCodeErrorHandler {

	public CBegin300ErrorHandler() {
		super(false);
	}

	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		final HttpResponse response = result.getResponse();
		if (response == null) {
			return;
		}
		final Header locationHeader = response.getFirstHeader("location");
		// TODO 可以把规则配到配置文件中
		if (StringUtils.contains(locationHeader.getValue(),
				"login.taobao.com")
				|| StringUtils.contains(locationHeader.getValue(),
						"login.tmall.com")
//				|| StringUtils.contains(locationHeader.getValue(),
//						"jump.taobao.com")
						) {
			LOG.warn("Redirect to authorize when invoke:" + result + ",will disable proxy ip:" 
					+ invoker.getIp(), result.getException());
			
			provider.disable(invoker);
		}
	}

}

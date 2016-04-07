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

public class C404ErrorHandler extends AbstractStatusCodeErrorHandler {

	public C404ErrorHandler() {
		super(false);
	}

	@Override
	public void errorHandling(HttpClientProvider provider,
			HttpClientInvoker invoker, HttpInvokeResult result, ItemListTaskBean task) {
		LOG.warn("Error to invoke: {}, proxy ip:{}, 404 due to cannot find the resource", task,
				invoker.getIp());
		if (task instanceof ImgTaskBean) {
			this.setToContinue(true);
		}
//		//this.retry(true);
//		Logs.statistics.warn(
//				"parser_item. shopId:{}, listUrl:{}, reason:{}",
//				bean.getOuerShopId(), bean.getItemUrl(),
//				Logs.StatsResult.FAIL);
	}

}

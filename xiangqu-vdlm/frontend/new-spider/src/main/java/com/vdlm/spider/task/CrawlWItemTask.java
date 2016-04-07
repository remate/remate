package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class CrawlWItemTask extends CrawlableTask<WItemTaskBean> {

	public CrawlWItemTask(BusSignalManager bsm, WItemTaskBean bean,
			HttpClientProvider provider, StatusCodeErrorHandlerFactory scehf,
			int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_witem. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	@Override
	protected ParseWItemTask createParsableTask(HttpClientInvoker invoker,
			HttpInvokeResult result) {
		return new ParseWItemTask(bsm, invoker, result, this);
	}

//	@Override
//	protected boolean needRetry() {
//		return false;
//	}

}

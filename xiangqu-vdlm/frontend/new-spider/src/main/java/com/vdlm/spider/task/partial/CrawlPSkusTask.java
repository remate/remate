package com.vdlm.spider.task.partial;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.parial.PItemSkusTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class CrawlPSkusTask extends CrawlableTask<PItemSkusTaskBean> {

	public CrawlPSkusTask(BusSignalManager bsm, PItemSkusTaskBean bean,
			HttpClientProvider provider, StatusCodeErrorHandlerFactory scehf) {
		super(bsm, bean, provider, scehf, 1);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_{}. shopId:{}, listUrl:{}, reason:{}",
				bean.getTaskName(), bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	@Override
	protected ParsePSkusTask createParsableTask(HttpClientInvoker invoker,
			HttpInvokeResult result) {
		return new ParsePSkusTask(bsm, invoker, result, this);
	}

	@Override
	protected boolean needRetry() {
		return false;
	}
}

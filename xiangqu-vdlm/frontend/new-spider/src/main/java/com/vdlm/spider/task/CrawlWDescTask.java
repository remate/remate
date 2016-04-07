package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class CrawlWDescTask extends CrawlableTask<WDescTaskBean> {

	public CrawlWDescTask(BusSignalManager bsm, 
						     WDescTaskBean bean, 
						     HttpClientProvider provider, 
						     StatusCodeErrorHandlerFactory scehf,
						     int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected ParseWDescTask createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseWDescTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_wdesc. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	@Override
	protected void maxRetries() {
		final ItemEvent event = new ItemEvent(ItemEventType.MAX_RETRIES);
		event.setItemId(bean.getItemTaskId());
		event.setFailedDesc("crawl desc failure");
		bsm.signal(event);
		super.maxRetries();
	}

	@Override
	protected boolean needRetry() {
		final ItemEvent event = new ItemEvent(ItemEventType.NEED_RETRY);
		event.setItemId(bean.getItemTaskId());
		bsm.signal(event);
		return event.isNeedRetry();
	}

}

package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class CrawlItemListTask extends CrawlableTask<ItemListTaskBean> {

	private final ItemTaskStrategy strategy;
	
	public CrawlItemListTask(BusSignalManager bsm, 
							   ItemListTaskBean bean, 
							   HttpClientProvider provider, 
							   StatusCodeErrorHandlerFactory scehf,
							   int maxRetryTimes,
							   ItemTaskStrategy strategy) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
		this.strategy = strategy;
	}

	@Override
	protected ParseItemListTask createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseItemListTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_itemlist. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getShopUrl(),
				Logs.StatsResult.ASYNC);
	}

	public ItemTaskStrategy getStrategy() {
		return strategy;
	}

}

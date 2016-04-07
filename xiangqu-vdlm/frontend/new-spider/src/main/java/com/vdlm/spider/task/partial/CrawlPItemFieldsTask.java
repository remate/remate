package com.vdlm.spider.task.partial;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.parial.PItemFieldsTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.ParsableTask;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class CrawlPItemFieldsTask extends CrawlableTask<PItemFieldsTaskBean> {

	public CrawlPItemFieldsTask(BusSignalManager bsm, PItemFieldsTaskBean bean,
			HttpClientProvider provider, StatusCodeErrorHandlerFactory scehf,
			int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_item_field. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	@Override
	protected ParsableTask<?> createParsableTask(HttpClientInvoker invoker,
			HttpInvokeResult result) {
		// TODO Auto-generated method stub
		return null;
	}

}

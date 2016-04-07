package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.ParseItemTask.ItemWrapper;
import com.vdlm.spider.utils.Logs;

/**
 * // 爬item

void crawlItem() {

    // 通过CrawlItemThreadPool中的线程从CrawlItemQueue中取出一个task

    CrawlItemTask task = CrawlItemQueue.poll();

    if (task != null) {

       // 发起http get请求

       HttpResut result = task.httpGet();

       if (!result.success()) {

           // 各种失败，则重入队列末进行retry操作

           CrawlItemQueue.add(task);

           return;

       }

       // 获取http result成功

       // 将result deliver给ParseItemTask，创建与此店铺对应的CPU解         // 析task并置入相应线程池

       ParseItemTask task1 = createParseItemTask(task, result);

       ParseItemThreadPool.submit(task1);

    }

}
 * @author: chenxi
 */

public class CrawlItemTask extends CrawlableTask<ItemTaskBean> {

	public CrawlItemTask(BusSignalManager bsm, 
			ItemTaskBean bean, 
			   			   HttpClientProvider provider, 
			   			   StatusCodeErrorHandlerFactory scehf,
			   			   int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected ParsableTask<ItemWrapper> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseItemTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_item. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

}

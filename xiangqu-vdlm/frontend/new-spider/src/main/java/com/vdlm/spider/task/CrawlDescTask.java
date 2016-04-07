package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.utils.Logs;

/**
 * // 爬desc

void crawlDesc() {

    // 通过CrawDescThreadPool中的线程从CrawlDescQueue中取出一个task

    CrawlDescTask task = CrawlDescQueue.poll();

    if (task != null) {

       // 发起http get请求

       HttpResut result = task.httpGet();

       if (!result.success()) {

           // 各种失败，则重入队列末进行retry操作

           CrawlDescQueue.add(task);

           return;

       }

       // 获取http result成功

       // 将result deliver给ParseDescTask，创建与此店铺对应的CPU解         // 析task并置入相应线程池

       ParseDescTask task1 = createParseDescTask(task, result);

       ParseDescThreadPool.submit(task1);

    }

}
 * @author: chenxi
 */

public class CrawlDescTask extends CrawlableTask<DescTaskBean> {

	public CrawlDescTask(BusSignalManager bsm, 
						   DescTaskBean bean, 
						   HttpClientProvider provider, 
						   StatusCodeErrorHandlerFactory scehf,
						   int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected ParsableTask<Desc> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseDescTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_desc. shopId:{}, listUrl:{}, reason:{}",
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

package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.task.ParseSkuTask.SkuList;

/**
 * // 爬sku

void crawlSku() {

    // 通过CrawSkuThreadPool中的线程从CrawlSkuQueue中取出一个task

    CrawlSkuTask task = CrawlSkuQueue.poll();

    if (task != null) {

       // 发起http get请求

       HttpResut result = task.httpGet();

       if (!result.success()) {

           // 各种失败，则重入队列末进行retry操作

           CrawlSkuQueue.add(task);

           return;

       }

       // 获取http result成功

       // 将result deliver给ParseSkuTask，创建与此店铺对应的CPU解析    // task并置入相应线程池

       ParseSkuTask task1 = createParseSkuTask(task, result);

       ParseSkuThreadPool.submit(task1);

    }

}
 * @author: chenxi
 */

public class CrawlSkuTask extends CrawlableTask<SkuTaskBean> {

	public CrawlSkuTask(BusSignalManager bsm, 
						  SkuTaskBean bean, 
						  HttpClientProvider provider, 
						  StatusCodeErrorHandlerFactory scehf,
						  int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected HttpClientInvoker getHttpClient() {
		return this.provider.provide(this.bean.getShopType(),
				this.bean.getRequestUrl(), this.bean.getRefererUrl(), bean.getReferIp(), bean.getUserAgent());
	}

	@Override
	protected ParsableTask<SkuList> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseSkuTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void maxRetries() {
		final ItemEvent event = new ItemEvent(ItemEventType.MAX_RETRIES);
		event.setItemId(bean.getItemTaskId());
		event.setFailedDesc("crawl sku failure");
		super.maxRetries();
	}

//	@Override
//	protected boolean needRetry() {
//		final ItemEvent event = new ItemEvent(ItemEventType.NEED_RETRY);
//		event.setItemId(bean.getItemTaskId());
//		bsm.signal(event);
//		return event.isNeedRetry();
//	}
}

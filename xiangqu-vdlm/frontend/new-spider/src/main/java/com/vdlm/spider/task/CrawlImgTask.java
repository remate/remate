package com.vdlm.spider.task;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.utils.Logs;

/**
 * // 爬img

void crawlImg() {

    // 通过CrawImgThreadPool中的线程从CrawlImgQueue中取出一个task

    CrawlImgTask task = CrawlImgQueue.poll();

    if (task != null) {

       // 发起http get请求

       HttpResut result = task.httpGet();

       if (!result.success()) {

           // 各种失败，则重入队列末进行retry操作

           CrawlImgQueue.add(task);

           return;

       }

       // 获取http result成功

       // 将result deliver给ParseImgTask，创建与此店铺对应的CPU解析    // task并置入相应线程池

       ParseImgTask task1 = createParseImgTask(task, result);

       ParseImgThreadPool.submit(task1);

    }

}
 * @author: chenxi
 */

public class CrawlImgTask extends CrawlableTask<ImgTaskBean> {
	
	public CrawlImgTask(BusSignalManager bsm, 
						   ImgTaskBean bean, 
			   			   HttpClientProvider provider, 
			   			   StatusCodeErrorHandlerFactory scehf,
			   			   int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected HttpClientInvoker getHttpClient() {
//		final String ip = this.provider.getIpPools().randomSafeAvaliableIp(bean.getShopType());
		String imgUrl = bean.getRequestUrl();
		if (imgUrl.startsWith("https://")) {
			imgUrl = imgUrl.replaceFirst("https://", "http://");
			bean.setRequestUrl(imgUrl);
		}
		return this.provider.provide(this.bean.getShopType(),
				this.bean.getRequestUrl(), this.bean.getRefererUrl());
	}

	@Override
	protected ParsableTask<Img> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseImgTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		Logs.statistics.warn(
				"parser_img. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	@Override
	protected void maxRetries() {
		final ItemEvent event = new ItemEvent(ItemEventType.MAX_RETRIES);
		event.setItemId(bean.getImg().getItemId());
		event.setFailedDesc("crawl img type " + bean.getImg().getType() + "failure");
		bsm.signal(event);
		super.maxRetries();
	}

//	@Override
//	protected boolean needRetry() {
//		final ItemEvent event = new ItemEvent(ItemEventType.NEED_RETRY);
//		event.setItemId(bean.getImg().getItemId());
//		bsm.signal(event);
//		return event.isNeedRetry();
//	}
}

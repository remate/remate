package com.vdlm.spider.task;

import org.apache.commons.lang3.StringUtils;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.DataSource;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;

/**
 * // 爬店铺

void crawlShop() {

    // 通过CrawlShopThreadPool中的线程从CrawlShopQueue中取出一个task

    CrawlShopTask task = CrawlShopQueue.poll();

    if (task != null) {

       // 发起http get请求

       HttpResut result = task.httpGet();

       if (!result.success()) {

           // 各种失败，则重入队列末进行retry操作

           CrawlShopQueue.add(task);

           return;

       }

       // 获取http result成功

       // 将result deliver给ParseShopTask，创建与此店铺对应的CPU解         // 析task并置入相应线程池

       ParseShopTask task1 = createParseShopTask(task, result);

       ParseShopThreadPool.submit(task1);

    }  

}
 * @author: chenxi
 */

public class CrawlShopTask extends CrawlableTask<ShopTaskBean> {

	public CrawlShopTask(BusSignalManager bsm, 
							ShopTaskBean bean, 
							HttpClientProvider provider, 
							StatusCodeErrorHandlerFactory scehf,
							int maxRetryTimes) {
		super(bsm, bean, provider, scehf, maxRetryTimes);
	}

	@Override
	protected HttpClientInvoker getHttpClient() {
		final boolean useProxy = bean.isUseProxy();
		String ip;
		if (useProxy) {
			ip = this.provider.getIpPools().pollingSafeAvaliableIp(this.bean.getShopType());
		} else {
			ip = this.provider.getIpPools().getLoopbackIp();
		}
		
		if ( ! StringUtils.isEmpty(this.bean.getItemId())) { 
			return this.provider.provide(this.bean.getShopType(), getReqUrlByItemId(this.bean.getItemId()), 
					this.bean.getRefererUrl(), ip);
		} else {
			return this.provider.provide(this.bean.getShopType(), this.bean.getRequestUrl(),
					this.bean.getRefererUrl(), ip);
		}
	}
	
	@Override
	protected ParsableTask<Shop> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result) {
		return new ParseShopTask(bsm, invoker, result, this);
	}

	@Override
	protected void handleUnusableInvoker() {
		LOG.warn("can not get usable invoker from provider, ignore ParseShopInfoBean:{}", this.bean);
	}
	
	private String getReqUrlByItemId(String itemId) {
		String auctionUrlType = ""; 
		if (DataSource.MOBILE.equals(this.bean.getDataSource())) {
			auctionUrlType = WItemTaskBean.WDETAIL_PREFIX;  // 如果启用手淘关闭注释
		} else {
			if(bean.getShopType() == ShopType.TMALL){
				auctionUrlType = "http://detail.tmall.com/item.htm?id=";		
			} else {
				auctionUrlType = "http://item.taobao.com/item.htm?id=";		
			}
		}
		auctionUrlType += itemId.trim();
		
		return auctionUrlType;
	}
	
}

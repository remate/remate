/**
 * 
 */
package com.vdlm.spider.parser.shop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.spider.Config;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.AbstractParser;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.utils.AuthUtils;
import com.vdlm.spider.utils.Logs;
import com.vdlm.spider.utils.Logs.StatsResult;

/**
 * <pre>
 * 解析店铺中的商品
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:12:12 PM Jul 16, 2014
 */
public abstract class ShopParser extends AbstractParser {

	private static final Logger LOG = LoggerFactory.getLogger(ShopParser.class);
	

	protected final HttpClientProvider provider;
	protected final ParseShopTaskBean bean;

	protected HttpClientInvoker invoker;

	public ShopParser(final HttpClientProvider provider, final ParseShopTaskBean bean) {
		this.provider = provider;
		this.bean = bean;
	}
	
	// 重试
	void retry() {
		retry(true);
	}

	void retry(boolean incr) {
		if (!UrlCounters.delete(bean.getReqFrom(), bean.getOuerUserId(), bean.getOuerShopId(), bean.getRequestUrl())) {
			// 缓存清失败就不管了
			Logs.unpredictableLogger.warn("Failed to retry invoker url:{}, clear cache failed.", this.bean.getShopUrl());
		}

		if (this.bean.retryTimes(incr) > Config.instance().getRetryTimes()
				|| (System.currentTimeMillis() - this.bean
						.getCreateTimeMillis()) > Config.instance()
						.getRetryTimeout()) {
//			final ParserType parserType = ParserType.index(this.bean.getShopType());
//			if (parserType != this.bean.getParserType()) {
//				// 爬首页
//				// 加入 shop 队列
//				final ParseShopTaskBean tb = this.bean.clone();
//				tb.setRefererUrl(null);
//				tb.setRequestUrl(this.bean.getShopUrl());
//				// TODO 后续跟踪
//				//tb.setRetryTimes(0);
//				tb.setParserType(parserType);
//				if (!TaskQueues.getParseShopTaskQueue().add(tb.toJSONBytes())) {
//					Logs.unpredictableLogger.warn("Failed to submit ParseShopTask: {}", tb);
//				}
//			} else {
//				// 不会进到这里
//				LOG.error("retry time over, ignore: {}", this.bean);
//			}
//			return;
			LOG.warn("retry time over, Failed to retry ParseShopTask: {}",
					this.bean);
			Logs.statistics.warn("parse_shopId={0}, result={1}",
					bean.getOuerShopId(), Logs.StatsResult.FAIL);
			TaskCounters.decrementAndGet(this.bean.getTaskId());
			return;
		}
		
		boolean retry = false;
		try {
			retry = TaskQueues.getParseShopTaskQueue().add(this.bean.toJSONBytes());
			LOG.warn("Start to retry, shopUrl:{}, retry times:{}",
					this.bean.getShopUrl(), this.bean.getRetryTimes());
		}
		catch (Exception e) {
			retry = false;
			//unpredictable exception
		}
		if (!retry) {
			Logs.unpredictableLogger.warn("Failed to retry submit ParseShopTask: {}", this.bean);
			Logs.statistics.warn("parse_shopId={0}, result={1}",
					bean.getOuerShopId(), Logs.StatsResult.FAIL);
		}
	}	

	@Override
	public void parse() {
		this.invoker = this.provider.provide(this.bean.getShopType(), this.bean.getRequestUrl(),
				this.bean.getRefererUrl());
		// 不可用，则重试
		if (!this.invoker.isUsable()) {
			this.retry(false);
			Logs.statistics.warn("parse_shopId={0}, result={1}",
					bean.getOuerShopId(), Logs.StatsResult.ASYNC);
			return;
		}

		final HttpInvokeResult result = this.invoker.invoke();

		if (result.isOK()) {
			StatsResult r = this.parse(result.getContentStringAndReset());
			Logs.statistics.warn("parse_shopId={0}, result={1}",
					bean.getOuerShopId(), r);
		}
		// 404错误
		else if (result.getStatusCode() == 404) {
			LOG.warn("Error to invoke:{}, proxy ip:{}, statusCode:{}",
					this.bean, this.invoker.getIp(), result.getStatusCode());
			//this.retry(true);
			Logs.statistics.warn("parse_shopId={0}, result={1}, 404 found",
					bean.getOuerShopId(), Logs.StatsResult.FAIL);
			return;
		}
		// 禁止访问
		else if (result.getStatusCode() == 400 
				|| result.getStatusCode() == 401 
				|| result.getStatusCode() == 402 
				|| result.getStatusCode() == 403 
				|| result.getStatusCode() == 405) {
			LOG.warn("Error to invoke:" + result + ",will disable proxy ip:"
					+ this.invoker.getIp(), result.getException());
			
			this.provider.disable(this.invoker);
			this.retry(true);
		}
		// 需要鉴权则换个IP重试，并且让当前代理闲置一下
		else if (AuthUtils.isAuthResponse(result.getResponse())) {
			LOG.warn("Redirect to authorize when invoke:" + result + ",will disable proxy ip:" 
					+ this.invoker.getIp(), result.getException());
			
			this.provider.disable(this.invoker);
			this.retry(false);
		}
		// 未知情况
		else {
			LOG.error("Error to invoke:" + result + ", proxy ip:" + this.invoker.getIp(), result.getException());
			// 重试
			this.retry(true);
		}
		Logs.statistics.warn("parse_shopId={0}, result={1}",
				bean.getOuerShopId(), Logs.StatsResult.ASYNC);
	}

	/**
	 * <pre>
	 * 解析 shop 页 
	 * </pre>
	 * @param htmlContent
	 */
	abstract Logs.StatsResult parse(String htmlContent);
	
}

/**
 * 
 */
package com.vdlm.spider.parser.itemlist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.Config;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.AbstractParser;
import com.vdlm.spider.parser.config.ItemListConfig;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.utils.AuthUtils;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 7:35:18 PM Jul 16, 2014
 */
public abstract class ItemListParser extends AbstractParser {

	private static final Logger LOG = LoggerFactory
			.getLogger(ItemListParser.class);

	protected final HttpClientProvider provider;
	protected final ParseShopTaskBean bean;

	protected HttpClientInvoker invoker;

	public ItemListParser(final HttpClientProvider provider,
			final ParseShopTaskBean bean) {
		this.provider = provider;
		this.bean = bean;
	}

	static final Pattern pattern = Pattern.compile("inshopn=(\\d+)");

	// private int url2RequestCount = 1; //该值用于记录url2请求次数
	// private int url2StartSize = Config.instance().getUrl2StartSize();
	// private int url2EndSize = Config.instance().getUrl2EndSize();

	/**
	 * <pre>
	 * 获取 itemList 配置
	 * </pre>
	 * 
	 * @return
	 */
	public abstract ItemListConfig getItemListConfig();

	// 重试
	void retry() {
		this.retry(true);
	}

	void retry(boolean incr) {
		if (this.bean.retryTimes(incr) > Config.instance().getRetryTimes()
				|| (System.currentTimeMillis() - this.bean
						.getCreateTimeMillis()) > Config.instance()
						.getRetryTimeout()) {
			LOG.warn("retry time over, Failed to retry ParseShopTask: {}",
					this.bean);
			Logs.statistics.warn(
					"parser_itemlist. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getShopUrl(),
					Logs.StatsResult.FAIL);
			return;
		}

		if (!UrlCounters.delete(bean.getReqFrom(), bean.getOuerUserId(),
				bean.getOuerShopId(), bean.getRequestUrl())) {
			// 缓存清失败就不管了
			Logs.unpredictableLogger.warn(
					"Failed to retry invoker url:{}, clear cache failed.",
					this.bean.getRequestUrl());
			return;
		}

		boolean retry = false;
		try {
			retry = TaskQueues.getParseShopTaskQueue().add(
					this.bean.toJSONBytes());
			LOG.warn("Start to retry, shopUrl:{}, retry times:{}",
					this.bean.getShopUrl(), this.bean.getRetryTimes());
		} catch (Exception e) {
			retry = false;
			// unpredictable exception
		}
		if (!retry) {
			Logs.unpredictableLogger.warn(
					"Failed to retry submit ParseShopTask: {}", this.bean);
			Logs.statistics.warn(
					"parser_itemlist. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getShopUrl(),
					Logs.StatsResult.FAIL);
		}
	}

	@Override
	final public void parse() {
		this.invoker = this.provider.provide(this.bean.getShopType(),
				this.bean.getRequestUrl(), this.bean.getRefererUrl());
		// 不可用，则重试
		if (!this.invoker.isUsable()) {
			this.retry(false);
			Logs.statistics.warn(
					"parser_itemlist. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getShopUrl(),
					Logs.StatsResult.ASYNC);
			return;
		}

		final HttpInvokeResult result = this.invoker.invoke();

		if (result.isOK()) {
			Logs.StatsResult ret = this
					.parse(result.getContentStringAndReset());
			Logs.statistics.warn(
					"parser_itemlist. shopId:{}, shopUrl:{}, reason:{}",
					this.bean.getOuerShopId(), this.bean.getShopUrl(), ret);
		}
		// 404错误
		else if (result.getStatusCode() == 404) {
			LOG.warn("Error to invoke: {}, proxy ip:{}", this.bean,
					this.invoker.getIp());
			// this.retry(true);
			Logs.statistics
					.warn("parser_itemlist. shopId:{}, listUrl:{}, code:{}, reason:{}",
							bean.getOuerShopId(), bean.getShopUrl(),
							result.getStatusCode(), Logs.StatsResult.FAIL);
		}
		// 禁止访问
		else if (result.getStatusCode() == 400 || result.getStatusCode() == 401
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
			LOG.warn("Redirect to authorize when invoke:" + result
					+ ",will disable proxy ip:" + this.invoker.getIp(),
					result.getException());

			this.provider.disable(this.invoker);
			this.retry(false);
		}
		// 未知情况
		else {
			LOG.error("Error to invoke:" + result + ", proxy ip:"
					+ this.invoker.getIp(), result.getException());
			// 重试
			this.retry(true);
		}
		Logs.statistics.warn(
				"parser_itemlist. shopId:{}, listUrl:{}, code:{}, reason:{}",
				bean.getOuerShopId(), bean.getShopUrl(),
				result.getStatusCode(), Logs.StatsResult.ASYNC);
	}

	/**
	 * <pre>
	 * 解析 itemlist.json
	 * </pre>
	 * 
	 * @param jsonString
	 * @return
	 */
	Logs.StatsResult parse(final String jsonString) {
		final JSONObject json;
		try {
			json = JSON.parseObject(jsonString);
		} catch (Exception e) {
			Logs.maybeChangedRuleLogger
					.error("Error to parse jsonString from itemlist.htm, maybe invalid ParseRule:{}\n,jsonString:\n{}",
							this.bean, jsonString);
			return Logs.StatsResult.FAIL;
		}

		// 获取 item 列表
		final JSONArray itemList;
		try {
			itemList = this
					.get(json, this.getItemListConfig().getAuctions(), 2);
			Logs.maybeChangedRuleLogger.info("ShopUrl:{}, itemListCount:{}",
					this.bean.getShopUrl(),
					itemList == null ? 0 : itemList.size());
		} catch (Exception e) {
			Logs.maybeChangedRuleLogger.error(
					"can not find itemList from itemlist.htm, maybe invalid ParseRule:"
							+ this.bean, e);
			return Logs.StatsResult.FAIL;
		}
		// 没有数据
		if (itemList == null) {
			Logs.maybeChangedRuleLogger
					.error("can not find itemList from itemlist.htm, maybe invalid ParseRule:"
							+ this.bean);
			return Logs.StatsResult.FAIL;
		}

		int cntAdded = 0;
		for (int i = 0; i < (itemList).size(); i++) {
			final JSONObject item = (itemList).getJSONObject(i);
			
			final Object auctionId = this.get(item, this.getItemListConfig()
					.getAuctionId());
			final Object auctionUrl = this.getShopConfig().getItemUrl(auctionId.toString()); 

			if (LOG.isDebugEnabled()) {
				LOG.debug("i={}, itemUrl={}, itemId={}, total={}", i,
						auctionUrl, auctionId, (itemList).size());
			}

			final ParseItemTaskBean tb = new ParseItemTaskBean();
			BeanUtils.copyProperties(this.bean, tb);
			tb.setItemId(auctionId.toString());
			tb.setItemUrl(auctionUrl.toString());
			tb.setRefererUrl(null);
			tb.setRetryTimes(0);
			tb.setParserType(ParserType.item(this.bean.getShopType()));
			
			if (TaskQueues.getParseItemTaskQueue().add(tb.toJSONBytes())) {
				TaskCounters.incrementAndGet(this.bean.getTaskId());
				cntAdded++;
			} else {
				Logs.unpredictableLogger.error(
						"Failed to submit ParseItemTask: {}", tb);
			}
			
		}
		if(cntAdded < (itemList).size()) {
			Logs.statistics
					.warn("parser_itemlist. shopId:{}, listUrl:{}, total:{}, current:{}, reason:{}",
							bean.getOuerShopId(), bean.getShopUrl(),
							(itemList).size(), cntAdded,
							Logs.StatsResult.PARTIAL);
		}

		// url2当前最多只支持返回128条记录，若宝贝数超出最大值，设置索引重新加入队列
		try {
			if (((JSONArray) itemList).size() >= Config.instance()
					.getUrl2EndSize()) {
				Matcher mc = pattern.matcher(this.bean.getRequestUrl());
				mc.find();
				int url2StartSize = Integer.parseInt(mc.group(1));
				int url2EndSize = url2StartSize
						+ Config.instance().getUrl2EndSize();

				final ParseShopTaskBean tb = this.bean.clone();
				tb.setRefererUrl(this.bean.getRequestUrl());
				tb.setRequestUrl(this.getItemListConfig().getUrl2(
						String.valueOf(url2StartSize),
						String.valueOf(url2EndSize), this.bean.getTUserId()));
				tb.setRetryTimes(0);
				if (!TaskQueues.getParseShopTaskQueue().add(tb.toJSONBytes())) {
					Logs.unpredictableLogger.warn(
							"Failed to submit ParseShopTask: {}", tb);
				} 

				// url2RequestCount ++;
				if (LOG.isDebugEnabled()) {
					LOG.debug("currentStartSize={}, currentEndPage={}",
							url2StartSize, url2EndSize);
				}
			}
		} catch (Exception e) {
			Logs.maybeChangedRuleLogger.error(
					"can not find pageInfo, maybe invalid ParseRule: "
							+ this.bean, e);
			return Logs.StatsResult.FAIL;
		}

		return Logs.StatsResult.ASYNC;
	}

	Object get(JSONObject json, String jsonIndex) {
		final String[] idxs = jsonIndex.split("\\.");

		Object obj = json;
		for (String idx : idxs) {
			final int bgn = idx.lastIndexOf('[');
			final int end = idx.lastIndexOf(']');
			if (bgn < 0 || end < 0 || bgn > end) {
				obj = ((JSONObject) obj).get(idx);
				continue;
			}

			if (end == idx.length() - 1) {
				if (bgn != 0) {
					obj = ((JSONObject) obj).get(idx.substring(0, bgn));
				}
				obj = ((JSONArray) obj).get(Integer.parseInt(idx.substring(
						bgn + 1, end - 1)));
			} else {
				obj = ((JSONObject) obj).get(idx);
			}
		}

		return obj;
	}

	/*
	 * index:split分割合并位数(按第1位开始计算)
	 */
	JSONArray get(JSONObject json, String jsonIndex, Integer index) {
		final String[] idxs = new String[jsonIndex.split("\\.").length + 1];
		for (int i = 0; i < idxs.length - 1; i++) {
			idxs[i] = jsonIndex.split("\\.")[i];
		}
		idxs[idxs.length - 1] = jsonIndex.split("\\.")[jsonIndex.split("\\.").length - 1];

		Object obj = json;
		int i = 0;
		int j = 0;
		String str = "";
		for (String idx : idxs) {
			if (i < index) {
				++i;
				str += idx + ".";
				continue;
			}
			if (str.equals(""))
				str = idx;
			else {
				if (str.split("\\.").length > 0) {
					if (j > 0)
						str = idx;
					else
						str = str.substring(0, str.length() - 1);
					++j;
				} else
					str = idx;
			}
			final int bgn = str.lastIndexOf('[');
			final int end = str.lastIndexOf(']');
			if (bgn < 0 || end < 0 || bgn > end) {
				obj = ((JSONObject) obj).get(str);
				continue;
			}

			if (end == str.length() - 1) {
				if (bgn != 0) {
					obj = ((JSONObject) obj).get(str.substring(0, bgn));
				}
				obj = ((JSONArray) obj).get(Integer.parseInt(str.substring(
						bgn + 1, end - 1)));
			} else {
				obj = ((JSONObject) obj).get(str);
			}
		}
		if (!(obj instanceof JSONArray)) {
			obj = null;
		}

		return (JSONArray) obj;
	}
}

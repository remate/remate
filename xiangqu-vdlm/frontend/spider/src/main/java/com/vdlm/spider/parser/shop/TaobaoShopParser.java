/**
 * 
 */
package com.vdlm.spider.parser.shop;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.vdlm.spider.ParserType;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.Props;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:10:34 PM Jul 16, 2014
 */
public class TaobaoShopParser extends ShopParser {

	private static final Logger LOG = LoggerFactory.getLogger(TaobaoShopParser.class);

	public TaobaoShopParser(HttpClientProvider provider, ParseShopTaskBean bean) {
		super(provider, bean);
	}

	@Override
	public Map<String, ParserConfig> getParserConfigs() {
		return ParserConfigs.getOrCreateTaobaoShopConfig();
	}

	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTaobaoAuthConfig();
	}

	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTaobaoShopConfig();
	}

	@Override
	public Logs.StatsResult parse(String htmlContent) {
//		try {
//			FileUtils.write(new java.io.File("C:\\Users\\Think\\Desktop\\111.html"), htmlContent);
//		}
//		catch (IOException e1) {
//			e1.printStackTrace();
//		}
		Logs.StatsResult ret = Logs.StatsResult.SUCESS;

		int counter = 0;
		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(htmlContent);
		try {
			final Map<String, ParserConfig> configs = this.getParserConfigs();
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			// 啥都没有
			if (nodeList == null || nodeList.size() == 0) {
				// 如果是认证页面，则重试
				if (this.isAuthHtml(htmlContent)) {
					this.provider.disable(this.invoker);
					this.retry(true);
				} else { // 解析不出来则去解析首页
					Logs.maybeChangedRuleLogger.error("Nothing to parse search.htm, maybe invalid ParseRule: {}",
							this.bean);

					// 爬首页
					this.toParseIndex();
				}
				return Logs.StatsResult.ASYNC;
			}

			List<ParserConfig> matchConfigs;
			for (int a = 0; a < nodeList.size(); a++) {

				final Node aNode = nodeList.elementAt(a);

				matchConfigs = ParserUtils.match(aNode, Props.ITEM, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (String url : ParserUtils.getValues(aNode,
							matchConfigs, configs)) {
						if (StringUtils.isBlank(url))
							continue;

						++counter;

						if (!UrlCounters.exists(this.bean.getReqFrom(),
								this.bean.getOuerUserId(),
								this.bean.getOuerShopId(), url)) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("ignore item.html >> " + url);
							}
							continue;
						}

						if (LOG.isDebugEnabled()) {
							LOG.debug("item.html >> " + url);
						}

						// 加入 item 队列
						final ParseItemTaskBean tb = new ParseItemTaskBean();
						BeanUtils.copyProperties(this.bean, tb);
						tb.setItemUrl(url);
						tb.setRefererUrl(null);
						tb.setRetryTimes(0);
						tb.setParserType(ParserType.index(this.bean
								.getShopType()));
						if (TaskQueues.getParseItemTaskQueue().add(
								tb.toJSONBytes())) {
							TaskCounters.incrementAndGet(this.bean.getTaskId());
						} else {
							ret = Logs.StatsResult.PARTIAL;
							Logs.unpredictableLogger.warn(
									"Failed to submit ParseItemTask: {}", tb);
						}
					}
				}

				matchConfigs = ParserUtils.match(aNode, Props.PAGINATION, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (String url : ParserUtils.getValues(aNode,
							matchConfigs, configs)) {
						if (StringUtils.isBlank(url))
							continue;
						++counter;

						final int index = url.lastIndexOf('#');
						if (index > 0) {
							url = url.substring(0, index);
						}

						if (!UrlCounters.add(this.bean.getReqFrom(),
								this.bean.getOuerUserId(),
								this.bean.getOuerShopId(), url)) {
							if (LOG.isDebugEnabled()) {
								LOG.debug("ignore pagination.shop.html >> "
										+ url);
							}
							continue;
						}

						if (LOG.isDebugEnabled()) {
							LOG.debug("pagination.shop.html >> " + url);
						}

						// 加入 shop 队列
						final ParseShopTaskBean tb = this.bean.clone();
						tb.setRefererUrl(this.bean.getRequestUrl());
						tb.setRequestUrl(url);
						tb.setRetryTimes(0);
						if (!TaskQueues.getParseShopTaskQueue().add(
								tb.toJSONBytes())) {
							Logs.unpredictableLogger.warn(
									"Failed to submit ParseShopTask: {}", tb);
							ret = Logs.StatsResult.PARTIAL;
						}
					}
				}
			}

			if (counter == 0) {
				Logs.unpredictableLogger.error("Nothing to parse search.htm, maybe invalid ParseRule:{}", this.bean);

				// 去爬首页
				this.toParseIndex();
				ret = Logs.StatsResult.ASYNC;
			}
		} catch (Exception e) {
			// 这里不输出html，太长了
			Logs.unpredictableLogger.error("Error to parse search.htm, maybe invalid ParseRule:" + this.bean, e);
			// 去爬首页
			this.toParseIndex();
			ret = Logs.StatsResult.ASYNC;
		}
		return ret;
	}

	/**
	 * 爬index.htm
	 */
	void toParseIndex() {
		// 加入 shop 队列
		final ParseShopTaskBean tb = this.bean.clone();
		tb.setRefererUrl(null);
		tb.setRequestUrl(this.bean.getShopUrl());
		tb.setRetryTimes(0);
		tb.setParserType(ParserType.index(this.bean.getShopType()));
		if (!TaskQueues.getParseShopTaskQueue().add(tb.toJSONBytes())) {
			Logs.unpredictableLogger.warn("Failed to submit ParseShopTask: {}", tb);
		}
	}
}

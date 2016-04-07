/**
 * 
 */
package com.vdlm.spider.parser.shop.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ParseShopInfoBean;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.AbstractParser;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.Props;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.service.ShopService;
import com.vdlm.spider.utils.AuthUtils;
import com.vdlm.spider.utils.Logs;
import com.vdlm.spider.utils.URLDecoders;

/**
 * <pre>
 * 爬店铺主页
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:34:29 PM Jul 22, 2014
 */
public abstract class ShopInfoParser extends AbstractParser {

	private static final Logger LOG = LoggerFactory.getLogger(ShopInfoParser.class);

	protected final HttpClientProvider provider;
	protected final ParseShopInfoBean bean;
	protected final ShopService service;

	protected HttpClientInvoker invoker;
	HttpInvokeResult result;

	public ShopInfoParser(final HttpClientProvider provider, final ParseShopInfoBean bean, final ShopService service) {
		this.provider = provider;
		this.bean = bean;
		this.service = service;
	}

	@Override
	public void parse() {
		// 轮询获取一个安全的IP地址
		final String ip = this.provider.getIpPools().pollingSafeAvaliableIp(this.bean.getShopType());
		this.invoker = this.provider.provide(this.bean.getShopType(), this.bean.getRequestUrl(),
				this.bean.getRefererUrl(), ip);
		// 不可用，则重试
		if (!this.invoker.isUsable()) {
			LOG.warn("can not get usable invoker from provider, ignore ParseShopInfoBean:{}", this.bean);
			return;
		}

		this.result = this.invoker.invoke();

		if (this.result.isOK()) {
			Logs.StatsResult ret = this.parse(this.result.getContentStringAndReset());
			Logs.statistics
					.warn("parser_shopinfo_sync. shopId:{}, shopUrl:{}, code:{}, reason:{}",
							this.bean.getOuerShopId(), this.bean.getShopUrl(),
							this.result.getStatusCode(), ret);
			return;
		}
		// 404错误，丢弃任务
		else if (this.result.getStatusCode() == 404) {
			LOG.warn("Error to invoke:{}, proxy ip:{}", this.bean, this.invoker.getIp());
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
		}
		// 需要鉴权则让当前代理闲置一下
		else if (AuthUtils.isAuthResponse(this.result.getResponse())) {
			LOG.warn("Redirect to authorize when invoke:" + result + ",will disable proxy ip:" 
					+ this.invoker.getIp(), result.getException());
			
			this.provider.disable(this.invoker);
		}
		// 未知情况
		else {
			LOG.error("Error to invoke:" + this.result + ", proxy ip:" + this.invoker.getIp() + ", ignore: "
					+ this.bean + "StatusCode:" + this.result.getStatusCode(), this.result.getException());
		}
		Logs.statistics
				.warn("parser_shopinfo_sync. shopId:{}, shopUrl:{}, code:{}, reason:{}",
						this.bean.getOuerShopId(), this.bean.getShopUrl(),
						this.result.getStatusCode(), Logs.StatsResult.FAIL);
	}

	String getCharset() {
		if (StringUtils.isNotBlank(this.getShopConfig().getCharset())) {
			return this.getShopConfig().getCharset();
		}
		if (this.result.getCharset() != null) {
			return this.result.getCharset().name();
		}
		return Statics.ENCODE;
	}

	Logs.StatsResult parse(final String htmlContent) {
		int matchTitle = 0;
		final Set<String> shopUrls = new HashSet<String>(5);
		final Set<String> shopNames = new HashSet<String>(5);
		final Set<String> nicknames = new HashSet<String>(5);
		final Set<String> userIds = new HashSet<String>(5);
		final Set<String> shopIds = new HashSet<String>(5);
		final List<String> scores = new ArrayList<String>(5);

		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(htmlContent);
		try {
			final Map<String, ParserConfig> configs = this.getParserConfigs();
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			// 啥都没有
			if (nodeList == null || nodeList.size() == 0) {
				Logs.maybeChangedRuleLogger.error("Nothing to parse from {}, maybe invalid ParseRule:{}",
						this.invoker.getUrl(), this.bean);
				return Logs.StatsResult.FAIL;
			}

			final String charset = this.getCharset();

			List<ParserConfig> matchConfigs;
			for (int a = 0; a < nodeList.size(); a++) {

				final Node aNode = nodeList.elementAt(a);

				// 网页title
				if (aNode instanceof TitleTag) {
					final String title = ((TitleTag) aNode).getStringText();

					if (LOG.isDebugEnabled()) {
						LOG.debug("title = {}", title);
					}

					if (StringUtils.contains(title, this.bean.getRnd())) {
						matchTitle++;
					}
					continue;
				}

				// 店铺地址
				matchConfigs = ParserUtils.match(aNode, Props.SHOP_URL, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					shopUrls.addAll(ParserUtils.getValues(aNode, matchConfigs, configs));
				}

				// 店铺名称
				matchConfigs = ParserUtils.match(aNode, Props.SHOP_NAME, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					shopNames.addAll(ParserUtils.getValues(aNode, matchConfigs, configs));
				}

				// 昵称
				matchConfigs = ParserUtils.match(aNode, Props.NICKNAME, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (String nickname : ParserUtils.getValues(aNode, matchConfigs, configs)) {
						nicknames.add(URLDecoders.decode(nickname, charset));
					}
				}

				// userId
				matchConfigs = ParserUtils.match(aNode, Props.USER_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (ParserConfig matchConfig : matchConfigs) {
						for (String value : ParserUtils.getValues(aNode, matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								userIds.add(value.trim());
							}
							else {
								final String[] kw = matchConfig.getExtractKw().split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value.indexOf(kw[1], start);
									if (stop > 0) {
										userIds.add(value.substring(start + kw[0].length(), stop).trim());
									}
									else {
										userIds.add(value.substring(start + kw[0].length()));
									}
								}
							}
						}
					}
				}

				// shopId
				matchConfigs = ParserUtils.match(aNode, Props.SHOP_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (ParserConfig matchConfig : matchConfigs) {
						for (String value : ParserUtils.getValues(aNode, matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								shopIds.add(value.trim());
							}
							else {
								final String[] kw = matchConfig.getExtractKw().split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value.indexOf(kw[1], start);
									if (stop > 0) {
										shopIds.add(value.substring(start + kw[0].length(), stop).trim());
									}
									else {
										shopIds.add(value.substring(start + kw[0].length()));
									}
								}
							}
						}
					}
				}

				// 店铺评分
				matchConfigs = ParserUtils.match(aNode, Props.SCORE, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					scores.addAll(ParserUtils.getValues(aNode, matchConfigs, configs));
				}
			}
		}
		catch (Exception e) {
			// 解析出错
			// 这里不输出html，太长了
			Logs.unpredictableLogger.error("Error to parse from " + this.invoker.getUrl()
					+ ", maybe invalide ParseRule: " + this.bean, e);
			return Logs.StatsResult.FAIL;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("shopUrls >> {}", JSON.toJSONString(shopUrls));
			LOG.debug("shopNames >> {}", JSON.toJSONString(shopNames));
			LOG.debug("matchTitle >> {}", matchTitle);
			LOG.debug("nicknames >> {}", JSON.toJSONString(nicknames));
			LOG.debug("userIds >> {}", JSON.toJSONString(userIds));
			LOG.debug("shopIds >> {}", JSON.toJSONString(shopIds));
			LOG.debug("scores >> {}", JSON.toJSONString(scores));
		}
		
		if (matchTitle == 0) {
			LOG.error("rnd can not match the title of the htm:{}, ignore {}", this.invoker.getUrl(), this.bean);
			return Logs.StatsResult.FAIL;
		}

		if (!nicknames.isEmpty()) {
			this.bean.setNickname(nicknames.iterator().next());
		}

		if (shopNames.isEmpty()) {
			Logs.maybeChangedRuleLogger.error("can not find the shopName, maybe Invalid ParseRule:{}", this.bean);
		}
		else {
			this.bean.setShopName(shopNames.iterator().next());
		}

		if (shopUrls.isEmpty()) {
			Logs.maybeChangedRuleLogger.error("can not find the shopUrl, maybe Invalid ParseRule:{}", this.bean);
			return Logs.StatsResult.FAIL;
		}
		
		if(!userIds.isEmpty()) {
			this.bean.setTUserId(userIds.iterator().next());
		}

		int shopUrlIndex = 0;
		// 先精确匹配
		for (String shopUrl : shopUrls) {
			if (StringUtils.endsWith(shopUrl, this.getShopConfig().getUrlEndsWith())) {
				this.bean.setShopUrl(shopUrl);
				++shopUrlIndex;
				break;
			}
		}

		if (shopUrlIndex == 0) {
			for (String shopUrl : shopUrls) {
				final int index = StringUtils.indexOf(shopUrl, this.getShopConfig().getUrlEndsWith());
				if (index > 0) {
					this.bean.setShopUrl(shopUrl.substring(0, index + this.getShopConfig().getUrlEndsWith().length()));
					++shopUrlIndex;
					break;
				}
			}
		}

		if (shopUrlIndex > 0) {
			final Shop shop = new Shop();
			shop.setReqFrom(this.bean.getReqFrom());
			shop.setOuerUserId(this.bean.getOuerUserId());
			shop.setOuerShopId(this.bean.getOuerShopId());
			shop.setName(this.bean.getShopName());
			shop.setNickname(this.bean.getNickname());
			shop.setShopUrl(this.bean.getShopUrl());
			shop.setShopType(this.bean.getShopType());
			shop.setUserId(this.bean.getTUserId());

			int scoreIndex = 0;
			if (CollectionUtils.isNotEmpty(scores)) {
				StringBuilder sb = new StringBuilder(12);
				for (String score : scores) {
					sb.append('-').append(score);
					if (++scoreIndex >= 3) {
						break;
					}
				}
				shop.setScore(sb.deleteCharAt(0).toString());
			}

			int index = 0;
			if (CollectionUtils.isNotEmpty(userIds)) {
				shop.setUserId(userIds.iterator().next());
			}
			else {
				++index;
			}

			if (CollectionUtils.isNotEmpty(shopIds)) {
				shop.setShopId(shopIds.iterator().next());
			}
			else {
				++index;
			}

			if (index > 0) {
				Logs.maybeChangedRuleLogger.error("can not find shopInfo, maybe invald ParseRule, ignore:{}",
						JSON.toJSONString(shop, SerializerFeature.PrettyFormat));
				return Logs.StatsResult.FAIL;
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("shop >> {}", JSON.toJSONString(shop, SerializerFeature.PrettyFormat));
			}

			this.service.save(shop);

			return Logs.StatsResult.SUCESS;
		}

		return Logs.StatsResult.FAIL;
	}
	
}

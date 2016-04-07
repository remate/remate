/**
 * 
 */
package com.vdlm.spider.parser.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.Config;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.cache.ItemCounters;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.core.IntRef;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.AbstractParser;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.Parser;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.Props;
import com.vdlm.spider.parser.config.ItemConfig;
import com.vdlm.spider.parser.config.ItemConfigs;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.service.ItemService;
import com.vdlm.spider.utils.AuthUtils;
import com.vdlm.spider.utils.CollectionTools;
import com.vdlm.spider.utils.JSONUtils;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 7:35:18 PM Jul 16, 2014
 */
public abstract class ItemParser extends AbstractParser {

	private static final Logger LOG = LoggerFactory.getLogger(ItemParser.class);

	protected final HttpClientProvider provider;
	protected final ParseItemTaskBean bean;
	protected final ItemService service;

	protected HttpClientInvoker invoker;

	public ItemParser(final HttpClientProvider provider,
			final ParseItemTaskBean bean, final ItemService service) {
		this.provider = provider;
		this.bean = bean;
		this.service = service;
	}

	/**
	 * <pre>
	 * 获取 item 配置
	 * </pre>
	 * 
	 * @return
	 */
	public abstract ItemConfig getItemConfig();

	// 重试
	void retry() {
		retry(true);
	}

	void retry(boolean incr) {
		// 是否需要重试
		if (!this.bean.isRetry()
				|| this.bean.retryTimes(incr) > Config.instance()
						.getRetryTimes()
				|| (System.currentTimeMillis() - this.bean
						.getCreateTimeMillis()) > Config.instance()
						.getRetryTimeout()) {
			LOG.warn("retry time over, Failed to retry ParseItemTask: {}",
					this.bean);
			TaskCounters.decrementAndGet(this.bean.getTaskId());
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.FAIL);
			return;
		}

		if (!(UrlCounters.delete(bean.getReqFrom(), bean.getOuerUserId(),
				bean.getOuerShopId(), bean.getItemUrl()) || ItemCounters
				.delete(bean.getReqFrom(), bean.getOuerUserId(),
						bean.getOuerShopId(), bean.getShopType(),
						bean.getItemId()))) {
			// 缓存清失败就不管了
			Logs.unpredictableLogger.warn(
					"Failed to retry invoker url:{}, clear cache failed.",
					this.bean.getRequestUrl());
			return;
		}

		boolean retry = false;
		try {
			retry = TaskQueues.getParseItemTaskQueue().add(
					this.bean.toJSONBytes());
			LOG.warn("Start to retry, ItemUrl:{}, retry times:{}",
					this.bean.getItemUrl(), this.bean.getRetryTimes());
		} catch (final Exception e) {
			retry = false;
			// unpredictable exception
		}
		if (!retry) {
			Logs.unpredictableLogger.warn(
					"Failed to retry submit ParseItemTask: {}", this.bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.FAIL);
		}
	}

	@Override
	final public void parse() {
		final long start = System.currentTimeMillis();
		Logs.itemParsedLogger.info("start parse item shopId:{}, itemUrl:{} ", 
				bean.getOuerShopId(), bean.getItemUrl());
		this.invoker = this.provider.provide(this.bean.getShopType(),
				this.bean.getItemUrl(), this.bean.getRefererUrl());
		// 不可用，则重试
		if (!this.invoker.isUsable()) {
			this.retry(false);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.ASYNC);
			return;
		}

		final HttpInvokeResult result = this.invoker.invoke();

		if (result.isOK()) {
			// maybe different source
			// check taobao or tmall
			final String tmallUrl = result.getTmallUrl();
			if (tmallUrl != null && !tmallUrl.equals(this.bean.getRefererUrl())) {
				// re invoke
				this.bean.setShopType(ShopType.TMALL);
				this.bean.setParserType(ParserType.TMALL_ITEM);
				this.bean.setItemUrl(tmallUrl);
				this.bean.setRefererUrl(tmallUrl);
				final Parser parser = new TmallItemParser(provider, bean, service);
				parser.parse();
				return;
			}
			boolean retry = false;
			
			try {
				final int ret = this.parse(result.getContentStringAndReset());
				switch (ret) {
				case SUCCESS:
					Logs.statistics.warn(
							"parser_item. shopId:{}, listUrl:{}, reason:{}",
							bean.getOuerShopId(), bean.getItemUrl(),
							Logs.StatsResult.SUCESS);
					Logs.itemParsedLogger.info("successfully parsed item shopId:{}, itemUrl:{}, spent {} ms ", 
							bean.getOuerShopId(), bean.getItemUrl(), System.currentTimeMillis() - start);
					break;
				case RETRY:
					Logs.statistics.warn(
							"parser_item. shopId:{}, listUrl:{}, reason:{}",
							bean.getOuerShopId(), bean.getItemUrl(),
							Logs.StatsResult.ASYNC);
					retry = true;
					Logs.itemParsedLogger.info("finished, but should retry item shopId:{}, itemUrl:{}, spent {} ms ", 
							bean.getOuerShopId(), bean.getItemUrl(), System.currentTimeMillis() - start);
					break;
				default:
					// 不会进到这里
					break;
				}
				return;
			} catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to parse item.htm, "
						+ this.bean, e);
				retry = false;
			}
			if (!retry) {
				TaskCounters.decrementAndGet(this.bean.getTaskId());
			}
		}
		// 404错误，丢弃任务
		else if (result.getStatusCode() == 404) {
			LOG.warn("Error to invoke: {}, proxy ip:{}", this.bean,
					this.invoker.getIp());
			//this.retry(true);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.FAIL);
		}
		// 禁止访问
		else if (result.getStatusCode() == 400 || result.getStatusCode() == 401
				|| result.getStatusCode() == 402
				|| result.getStatusCode() == 403
				|| result.getStatusCode() == 404
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
			this.bean.retryTimes();
			this.retry(true);
		}
		Logs.statistics.warn(
				"parser_item. shopId:{}, listUrl:{}, reason:{}",
				bean.getOuerShopId(), bean.getItemUrl(),
				Logs.StatsResult.ASYNC);
	}

	/**
	 * <pre>
	 * 解析 item.html
	 * </pre>
	 * 
	 * @param htmlContent
	 * @return
	 */
	int parse(final String htmlContent) {
		// 商品对象
		final Item item = new Item();
		item.setOuerUserId(this.bean.getOuerUserId());
		item.setOuerShopId(this.bean.getOuerShopId());
		item.setShopType(this.bean.getShopType());
		item.setItemUrl(this.bean.getItemUrl());
		item.setItemId(this.bean.getItemId());
		item.setReqFrom(this.bean.getReqFrom());
		// name / price
		final Set<String> names = new HashSet<String>(5);
		final Set<String> prices = new HashSet<String>(5);
		// userId / shopId
		final Set<String> userIds = new HashSet<String>(5);
		final Set<String> shopIds = new HashSet<String>(5);
		final Set<String> soldOuts = new HashSet<String>(5);
		final Set<String> notFound = new HashSet<String>(5);
		// sku信息
		final List<String> skuTypes = new ArrayList<String>(20);
		final List<List<String>> skuValues = new ArrayList<List<String>>();
		final List<List<String>> skuTexts = new ArrayList<List<String>>();
		final Set<String> skuImgs = new LinkedHashSet<String>();
		// 组图
		final Set<String> groupImgs = new LinkedHashSet<String>();
		// 页面标题
		String title = null;
		// 详情
		String details = null;
		// 详情图片
		final Set<String> detailImgs = new LinkedHashSet<String>();

		final org.htmlparser.Parser parser = HtmlParserProvider
				.createParser(htmlContent);
		try {
			final Map<String, ParserConfig> configs = this.getParserConfigs();
			// 获取配置
			final NodeFilter filter = NodeFilters
					.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			// 啥都没有
			if (nodeList == null || nodeList.size() == 0) {
				// 如果是认证页面，则重试
				if (this.isAuthHtml(htmlContent)) {
					this.provider.disable(this.invoker);
					this.retry(true);
					return RETRY;
				} else {
					Logs.maybeChangedRuleLogger
							.warn("Nothing to parse item.htm, maybe invalid ParseRule:"
									+ this.bean);
					this.retry(true);
					return RETRY;
				}
			}

			List<ParserConfig> matchConfigs;
			for (int a = 0; a < nodeList.size(); a++) {
				final Node aNode = nodeList.elementAt(a);

				// 网页title
				if (aNode instanceof TitleTag) {
					title = ((TitleTag) aNode).getStringText();
					// clear taobao or tmall style
					if(title.indexOf(this.getItemConfig().getNameFilterKey()) > 0) {
						title = title.replaceAll(this.getItemConfig().getNameFilterKey(), "");
					}
					continue;
				}
				
				// 宝贝是否存在(下架或已转移)
				matchConfigs = ParserUtils.match(aNode, Props.NOT_FOUND, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					notFound.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}
				
				// item名称
				matchConfigs = ParserUtils.match(aNode, Props.NAME, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					names.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}

				// sku 类型
				matchConfigs = ParserUtils
						.match(aNode, Props.SKU_TYPE, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					final List<String> types = ParserUtils.getValues(aNode,
							matchConfigs, configs);
					if (CollectionUtils.isEmpty(types)) {
						continue;
					}

					final NodeList skuNodeList = aNode.getChildren();
					if (skuNodeList == null || skuNodeList.size() == 0) {
						continue;
					}

					final List<ParserConfig> destConfigs = new ArrayList<ParserConfig>();
					for (final Map.Entry<String, ParserConfig> entry : configs
							.entrySet()) {
						if (StringUtils.equals(entry.getValue().getProp(),
								Props.SKU_VALUE)) {
							destConfigs.add(entry.getValue());
						} else if (StringUtils.equals(entry.getValue()
								.getProp(), Props.SKU_TEXT)) {
							destConfigs.add(entry.getValue());
						} else if (StringUtils.equals(entry.getValue()
								.getProp(), Props.SKU_IMG)) {
							destConfigs.add(entry.getValue());
						}
					}

					final NodeList nodeList2;
					try {
						nodeList2 = skuNodeList.extractAllNodesThatMatch(
								NodeFilters.getOrCreateNodeFilter(destConfigs),
								true);
					} catch (final Exception ignore) {
						continue;
					}
					if (nodeList2 == null || nodeList2.size() == 0) {
						continue;
					}

					final List<String> values = new ArrayList<String>();
					final List<String> texts = new ArrayList<String>();

					for (int i = 0; i < nodeList2.size(); i++) {
						final Node n = nodeList2.elementAt(i);

						matchConfigs = ParserUtils.match(n, Props.SKU_VALUE,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							values.addAll(ParserUtils.getValues(n,
									matchConfigs, configs));
						}

						matchConfigs = ParserUtils.match(n, Props.SKU_TEXT,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							texts.addAll(ParserUtils.getValues(n, matchConfigs,
									configs));
						}

						matchConfigs = ParserUtils.match(n, Props.SKU_IMG,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							skuImgs.addAll(ParserUtils.getValues(n,
									matchConfigs, configs));
						}
					}

					// 解析出问题了
					if (values.size() != texts.size() || values.isEmpty()) {
						Logs.maybeChangedRuleLogger
								.error("Error to Parse sku info from item.htm, maybe invalid ParseRule:{}",
										this.bean);
						continue;
					}

					if (skuTypes.add(types.get(0))) {
						skuValues.add(values);
						skuTexts.add(texts);
					}
				}

				// 价格
				matchConfigs = ParserUtils.match(aNode, Props.PRICE, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					prices.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}

				// 图片
				matchConfigs = ParserUtils.match(aNode, Props.IMG, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					groupImgs.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}

				// 是否下架
				matchConfigs = ParserUtils
						.match(aNode, Props.SOLD_OUT, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					soldOuts.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}

				// userId
				matchConfigs = ParserUtils.match(aNode, Props.USER_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode,
								matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								userIds.add(value.trim());
							} else {
								final String[] kw = matchConfig.getExtractKw()
										.split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value
											.indexOf(kw[1], start);
									if (stop > 0) {
										userIds.add(value.substring(
												start + kw[0].length(), stop)
												.trim());
									} else {
										userIds.add(value.substring(start
												+ kw[0].length()));
									}
								}
							}
						}
					}
				}

				// shopId
				matchConfigs = ParserUtils.match(aNode, Props.SHOP_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode,
								matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								shopIds.add(value.trim());
							} else {
								final String[] kw = matchConfig.getExtractKw()
										.split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value
											.indexOf(kw[1], start);
									if (stop > 0) {
										shopIds.add(value.substring(
												start + kw[0].length(), stop)
												.trim());
									} else {
										shopIds.add(value.substring(start
												+ kw[0].length()));
									}
								}
							}
						}
					}
				}

			}
		} catch (final Exception e) {
			// 解析出错
			// 这里不输出html，太长了
			Logs.unpredictableLogger.error(
					"Error to parse item.htm, maybe invalide ParseRule: "
							+ this.bean, e);
		}

		// 设置 item 名称
		if (CollectionUtils.isNotEmpty(names)) {
			item.setName(names.iterator().next());
		}
		// 没找到 item 名称，默认title
		if (StringUtils.isBlank(item.getName())) {
			item.setName(title);
		}
		if (CollectionUtils.isNotEmpty(userIds)) {
			item.setUserId(userIds.iterator().next());
		}
		if (CollectionUtils.isNotEmpty(shopIds)) {
			item.setShopId(shopIds.iterator().next());
		}
		
		if (CollectionUtils.isNotEmpty(notFound)) {
			LOG.warn("item not found:{}", this.bean);
			// 只记录未找到商品，不做往后处理
			item.setStatus(Statics.NOT_FOUND);
			this.persist(item, null, null);
			return SUCCESS;
		}

		if (StringUtils.isBlank(item.getName())
				|| StringUtils.isBlank(item.getUserId())
				|| StringUtils.isBlank(item.getShopId())) {
			LOG.warn("lack some important propery, retry later>>\n{}",
					JSON.toJSONString(item, true));
			return RETRY;
		}

		// 设置金额
		if (CollectionUtils.isNotEmpty(prices)) {
			Double price = null;
			for (final String e : prices) {
				try {
					final Double d = Double.valueOf(e);
					if (price == null || d > price) {
						price = d;
					}
				} catch (final Exception ignore) {
				}
			}
			item.setPrice(price);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("item >> {}", JSON.toJSONString(item));
			LOG.debug("names >> {}", JSON.toJSONString(names));
			LOG.debug("groupImgs >> {}", JSON.toJSONString(groupImgs));
			LOG.debug("skuTypes >> {}", JSON.toJSONString(skuTypes));
			LOG.debug("skuValues >> {}", JSON.toJSONString(skuValues));
			LOG.debug("skuTexts >> {}", JSON.toJSONString(skuTexts));
			LOG.debug("skuImgs >> {}", JSON.toJSONString(skuImgs));
			LOG.debug("userIds >> {}", JSON.toJSONString(userIds));
			LOG.debug("shopIds >> {}", JSON.toJSONString(shopIds));
		}

		if (CollectionUtils.isNotEmpty(soldOuts)) {
			LOG.warn("item sold out:{}", this.bean);
			// 下架商品，无需解析 sku
			item.setStatus(Statics.SOLD_OUT);
			this.persist(item, null, null);
			return SUCCESS;
		}

		// 提取详情
		final String remoteDescUrl = getApiDescUrl(htmlContent);
		if (StringUtils.isBlank(remoteDescUrl)) {
			Logs.maybeChangedRuleLogger
					.error("can not get desc remote url from item.htm, maybe invalid ParseRule: {}",
							this.bean);
		} else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("desc remoteUrl = {}", remoteDescUrl);
			}
			// 直接返回html格式
			details = this.getRemoteDescInfo(remoteDescUrl);

			// 20150119 该版本只解析出图片
			final List<String> descImgs = getImgsFromString(details);
			detailImgs.addAll(descImgs);

			// 20150126 该版本解析详情实现图文混排，以片段形式保存
			item.setFragments(getDescFragments(details));

		}

		// 提取图片//////////////////////////////////////////////////
		final List<Img> imgs = this.createImgList(groupImgs, skuImgs,
				detailImgs);

		if (LOG.isDebugEnabled()) {
			LOG.debug("imgs >> {}", JSON.toJSONString(imgs));
		}

		if (imgs.isEmpty()) {
			Logs.maybeChangedRuleLogger
					.error("Nothing img to parse item.htm, maybe invalid ParseRule:{}",
							this.bean);
		}

		// /////////////////////////////////////

		// 没有 sku
		if (skuTypes.isEmpty() || skuValues.isEmpty()) {
			// TODO 后面再考虑
			final Sku defaultSku = this.getDefaultSku(htmlContent);
			if (defaultSku != null) {
				this.persist(item, CollectionTools.asArrayList(defaultSku),
						imgs);
			} else {
				this.persist(item, null, imgs);
			}
			return SUCCESS;
		}

		// 格式化sku信息
		final List<Sku> skus = this
				.createSkuList(skuTypes, skuValues, skuTexts);

		// skuMapping
		item.setSkuTypes(skuTypes);
		item.setSkuTexts(skuTexts);

		// 根据 item.html 设置默认 sku 信息
		this.parseSku(htmlContent, item, skus);

		// 得到 远程获取sku库存、价格的 url地址////////////////////////
		final String remoteUrl = this.getSkuRemoteUrl(htmlContent);

		if (StringUtils.isBlank(remoteUrl)) {
			Logs.maybeChangedRuleLogger
					.error("can not get remote url from item.htm, maybe invalid ParseRule: {}",
							this.bean);
		} else {

			if (LOG.isDebugEnabled()) {
				LOG.debug("sku remoteUrl = {}", remoteUrl);
			}

			final String skuInfo = this.getRemoteSkuInfo(remoteUrl);

			if (StringUtils.isNotBlank(skuInfo)) {
				// 根据获取都的远程数据，格式化sku信息
				this.parseRemoteSku(skuInfo, item, skus);
			}
		}

		if (CollectionUtils.isEmpty(skus)
				|| CollectionUtils.isEmpty(imgs)) {
			LOG.warn("lack some important propery, retry later>>\n{}",
					JSON.toJSONString(item, true));
			return RETRY;
		}
		
		// if groupImgs is null
		if(groupImgs == null || groupImgs.size() == 0) {
			item.setStatus(Statics.INCOMPLETED_INFO);
		}

		// 持久化
		this.persist(item, skus, imgs);

		return SUCCESS;
	}

	/**
	 * <pre>
	 * 获取默认的sku对象
	 * </pre>
	 * 
	 * @param htmlContent
	 * @return
	 */
	Sku getDefaultSku(String htmlContent) {
		// 得到 远程获取sku库存、价格的 url地址////////////////////////
		final String remoteUrl = this.getSkuRemoteUrl(htmlContent);

		if (StringUtils.isBlank(remoteUrl)) {
			Logs.maybeChangedRuleLogger
					.error("can not get remote url from item.htm, maybe invalid ParseRule: {}",
							this.bean);
			return null;
		} else {

			if (LOG.isDebugEnabled()) {
				LOG.debug("sku remoteUrl = {}", remoteUrl);
			}

			final String skuInfo = this.getRemoteSkuInfo(remoteUrl);

			if (StringUtils.isNotBlank(skuInfo)) {
				final Double price = this.getRemoteDefaultSkuPrice(skuInfo);
				final Integer stock = this.getRemoteDefaultSkuStock(skuInfo);

				final Sku sku = new Sku();
				sku.setAmount(stock);
				sku.setPrice(price);
				sku.setSpec("无");
				sku.setOrigSpec("");
				return sku;
			}
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取默认库存
	 * </pre>
	 * 
	 * @param skuInfo
	 * @return
	 */
	Integer getRemoteDefaultSkuStock(final String skuInfo) {
		// 获取库存//////////////////////////////////
		final String keyword = this.getItemConfig()
				.getRemoteDefaultStockKeyword();
		final String key = this.getItemConfig().getRemoteDefaultStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to get default stock of sku, {}",
					this.getItemConfig());
			return null;
		}

		final String jsonString = this.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger
					.warn("can not find default stock of sku by keyword, maybe invalid ParseRule: {}",
							this.bean);
			return null;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error(
					"Invalid default stock jsonString, maybe invalid ParseRule:"
							+ this.bean, e);
			return null;
		}
		final List<String> values = JSONUtils.extractList(json, null, key);

		final Double d = ParserUtils.getMaxValue(values);
		if (d == null) {
			return null;
		}
		return d.intValue();
	}

	/**
	 * <pre>
	 * 获取默认价格
	 * </pre>
	 * 
	 * @param skuInfo
	 * @return
	 */
	Double getRemoteDefaultSkuPrice(final String skuInfo) {
		// 获取价格//////////////////////////////////
		final String keyword = this.getItemConfig()
				.getRemoteDefaultPriceKeyword();
		final String key = this.getItemConfig().getRemoteDefaultPriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to get default price of sku, {}",
					this.getItemConfig());
			return null;
		}

		final String jsonString = this.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger
					.error("can not find default price of sku by keyword, maybe invalid ParseRule: {}",
							this.bean);
			return null;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error(
					"Invalid default price jsonString, maybe invalid ParseRule:"
							+ this.bean, e);
			return null;
		}
		final List<String> values = JSONUtils.extractList(json, null, key);

		return ParserUtils.getMaxValue(values);
	}

	/**
	 * 解析获取desc信息的 远程url地址
	 */
	String getApiDescUrl(String htmlContent) {
		String url = "";
		try {
			String descKey = null;
			final int index;
			switch (this.bean.getShopType()) {
			case TAOBAO:
				descKey = ItemConfigs.getOrCreateTaobaoItemConfig()
						.getApiDescKey();
				
				index = htmlContent.indexOf(descKey);
				if (index <= 0) {
					return null;
				}
				try {
					final int start = index - 2;
					final int end = htmlContent.indexOf("\"", start);

					url = htmlContent.substring(start, end);
				} catch (final Exception e) {
					Logs.maybeChangedRuleLogger
							.error("Can't find descRemoteUrl from item.htm, maybe invalid ParseRule: {}",
									this.bean);
				}
				break;
			case TMALL:
				descKey = ItemConfigs.getOrCreateTmallItemConfig()
						.getApiDescKey();
				
				index = htmlContent.indexOf(descKey);
				if (index <= 0) {
					return null;
				}
				try {
					final int start = index + descKey.length() + 3;
					final int end = htmlContent.indexOf("\"", start);

					url = htmlContent.substring(start, end);
				} catch (final Exception e) {
					Logs.maybeChangedRuleLogger
							.error("Can't find descRemoteUrl from item.htm, maybe invalid ParseRule: {}",
									this.bean);
				}
				break;
			default:
				break;
			}
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger
					.error("Error to getDescRemoteUrl from item.htm, maybe invalid ParseRule: {}",
							this.bean);
		}
		return url;
	}

	/**
	 * 获取远程 desc 信息
	 */
	String getRemoteDescInfo(String remoteUrl) {
		remoteUrl = ParserUtils.formatUrl(remoteUrl);
		// 请求参数与 首次 保持一致
		final HttpClientInvoker invoker = this.provider.provide(remoteUrl,
				this.invoker);

		final HttpInvokeResult result = invoker.invoke();

		if (result.isOK()) {
			return formatDesc(result.getContentString());
		}
		// 需要鉴权则让当前代理闲置一下
		else if (AuthUtils.isAuthResponse(result.getResponse())) {
			this.provider.disable(this.invoker);
			// TODO 这里需要重试？
		}
		if (result.getException() != null) {
			Logs.maybeChangedRuleLogger.error("Error to request " + result
					+ ", maybe invalid ParseRule:" + this.bean,
					result.getException());
		} else {
			Logs.maybeChangedRuleLogger.error(
					"Failed to request {} , maybe invalid ParseRule: {}",
					result, this.bean);
		}
		return null;
	}

	/**
	 * 
	 * 解析desc信息 取var desc='xxx'里面的内容
	 */
	String formatDesc(String remoteDesc) {
		String desc = null;
		final int start = remoteDesc.indexOf("'") + 1; // '字符
		if (start <= 0) {
			return null;
		}
		final int end = remoteDesc.indexOf("'", start);
		desc = remoteDesc.substring(start, end);

		return desc;
	}

	/**
	 * 获取字符串中的图片列表
	 */
	@SuppressWarnings("unchecked")
	List<String> getImgsFromString(String sString) {
		if (StringUtils.isBlank(sString)) {
			return Collections.EMPTY_LIST;
		}
		final List<String> list = new ArrayList<String>();
		final Pattern p = Pattern.compile("src=\"(.*?)\"");
		final Matcher m = p.matcher(sString);
		while (m.find()) {
			if (m.group(1) != null) {
				// 解析只需要url,摒弃gif格式图片
				if (m.group().contains(".gif")) {
					continue;
				}
				list.add(m.group(1));
			}
		}
		return list;
	}

	/**
	 * 获取字符串中标签文本，类 strip-tags
	 */
	String getTextFromString(String sString) {
		// step1  过滤 <a...>*</a>
		sString = sString.replaceAll("<a[^>]+>[^<]*</a>", "");
		// step2  过滤 <...>*<.../>留下内容
		sString = sString.replaceAll("\\<.*?\\>", "");
		
		return sString;
	}

	static final String FRAG_TYPE_TEXT = "text";
	static final String FRAG_TYPE_IMG = "img";

	String getParagraphMode(String p) {
		return p.contains("<img") && p.contains(" src=") ? "img" : "text";
	}

	/**
	 * 解析详情片段格式
	 */
	@SuppressWarnings("unchecked")
	List<FragmentVO> getDescFragments(String sDesc) {
		if (StringUtils.isBlank(sDesc)) {
			return Collections.EMPTY_LIST;
		}
		final List<FragmentVO> fragments = new ArrayList<FragmentVO>();
		List<FragmentImageVO> fragImgs = new ArrayList<FragmentImageVO>();
		FragmentVO fragment = null;

		String description = "";
		String lastMode = "";
		int i = 0;
		int j = 0;

		// 先解析<p></p>标签
		final Matcher ma = Pattern.compile("<p.*?>([\\s\\S]*?)</p>").matcher(sDesc);
		while (ma.find()) {
			String p = ma.group(1);
			if (p != null) {
				final String thisMode = getParagraphMode(p); // text, img
				if (thisMode.equals(FRAG_TYPE_TEXT)) {
					// 只提取文本
					p = getTextFromString(p);
				} else if (thisMode.equals(FRAG_TYPE_IMG)) {
					// 只提取url
					// 可能一个<p></p>中包含多个img
				}
				if (!thisMode.equals(lastMode)) {
					if (fragment != null) {
						fragments.add(fragment);
					}

					fragment = new FragmentVO();
					fragImgs = new ArrayList<FragmentImageVO>();
					description = "";
					i++;
				}
				fragment.setName("段落 " + i);
				fragment.setShowModel(true); // 默认为文字靠前
				fragment.setIdx(i);

				// fragment.setShopId(Long.toString(IdTypeHandler.decode(this.bean.getOuerShopId())));
				fragment.setShopId(this.bean.getOuerShopId());

				if (FRAG_TYPE_IMG.equals(thisMode)) {
					// 图片处理
					if (getImgsFromString(p).size() > 0) {
						for (int k = 0; k < getImgsFromString(p).size(); k++) {
							final FragmentImageVO fragmentImg = new FragmentImageVO();
							fragmentImg.setImgUrl(ParserUtils.formatUrl(getImgsFromString(p).get(k)));
							fragmentImg.setIdx(j++);
							fragImgs.add(fragmentImg);
						}
					}
					fragment.setImgs(fragImgs);
				} else {
					// 文本处理
					description += p + "\n";
					fragment.setDescription(description);
				}
				lastMode = thisMode;
			}
		}
		// 最后一个
		fragments.add(fragment);

		return fragments;
	}

	/**
	 * <pre>
	 * 格式化sku信息
	 * </pre>
	 * 
	 * @param htmlContent
	 * @param item
	 * @param skus
	 */
	abstract void parseSku(String htmlContent, Item item, List<Sku> skus);

	/**
	 * <pre>
	 * 格式化sku信息
	 * </pre>
	 * 
	 * @param skuInfo
	 * @param item
	 * @param skus
	 */
	abstract void parseRemoteSku(String skuInfo, Item item, List<Sku> skus);

	/**
	 * <pre>
	 * 持久化, TODO 后面持久化操作可以做成异步
	 * </pre>
	 * 
	 * @param item
	 * @param skus
	 * @param imgs
	 */
	protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
		if (skus == null) {
			skus = new ArrayList<Sku>(1);
		}
		if (imgs == null) {
			imgs = new ArrayList<Img>(1);
		}

		// 下架商品
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			this.service.save(item, skus, imgs);
			return;
		}
		// 未找到商品
		if (item.getStatus() != null && item.getStatus() == Statics.NOT_FOUND) {
			this.service.save(item, skus, imgs);
			return;
		}

		// 校验价格、sku信息 并 入库
		Double maxPrice = null;
		Integer amount = 0;

		int nullIndex = 0;
		for (final Sku sku : skus) {
			if (sku.getAmount() == null) {
				sku.setAmount(99);
				++nullIndex;
			} else {
				amount += sku.getAmount();
			}

			if (sku.getPrice() == null) {
				if (item.getPrice() == null) {
					++nullIndex;
					sku.setPrice(0.0);
				} else {
					sku.setPrice(item.getPrice());
				}
			} else if (maxPrice == null || sku.getPrice() > maxPrice) {
				maxPrice = sku.getPrice();
			}
		}

		if (maxPrice != null) {
			item.setPrice(maxPrice);
		}
		if (item.getAmount() == null) {
			item.setAmount(amount);
		}

		// 判断 item 有效性
		if (nullIndex > 0) {
			item.setStatus(Statics.INCOMPLETED_INFO);

			if (item.getPrice() == null) {
				item.setPrice(0.0);
			}
		} else if (item.getStatus() == null) {
			item.setStatus(Statics.NORMAL);
		}
		if (CollectionUtils.isEmpty(skus)) {
			final Sku sku = new Sku();
			sku.setAmount(0);
			sku.setPrice(0.0);
			sku.setSpec("无");
			sku.setOrigSpec("");
			skus.add(sku);
		}

		if (item.getPrice() == null) {
			item.setPrice(0.0);
			item.setStatus(Statics.INCOMPLETED_INFO);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("item >> {}", JSON.toJSONString(item));
			for (final Sku sku : skus) {
				LOG.debug("sku >> {}", JSON.toJSONString(sku));
			}
			for (final Img img : imgs) {
				LOG.debug("img >> {}", JSON.toJSONString(img));
			}
		}

		// 保存 item
		this.service.save(item, skus, imgs);
	}

	/**
	 * <pre>
	 * 格式化 图片信息
	 * </pre>
	 * 
	 * @param groupImgs
	 * @param skuImgs
	 * @return
	 */
	List<Img> createImgList(Collection<String> groupImgs,
			Collection<String> skuImgs, Collection<String> detailImgs) {
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		// 默认限制n张，默认组图，默认顺序
		final int imgSize = this.getItemConfig().getImgSize();
		int t = 0;
		for (final String u : groupImgs) {
			final String imgUrl = ParserUtils.formatImgUrl(u);
			// 组图不做size限制
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_GROUP);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(1);
				imgs.add(img);
				++t;
			}
		}
		// sku图不做size限制
		for (final String u : skuImgs) {
			final String imgUrl = ParserUtils.extractImgUrlFromStyle(u);
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_SKU);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(2);
				imgs.add(img);
				++t;
			}
		}
		int i = 0;
		for (final String u : detailImgs) {
			final String imgUrl = ParserUtils.formatUrl(u);
			if (imgUrls.add(imgUrl) && i <= imgSize) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_DETAIL);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(3);
				imgs.add(img);
				++t;
				++i;
			}
		}
		return imgs;
	}

	/**
	 * <pre>
	 * 根据关键字 获取JSON 字符串
	 * </pre>
	 * 
	 * @param text
	 * @param kw
	 * @return
	 */
	String getJSONString(final String text, final String kw) {
		final int index = text.indexOf(kw);
		if (index < 0) {
			return null;
		}
		final int bgn = text.indexOf('{', index);
		if (index < 0) {
			return null;
		}

		int left = 0;
		int right = 0;
		int end;
		// 找到完整的json串
		for (int i = bgn;; i++) {
			final char ch = text.charAt(i);
			if (ch == '{') {
				++left;
			} else if (ch == '}') {
				++right;
			}
			if (left == right) {
				end = i;
				break;
			}
		}
		return text.substring(bgn, end + 1);
	}

	/**
	 * <pre>
	 * 得到 获取sku信息的 远程url地址
	 * </pre>
	 * 
	 * @param htmlContent
	 * @return
	 */
	String getSkuRemoteUrl(String htmlContent) {
		final int index = htmlContent.indexOf(this.getItemConfig()
				.getRemoteUrlStartWith());

		if (index <= 0) {
			return null;
		}

		try {
			// 理论上不会出现 IndexOutOfBoundsException
			final int ch = htmlContent.charAt(index - 1);
			// 理论上不会出现-1
			final int index2 = htmlContent.indexOf(ch, index);

			return ParserUtils.formatUrl(htmlContent.substring(index, index2));
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger
					.error("Error to getSkuRemoteUrl from item.htm, maybe invalid ParseRule: {}",
							this.bean);
			return null;
		}
	}

	/**
	 * <pre>
	 * 获取远程 sku 信息
	 * </pre>
	 * 
	 * @param remoteUrl
	 * @return
	 */
	String getRemoteSkuInfo(String remoteUrl) {
		// 请求参数与 首次 保持一致
		final HttpClientInvoker invoker = this.provider.provide(remoteUrl,
				this.invoker);

		final HttpInvokeResult result = invoker.invoke();

		if (result.isOK()) {
			return result.getContentString();
		}
		// 需要鉴权则让当前代理闲置一下
		else if (AuthUtils.isAuthResponse(result.getResponse())) {
			this.provider.disable(this.invoker);
			// TODO 这里需要重试？
		}

		if (result.getException() != null) {
			Logs.maybeChangedRuleLogger.error("Error to request " + result
					+ ", maybe invalid ParseRule:" + this.bean,
					result.getException());
		} else {
			Logs.maybeChangedRuleLogger.error(
					"Failed to request {} , maybe invalid ParseRule: {}",
					result, this.bean);
		}
		

		return null;
	}

	/**
	 * <pre>
	 * 格式化 sku 信息
	 * </pre>
	 * 
	 * @param skuTypes
	 * @param skuValues
	 * @param skuTexts
	 * @return
	 */
	List<Sku> createSkuList(List<String> skuTypes,
			List<List<String>> skuValues, List<List<String>> skuTexts) {
		int size = 1;
		for (final List<String> skuValueList : skuValues) {
			size *= skuValueList.size();
		}

		final List<IntRef> indexs = new ArrayList<IntRef>(skuTypes.size());
		for (int i = 0; i < skuTypes.size(); i++) {
			indexs.add(new IntRef());
		}

		final List<Sku> results = new ArrayList<Sku>(size);

		for (int i = 0; i < size; i++) {
			final Sku result = new Sku();

			final StringBuilder spec = new StringBuilder();
			final StringBuilder origSpec = new StringBuilder();

			for (int a = 0; a < skuTypes.size(); a++) {
				final int index = indexs.get(a).get();

				origSpec.append(';').append(skuValues.get(a).get(index));
				spec.append(';').append(skuTypes.get(a)).append(':')
						.append(skuTexts.get(a).get(index));
			}

			result.setSpec(spec.deleteCharAt(0).toString());
			result.setOrigSpec(origSpec.deleteCharAt(0).toString());

			results.add(result);

			for (int a = skuTypes.size() - 1; a >= 0; a--) {
				final int index = indexs.get(a).incrementAndGet();

				if (index >= skuValues.get(a).size()) {
					indexs.get(a).set(0);
				} else {
					break;
				}

			}
		}
		return results;
	}	
}

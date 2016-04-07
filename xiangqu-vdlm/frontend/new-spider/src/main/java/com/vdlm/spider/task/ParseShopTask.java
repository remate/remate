package com.vdlm.spider.task;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.Props;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.task.helper.ShopParseHelper;
import com.vdlm.spider.task.resubmit.QueueEventType;
import com.vdlm.spider.task.resubmit.TaskTrackEvent;
import com.vdlm.spider.utils.Logs;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.utils.URLDecoders;
import com.vdlm.spider.wdetail.ItemResponse;

/**
 * // 解析店铺
 
void parseShop() {

    // 通过ParseShopThreadPool中的线程从池队列中取出一个task

    // 无需手工编写代码

    ParseShopTask task = getTaskFromThreadPoolQueue();

    if (task != null) {

       // 从task中取出http result内容

       String content = getContent(task);

       // 解析出shop对象

       Shop shop = parse(content);

       // 持久化shop

       save(shop);

       // 取出item list

       List<String> itemList = extractItemList(content);

       // 为每一个item创建相应的item请求任务，并置入相应对列

       CrawlItemTask task1;

       for (String item : itemList) {

           task1 = createCrawlItemTask(item);

           CrawlItemQueue.add(task1);

       }

    }

}
 * @author: chenxi
 */

public class ParseShopTask extends ParsableTask<Shop> {

	public ParseShopTask(BusSignalManager bsm, 
						   HttpClientInvoker invoker,
						   HttpInvokeResult result,
						   CrawlableTask<ShopTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected Shop parse() {
        final ShopTaskBean bean = (ShopTaskBean) getBean();
        
        if (StringUtils.isNotBlank(bean.getItemId())) {  // 取手淘数据
	        ItemResponse resp;
			try {
				resp = ObjectConvertUtils.fromString(result.getContentStringAndReset(), ItemResponse.class);
			} catch (final Exception e) {
				LOG.error("parse witem failed", e);
				logStatistics(Logs.StatsResult.FAIL, bean);
				final TaskTrackEvent<ShopTaskBean> event = 
						new TaskTrackEvent<ShopTaskBean>(QueueEventType.ENQUEUE_FAILED, bean, ShopTaskBean.class);
				bsm.signal(event);
				return null;
			}
			final String itemTitle = resp.getData().getItemInfoModel().getTitle();
	
			// 首次搬家
			//if ( !StringUtils.contains(itemTitle, bean.getRnd())) {
			if (bean.getRnd() != null && !StringUtils.contains(itemTitle, bean.getRnd())) {
				LOG.error("rnd can not match the title of the htm:{}, ignore {}", this.invoker.getUrl(), bean);
				logStatistics(Logs.StatsResult.FAIL, bean);
				final TaskTrackEvent<ShopTaskBean> event = 
						new TaskTrackEvent<ShopTaskBean>(QueueEventType.ENQUEUE_FAILED, bean, ShopTaskBean.class);
				bsm.signal(event);
				return null;
			}
			
	        bean.setTUserId(resp.getData().getSeller().getUserNumId());
			final Shop shop = new Shop();
			shop.setReqFrom(bean.getReqFrom());
			shop.setOuerUserId(bean.getOuerUserId());
			shop.setOuerShopId(bean.getOuerShopId());
			shop.setShopType(bean.getShopType());
			shop.setName(resp.getData().getSeller().getShopTitle());
			shop.setNickname(resp.getData().getSeller().getNick());
			shop.setShopId(resp.getData().getSeller().getShopId());
			shop.setShopUrl(generateShopUrl(resp.getData().getSeller().getShopId()));
			shop.setUserId(bean.getTUserId());
			shop.setScore(resp.getData().getSeller().getScore());
			return shop;
        } // else pc html
		int matchTitle = 0;
		final Set<String> shopUrls = new HashSet<String>(5);
		final Set<String> shopNames = new HashSet<String>(5);
		final Set<String> nicknames = new HashSet<String>(5);
		final Set<String> userIds = new HashSet<String>(5);
		final Set<String> shopIds = new HashSet<String>(5);
		final List<String> scores = new ArrayList<String>(5);

		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(result.getContentStringAndReset());
		try {
			final Map<String, ParserConfig> configs = ParserConfigProviders.getParserShopConfigs(bean);
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			// 啥都没有
			if (nodeList == null || nodeList.size() == 0) {
				Logs.maybeChangedRuleLogger.error("Nothing to parse from {}, maybe invalid ParseRule:{}",
						this.invoker.getUrl(), bean);
				logStatistics(Logs.StatsResult.FAIL, bean);
				return null;
			}

			final String charset = ShopParseHelper.getCharset(bean, result);

			List<ParserConfig> matchConfigs;
			for (int a = 0; a < nodeList.size(); a++) {

				final Node aNode = nodeList.elementAt(a);

				// 网页title
				if (aNode instanceof TitleTag) {
					final String title = ((TitleTag) aNode).getStringText();

					if (LOG.isDebugEnabled()) {
						LOG.debug("title = {}", title);
					}

					// 如果是首次搬家才需要判断
					if (bean.getRnd() != null && StringUtils.contains(title, bean.getRnd())) {
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
					for (final String nickname : ParserUtils.getValues(aNode, matchConfigs, configs)) {
						nicknames.add(URLDecoders.decode(nickname, charset));
					}
				}

				// userId
				matchConfigs = ParserUtils.match(aNode, Props.USER_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode, matchConfig, configs)) {
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
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode, matchConfig, configs)) {
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
		catch (final Exception e) {
			// 解析出错
			// 这里不输出html，太长了
			Logs.unpredictableLogger.error("Error to parse from " + this.invoker.getUrl()
					+ ", maybe invalide ParseRule: " + bean, e);
			logStatistics(Logs.StatsResult.FAIL, bean);
			return null;
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
			LOG.error("rnd can not match the title of the htm:{}, ignore {}", this.invoker.getUrl(), bean);
			logStatistics(Logs.StatsResult.FAIL, bean);
			final TaskTrackEvent<ShopTaskBean> event = 
					new TaskTrackEvent<ShopTaskBean>(QueueEventType.ENQUEUE_FAILED, bean, ShopTaskBean.class);
			bsm.signal(event);
			return null;
		}

		if (!nicknames.isEmpty()) {
			bean.setNickname(nicknames.iterator().next());
		}

		if (shopNames.isEmpty()) {
			Logs.maybeChangedRuleLogger.error("can not find the shopName, maybe Invalid ParseRule:{}", bean);
		}
		else {
			bean.setShopName(shopNames.iterator().next());
		}

		if (shopUrls.isEmpty()) {
			Logs.maybeChangedRuleLogger.error("can not find the shopUrl, maybe Invalid ParseRule:{}", bean);
			logStatistics(Logs.StatsResult.FAIL, bean);
			return null;
		}
		
		if(!userIds.isEmpty()) {
			bean.setTUserId(userIds.iterator().next());
		}

		int shopUrlIndex = 0;
		// 先精确匹配
		for (final String shopUrl : shopUrls) {
			if (StringUtils.endsWith(shopUrl, ParserConfigProviders.getShopConfig(bean).getUrlEndsWith())) {
				bean.setShopUrl(shopUrl);
				++shopUrlIndex;
				break;
			}
		}

		if (shopUrlIndex == 0) {
			for (final String shopUrl : shopUrls) {
				final int index = StringUtils.indexOf(shopUrl, ParserConfigProviders.getShopConfig(bean).getUrlEndsWith());
				if (index > 0) {
					bean.setShopUrl(shopUrl.substring(0, index + ParserConfigProviders.getShopConfig(bean).getUrlEndsWith().length()));
					++shopUrlIndex;
					break;
				}
			}
		}

		if (shopUrlIndex > 0) {
			final Shop shop = new Shop();
			shop.setReqFrom(bean.getReqFrom());
			shop.setOuerUserId(bean.getOuerUserId());
			shop.setOuerShopId(bean.getOuerShopId());
			shop.setName(bean.getShopName());
			shop.setNickname(bean.getNickname());
			shop.setShopUrl(bean.getShopUrl());
			shop.setShopType(bean.getShopType());
			shop.setUserId(bean.getTUserId());

			int scoreIndex = 0;
			if (CollectionUtils.isNotEmpty(scores)) {
				final StringBuilder sb = new StringBuilder(12);
				for (final String score : scores) {
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
				logStatistics(Logs.StatsResult.FAIL, bean);
				return null;
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("shop >> {}", JSON.toJSONString(shop, SerializerFeature.PrettyFormat));
			}

//			this.service.save(shop);

			logStatistics(Logs.StatsResult.SUCESS, bean);
			return shop;
		}

		logStatistics(Logs.StatsResult.FAIL, bean);
		return null;
	}

	@Override
	protected void persistent(Shop entity) {
		if (entity != null) {
			bsm.signal(entity);
		}
	}

	@Override
	protected void furtherCrawl(Shop entity) {
		final ItemListTaskBean task = TaskBeanFactory.createItemListTaskBean(entity.getId(), getBean());
		bsm.signal(task);
	}
	
	private String generateShopUrl(String shopId) {
		return "http://shop" + shopId + ".taobao.com";
	}

}

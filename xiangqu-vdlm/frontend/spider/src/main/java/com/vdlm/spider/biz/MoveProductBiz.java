/**
 * 
 */
package com.vdlm.spider.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vdlm.spider.Config;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseShopInfoBean;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.config.ItemListConfigs;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.parser.shop.info.ShopInfoParser;
import com.vdlm.spider.parser.shop.info.TaobaoShopInfoParser;
import com.vdlm.spider.parser.shop.info.TmallShopInfoParser;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.service.ShopService;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:20:40 PM Aug 11, 2014
 */
@Component
public class MoveProductBiz {

	private Logger log = LoggerFactory.getLogger(getClass());

	private HttpClientProvider provider;

	private ShopService shopService;

	@Autowired
	public void setShopService(ShopService shopService) {
		this.shopService = shopService;
	}

	@Autowired
	public void setProvider(HttpClientProvider provider) {
		this.provider = provider;
	}

	/**
	 * <pre>
	 * 加入本地队列中
	 * </pre>
	 * @param bean
	 * @return
	 */
	boolean add2TaskQueue(ParseShopTaskBean bean) {
		switch (bean.getShopType()) {
		case TAOBAO:
			bean.setParserType(ParserType.TAOBAO_ITEMLIST);
			bean.setRequestUrl(ItemListConfigs
					.getOrCreateTaobaoItemListConfig()
					.getUrl2(
							String.valueOf(Config.instance().getUrl2StartSize()),
							String.valueOf(Config.instance().getUrl2EndSize()),
							bean.getTUserId()));
			bean.setRefererUrl(ItemListConfigs
					.getOrCreateTaobaoItemListConfig().getRefererUrl());
			break;
		case TMALL:
			bean.setParserType(ParserType.TMALL_ITEMLIST);
			bean.setRequestUrl(ItemListConfigs
					.getOrCreateTaobaoItemListConfig()
					.getUrl2(
							String.valueOf(Config.instance().getUrl2StartSize()),
							String.valueOf(Config.instance().getUrl2EndSize()),
							bean.getTUserId()));
			bean.setRefererUrl(ItemListConfigs.getOrCreateTmallItemListConfig()
					.getRefererUrl());
			break;
		default:
			// 不会到这里
			return false;
		}
		
		// 加入至队列
		boolean ret = TaskQueues.getParseShopTaskQueue().add(bean.toJSONBytes());
		if(!ret) {
			Logs.statistics.warn(
					"parser_itemlist. shopId:{}, shopurl:{}, reason:{}",
					bean.getOuerShopId(), bean.getShopUrl(),
					Logs.StatsResult.FAIL);
		}
		return ret;
	}

	/**
	 * <pre>
	 * 获取店铺信息
	 * </pre>
	 * @param bean
	 * @param shopType
	 * @param rnd
	 * @param itemId
	 */
	void setShopInfo(ParseShopTaskBean bean, ShopType shopType, String rnd, String itemId) {
		switch (shopType) {
		case TAOBAO:
			this.setShopInfo0(bean, shopType, ShopConfigs.getOrCreateTaobaoShopConfig().getItemUrl(itemId), rnd);
			break;
		case TMALL:
			this.setShopInfo0(bean, shopType, ShopConfigs.getOrCreateTmallShopConfig().getItemUrl(itemId), rnd);
			break;
		case TAOBAO_OR_TMALL:
			this.setShopInfo0(bean, ShopType.TAOBAO, ShopConfigs.getOrCreateTaobaoShopConfig().getItemUrl(itemId), rnd);
			if (StringUtils.isBlank(bean.getShopUrl())) {
				this.setShopInfo0(bean, ShopType.TMALL, ShopConfigs.getOrCreateTmallShopConfig().getItemUrl(itemId),
						rnd);
			}
			// 以实际URL为准,目前 taobao 会重定向至 tmall
			else if (StringUtils.endsWith(bean.getShopUrl(), ShopConfigs.getOrCreateTmallShopConfig().getUrlEndsWith())) {
				bean.setShopType(ShopType.TMALL);
			}
			break;
		}
	}

	void setShopInfo0(ParseShopTaskBean bean, ShopType shopType, String requestUrl, String rnd) {
		final ParseShopInfoBean parseBean = new ParseShopInfoBean();
		parseBean.setOuerUserId(bean.getOuerUserId());
		parseBean.setOuerShopId(bean.getOuerShopId());
		parseBean.setShopType(shopType);
		parseBean.setRequestUrl(requestUrl);
		parseBean.setRnd(rnd);
		parseBean.setReqFrom(bean.getReqFrom());

		final ShopInfoParser parser;
		switch (shopType) {
		case TAOBAO:
			parser = new TaobaoShopInfoParser(this.provider, parseBean, shopService);
			break;
		case TMALL:
			parser = new TmallShopInfoParser(this.provider, parseBean, shopService);
			break;
		default:
			return;
		}
		parser.parse();

		bean.setShopType(shopType);
		bean.setShopUrl(parseBean.getShopUrl());
		bean.setShopName(parseBean.getShopName());
		bean.setNickname(parseBean.getNickname());
		bean.setTUserId(parseBean.getTUserId());
	}

	/**
	 * <pre>
	 * 搬家
	 * 根据title验证随机码
	 * 保存偶尔用户与第三方店铺的关系
	 * 搬家店铺商品
	 * </pre>
	 * @param deviceType
	 * @return
	 */
	public Map<String, Object> moveByRnd(final ParseShopInfoBean bean) {
		final Map<String, Object> result = new HashMap<String, Object>(5);

		// 获取店铺信息
		this.setShopInfo(bean, bean.getShopType(), bean.getRnd(), bean.getItemId());
		if (StringUtils.isBlank(bean.getShopUrl())) {
			log.error("Error to find shopInfo by {}", JSON.toJSONString(bean, SerializerFeature.PrettyFormat));

			result.put("statusCode", 601);
			return result;
		}

		bean.setTaskId(TaskCounters.init(bean.getMobilePhone(), bean.getOuerUserId(), bean.getShopType(),
				bean.getDeviceType()));

		final boolean b1 = this.add2TaskQueue(bean);
		if(b1) {
			if (log.isDebugEnabled()) {
				log.debug("success to add to ShopTaskQueue:{}", bean);
			}

			// 店铺名没拿到，默认店铺首页地址
			final String shopName = StringUtils.defaultIfBlank(bean.getShopName(), bean.getShopUrl());

			result.put("statusCode", 200);
			result.put("shopType", bean.getShopType().getValue());
			result.put("shopName", shopName);
			return result;
		}
		else {
			log.error("Error to add to ShopTaskQueue:{}", bean);

			result.put("statusCode", 602);
			return result;
		}
	}

	/**
	 * <pre>
	 * 搬家
	 * 保存偶尔用户与第三方店铺的关系
	 * 搬家店铺商品
	 * </pre>
	 * @param deviceType
	 * @return
	 */
	public Map<String, Object> moveByUrl(final ParseShopInfoBean bean) {
		final Map<String, Object> result = new HashMap<String, Object>(5);

		// 获取店铺信息
		this.setShopInfo0(bean, bean.getShopType(), bean.getRequestUrl(), StringUtils.EMPTY);
		if (StringUtils.isBlank(bean.getShopUrl())) {
			log.error("Error to find shopInfo by {}", JSON.toJSONString(bean, SerializerFeature.PrettyFormat));

			result.put("statusCode", 601);
			return result;
		}

		bean.setTaskId(TaskCounters.init(bean.getMobilePhone(), bean.getOuerUserId(), bean.getShopType(),
				bean.getDeviceType()));
		
		final boolean b1 = this.add2TaskQueue(bean);
		if(b1){
			if (log.isDebugEnabled()) {
				log.debug("success to add to ShopTaskQueue:{}", bean);
			}

			// 店铺名没拿到，默认店铺首页地址
			final String shopName = StringUtils.defaultIfBlank(bean.getShopName(), bean.getShopUrl());

			result.put("statusCode", 200);
			result.put("shopType", bean.getShopType().getValue());
			result.put("shopName", shopName);
			return result;
		}
		else {
			log.error("Error to add to ShopTaskQueue:{}", bean);

			result.put("statusCode", 602);
			return result;
		}
	}
}

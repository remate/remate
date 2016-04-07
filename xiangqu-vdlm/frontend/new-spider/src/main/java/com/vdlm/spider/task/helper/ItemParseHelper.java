package com.vdlm.spider.task.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.core.IntRef;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.utils.JSONUtils;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public abstract class ItemParseHelper {

	private final static Logger LOG = LoggerFactory.getLogger(ItemParseHelper.class);
	
	public static String getApiDescUrl(ItemTaskBean bean, String htmlContent) {
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;		
		String url = "";
		try {
			url = ParserConfigProviders.getApiDescUrl(bean, htmlContent);
		} catch (final Exception e) {
			Logs.maybeChangedRuleLogger
					.error("Error to getDescRemoteUrl from item.htm, maybe invalid ParseRule: {}",
							bean);
		}
		return url;
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
	public static List<Sku> createSkuList(List<String> skuTypes,
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
 
	/**
	 * <pre>
	 * 得到 获取sku信息的 远程url地址
	 * </pre>
	 * 
	 * @param htmlContent
	 * @return
	 */
	public static String getSkuRemoteUrl(ItemTaskBean bean, String htmlContent) {
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;	
		final int index = htmlContent.indexOf(ParserConfigProviders.getItemConfig(bean)
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
							bean);
			return null;
		}
	}
		
	public static void parseTaoBaoSku(ItemTaskBean bean, String htmlContent, List<Sku> skus) {
		// 解析 item.html 中的 sku 库存、价格/////////////////////////////
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;	
		try {
			parseTaoBaoSkuStock(bean, htmlContent, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku stock, maybe invalid ParseRule:" + bean, e);
		}
		try {
			parseTaoBaoSkuPrice(bean, htmlContent, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku price, maybe invalid ParseRule:" + bean, e);
		}
		///////////////////////////////////////////////
	}

	public static void parseTaoBaoSkuStock(ItemTaskBean bean, final String htmlContent, final List<Sku> skus) {		
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;				
		// 获取价格/////////////////////////////////
		final String keyword = ParserConfigProviders.getItemConfig(bean).getStockKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse stock of sku, {}", ParserConfigProviders.getItemConfig(bean));
			return;
		}

		final String jsonString = JSONUtils.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find stock of sku by keyword, maybe invalid ParseRule: {}",
					bean);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid stock jsonString, maybe invalid ParseRule:" + bean, e);
			return;
		}
		for (final Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");

			final List<String> values = JSONUtils.extractList(json, specs, key);

			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				sku.setAmount(d.intValue());
			}
		}
	}

	public static void parseTaoBaoSkuPrice(ItemTaskBean bean, final String htmlContent, final List<Sku> skus) {
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;
		// 获取价格/////////////////////////////////
		final String keyword = ParserConfigProviders.getItemConfig(bean).getPriceKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getPriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse price of sku, {}", ParserConfigProviders.getItemConfig(bean));
			return;
		}

		final String jsonString = JSONUtils.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find price of sku by keyword, maybe invalid ParseRule: {}",
					bean);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.warn("Invalid price jsonString, maybe invalid ParseRule:" + bean, e);
			return;
		}
		for (final Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");

			final List<String> values = JSONUtils.extractList(json, specs, key);

			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				sku.setPrice(d.doubleValue());
			}
		}
	}
	
	public static Map<String, Sku> parseTmallSku(ItemTaskBean bean, String htmlContent, List<Sku> skus) {
		final Map<String, Sku> skuStocks;
		final Map<String, Sku> skuPrices;
		// 解析 item.html 中的 sku 库存、价格/////////////////////////////
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;
		try {
			skuStocks = parseTmallSkuStock(bean, htmlContent, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku stock, maybe invalid ParseRule:" + bean, e);
		}
		try {
			skuPrices = parseTmallSkuPrice(bean, htmlContent, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku price, maybe invalid ParseRule:" + bean, e);
		}
		return null;
	}
	 
	public static Map<String, Sku> parseTmallSkuStock(ItemTaskBean bean, final String htmlContent, final List<Sku> skus) {
		final Map<String, Sku> skuStocks = new HashMap<String, Sku>();
		// 获取价格/////////////////////////////////
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;
		final String keyword =  ParserConfigProviders.getItemConfig(bean).getStockKeyword();
		final String key =  ParserConfigProviders.getItemConfig(bean).getStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse stock of sku, {}",  ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}

		final String jsonString = JSONUtils.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find stock of sku by keyword, maybe invalid ParseRule: {}",
					bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid stock jsonString, maybe invalid ParseRule:" + bean, e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}
		for (final Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");
			final List<String> values = JSONUtils.extractList(json, specs, key);
			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				sku.setAmount(d.intValue());
			}
			// 获取 skuId
			final List<String> skuIds = JSONUtils.extractList(json, specs, ParserConfigProviders.getItemConfig(bean).getSkuId());
			String skuId = sku.getOrigSpec();
			for (final String str : skuIds) {
				if (StringUtils.isNotBlank(str)) {
					skuId = str.trim();
				}
			}
			skuStocks.put(skuId, sku);
		}
		return skuStocks;
	}
	 
	public static Map<String, Sku> parseTmallSkuPrice(ItemTaskBean bean, final String htmlContent, final List<Sku> skus) {
		final Map<String, Sku> skuPrices = new HashMap<String, Sku>();
		// 获取价格/////////////////////////////////
//		final ItemTaskBean bean = (ItemTaskBean) retriable.bean;
		final String keyword = ParserConfigProviders.getItemConfig(bean).getPriceKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getPriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse price of sku, {}", ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}

		final String jsonString = JSONUtils.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find price of sku by keyword, maybe invalid ParseRule:{}",
					bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid price jsonString, maybe invalid ParseRule:" + bean, e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return null;
		}
		for (final Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");
			final List<String> values = JSONUtils.extractList(json, specs, key);
			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				sku.setPrice(d.doubleValue());
			}
			// 获取 skuId
			final List<String> skuIds = JSONUtils.extractList(json, specs, ParserConfigProviders.getItemConfig(bean).getSkuId());
			String skuId = sku.getOrigSpec();
			for (final String str : skuIds) {
				if (StringUtils.isNotBlank(str)) {
					skuId = str.trim();
				}
			}
			skuPrices.put(skuId, sku);
		}
		return skuPrices;
	}
}

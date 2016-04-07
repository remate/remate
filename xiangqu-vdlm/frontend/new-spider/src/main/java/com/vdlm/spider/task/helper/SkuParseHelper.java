package com.vdlm.spider.task.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.utils.JSONUtils;
import com.vdlm.spider.utils.Logs;
import com.vdlm.spider.utils.StringTools;

/**
 *
 * @author: chenxi
 */

public abstract class SkuParseHelper {

	private final static Logger LOG = LoggerFactory.getLogger(SkuParseHelper.class);
	
	public static void parseRemoteTaoBaoSku(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		// 解析远程 sku 库存、价格/////////////////////////////
		try {
			parseRemoteTaoBaoSkuStock(bean, skuInfo, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse remote sku stock, maybe invalid ParseRule:" + bean,
					e);
		}
		try {
			parseRemoteTaoBaoSkuPrice(bean, skuInfo, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse remote sku price, maybe invalid ParseRule:" + bean,
					e);
		}
		///////////////////////////////////////////////
	}

	public static void parseRemoteTaoBaoSkuPrice(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		// 获取价格/////////////////////////////////
		final String keyword = ParserConfigProviders.getItemConfig(bean).getRemotePriceKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getRemotePriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote price of sku, {}", ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = JSONUtils.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote price of sku by keyword, maybe invalid ParseRule: {}", bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote price jsonString, maybe invalid ParseRule:" + bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (final Sku sku : skus) {
			final List<String> values = JSONUtils.extractList(json, sku.getOrigSpec().split(";"), key);

			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				sku.setPrice(d.doubleValue());
			}
		}
	}

	public static void parseRemoteTaoBaoSkuStock(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		// 获取库存//////////////////////////////////
		final String keyword = ParserConfigProviders.getItemConfig(bean).getRemoteStockKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getRemoteStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote stock of sku, {}", ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = JSONUtils.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote stock of sku by keyword, maybe invalid ParseRule: {}", bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote stock jsonString, maybe invalid ParseRule:" + bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (final Sku sku : skus) {
			final List<String> values = JSONUtils.extractList(json, sku.getOrigSpec().split(";"), key);

			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				sku.setAmount(d.intValue());
			} else {
				sku.setAmount(0);
			}
		}
	}
		 
	public static void parseRemoteTmallSku(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
		// 解析远程 sku 库存、价格/////////////////////////////
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		try {
			parseRemoteTmallSkuStock(bean, skuInfo, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse remote sku stock, maybe invalid ParseRule:" + bean,
					e);
		}
		try {
			parseRemoteTmallSkuPrice(bean, skuInfo, skus);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse remote sku price, maybe invalid ParseRule:" + bean,
					e);
		}
		///////////////////////////////////////////////
	}
	
	public static void parseRemoteTmallSkuPrice(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
		// 获取价格/////////////////////////////////
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		final String keyword = ParserConfigProviders.getItemConfig(bean).getRemotePriceKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getRemotePriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote price of sku, {}", ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = JSONUtils.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote price of sku by keyword, maybe invalid ParseRule: {}", bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote price jsonString, maybe invalid ParseRule:" + bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (final Map.Entry<String, Sku> entry :bean.getSkuPrices().entrySet()) {
			final List<String> values = JSONUtils.extractList(json, StringTools.asArray(entry.getKey()), key);

			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				entry.getValue().setPrice(d.doubleValue());
			}
		}
	}
	 
	public static void parseRemoteTmallSkuStock(SkuTaskBean bean, final String skuInfo, final List<Sku> skus) {
		// 获取库存//////////////////////////////////
//		final SkuTaskBean bean = (SkuTaskBean) retriable.bean;
		final String keyword = ParserConfigProviders.getItemConfig(bean).getRemoteStockKeyword();
		final String key = ParserConfigProviders.getItemConfig(bean).getRemoteStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote stock of sku, {}", ParserConfigProviders.getItemConfig(bean));
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = JSONUtils.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote stock of sku by keyword, maybe invalid ParseRule: {}", bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (final Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote stock jsonString, maybe invalid ParseRule:" + bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getSkuUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (final Map.Entry<String, Sku> entry :bean.getSkuStocks().entrySet()) {
			final List<String> values = JSONUtils.extractList(json, entry.getKey().split(";"), key);

			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				entry.getValue().setAmount(d.intValue());
			}
		}
	}
}

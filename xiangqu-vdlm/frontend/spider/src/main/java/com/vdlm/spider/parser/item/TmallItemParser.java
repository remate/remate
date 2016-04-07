/**
 * 
 */
package com.vdlm.spider.parser.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ItemConfig;
import com.vdlm.spider.parser.config.ItemConfigs;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.service.ItemService;
import com.vdlm.spider.utils.JSONUtils;
import com.vdlm.spider.utils.Logs;
import com.vdlm.spider.utils.StringTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:45:55 PM Jul 19, 2014
 */
public class TmallItemParser extends TaobaoItemParser {

	private static final Logger LOG = LoggerFactory.getLogger(TmallItemParser.class);

	protected final Map<String, Sku> skuStocks = new HashMap<String, Sku>();
	protected final Map<String, Sku> skuPrices = new HashMap<String, Sku>();

	public TmallItemParser(HttpClientProvider provider, ParseItemTaskBean bean, ItemService service) {
		super(provider, bean, service);
	}

	@Override
	public Map<String, ParserConfig> getParserConfigs() {
		return ParserConfigs.getOrCreateTmallItemConfig();
	}

	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTmallAuthConfig();
	}

	@Override
	public ItemConfig getItemConfig() {
		return ItemConfigs.getOrCreateTmallItemConfig();
	}

	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTmallShopConfig();
	}

	@Override
	void parseSku(String htmlContent, Item item, List<Sku> skus) {
		// 解析 item.html 中的 sku 库存、价格/////////////////////////////
		try {
			this.parseSkuStock(htmlContent, item, skus);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku stock, maybe invalid ParseRule:" + this.bean, e);
		}
		try {
			this.parseSkuPrice(htmlContent, item, skus);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Error to parse sku price, maybe invalid ParseRule:" + this.bean, e);
		}
		///////////////////////////////////////////////
	}

	@Override
	void parseSkuStock(final String htmlContent, final Item item, final List<Sku> skus) {
		// 获取价格/////////////////////////////////
		final String keyword = this.getItemConfig().getStockKeyword();
		final String key = this.getItemConfig().getStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse stock of sku, {}", this.getItemConfig());
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = this.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find stock of sku by keyword, maybe invalid ParseRule: {}",
					this.bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid stock jsonString, maybe invalid ParseRule:" + this.bean, e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuStock error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");

			final List<String> values = JSONUtils.extractList(json, specs, key);

			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				sku.setAmount(d.intValue());
			}

			// 获取 skuId
			final List<String> skuIds = JSONUtils.extractList(json, specs, this.getItemConfig().getSkuId());
			String skuId = sku.getOrigSpec();
			for (String str : skuIds) {
				if (StringUtils.isNotBlank(str)) {
					skuId = str.trim();
				}
			}

			this.skuStocks.put(skuId, sku);
		}
	}

	@Override
	void parseSkuPrice(final String htmlContent, final Item item, final List<Sku> skus) {
		// 获取价格/////////////////////////////////
		final String keyword = this.getItemConfig().getPriceKeyword();
		final String key = this.getItemConfig().getPriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse price of sku, {}", this.getItemConfig());
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = this.getJSONString(htmlContent, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error("can not find price of sku by keyword, maybe invalid ParseRule:{}",
					this.bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid price jsonString, maybe invalid ParseRule:" + this.bean, e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (Sku sku : skus) {
			final String[] specs = sku.getOrigSpec().split(";");

			final List<String> values = JSONUtils.extractList(json, specs, key);

			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				sku.setPrice(d.doubleValue());
			}

			// 获取 skuId
			final List<String> skuIds = JSONUtils.extractList(json, specs, this.getItemConfig().getSkuId());
			String skuId = sku.getOrigSpec();
			for (String str : skuIds) {
				if (StringUtils.isNotBlank(str)) {
					skuId = str.trim();
				}
			}

			this.skuPrices.put(skuId, sku);
		}
	}

	@Override
	void parseRemoteSkuPrice(final String skuInfo, final Item item, final List<Sku> skus) {
		// 获取价格/////////////////////////////////
		final String keyword = this.getItemConfig().getRemotePriceKeyword();
		final String key = this.getItemConfig().getRemotePriceKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote price of sku, {}", this.getItemConfig());
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = this.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote price of sku by keyword, maybe invalid ParseRule: {}", this.bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote price jsonString, maybe invalid ParseRule:" + this.bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (Map.Entry<String, Sku> entry : this.skuPrices.entrySet()) {
			final List<String> values = JSONUtils.extractList(json, StringTools.asArray(entry.getKey()), key);

			final Double d = ParserUtils.getMaxValue(values);
			if (d != null) {
				entry.getValue().setPrice(d.doubleValue());
			}
		}
	}

	@Override
	void parseRemoteSkuStock(final String skuInfo, final Item item, final List<Sku> skus) {
		// 获取库存//////////////////////////////////
		final String keyword = this.getItemConfig().getRemoteStockKeyword();
		final String key = this.getItemConfig().getRemoteStockKey();
		if (StringUtils.isBlank(keyword) || StringUtils.isBlank(key)) {
			LOG.warn("ignore to parse remote stock of sku, {}", this.getItemConfig());
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}

		final String jsonString = this.getJSONString(skuInfo, keyword);
		if (jsonString == null) {
			Logs.maybeChangedRuleLogger.error(
					"can not find remote stock of sku by keyword, maybe invalid ParseRule: {}", this.bean);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		final Object json;
		try {
			json = JSON.parse(jsonString);
		}
		catch (Exception e) {
			Logs.maybeChangedRuleLogger.error("Invalid remote stock jsonString, maybe invalid ParseRule:" + this.bean,
					e);
			Logs.statistics.warn(
					"parser_item. shopId:{}, listUrl:{}, reason:{}, remoteSkuPrice error",
					bean.getOuerShopId(), bean.getItemUrl(),
					Logs.StatsResult.PARTIAL);
			return;
		}
		for (Map.Entry<String, Sku> entry : this.skuStocks.entrySet()) {
			final List<String> values = JSONUtils.extractList(json, entry.getKey().split(";"), key);

			final Double d = ParserUtils.getMinValue(values);
			if (d != null) {
				entry.getValue().setAmount(d.intValue());
			}
		}
	}
}

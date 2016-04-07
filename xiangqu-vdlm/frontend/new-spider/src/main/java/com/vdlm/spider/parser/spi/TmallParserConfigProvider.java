package com.vdlm.spider.parser.spi;

import java.util.Map;

import com.vdlm.spider.Config;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.parser.config.ItemConfig;
import com.vdlm.spider.parser.config.ItemConfigs;
import com.vdlm.spider.parser.config.ItemListConfig;
import com.vdlm.spider.parser.config.ItemListConfigs;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigProvider;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;

/**
 *
 * @author: chenxi
 */

public class TmallParserConfigProvider implements ParserConfigProvider {

	@Override
	public Map<String, ParserConfig> getParserShopConfigs() {
		return ParserConfigs.getOrCreateTmallShopInfoConfig();
	}

	@Override
	public Map<String, ParserConfig> getParserItemConfigs() {
		return ParserConfigs.getOrCreateTmallItemConfig();
	}	
	 
	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTmallAuthConfig();
	}

	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTmallShopConfig();
	}

	@Override
	public ItemListConfig getItemListConfig() {
		return ItemListConfigs.getOrCreateTmallItemListConfig();
	}

	@Override
	public ItemConfig getItemConfig() {
		return ItemConfigs.getOrCreateTmallItemConfig();
	}

	@Override
	public String getApiDescUrl(String htmlContent, String descKey) throws Exception {
		final int index = htmlContent.indexOf(descKey);
		if (index <= 0) {
			return null;
		}

		final int start = index + descKey.length() + 3;
		final int end = htmlContent.indexOf("\"", start);

		return htmlContent.substring(start, end);
	}

	@Override
	public void setItemListProperties(ItemListTaskBean bean) {
		bean.setParserType(ParserType.TMALL_ITEMLIST);
		bean.setRequestUrl(ItemListConfigs
				.getOrCreateTaobaoItemListConfig()
				.getUrl2(
						String.valueOf(Config.instance().getUrl2StartSize()),
						String.valueOf(Config.instance().getUrl2EndSize()),
						bean.getTUserId()));
		bean.setRefererUrl(ItemListConfigs.getOrCreateTmallItemListConfig()
				.getRefererUrl());
	}

}

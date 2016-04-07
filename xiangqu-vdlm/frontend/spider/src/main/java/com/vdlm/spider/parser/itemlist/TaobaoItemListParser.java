/**
 * 
 */
package com.vdlm.spider.parser.itemlist;

import java.util.Map;

import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.config.ItemListConfig;
import com.vdlm.spider.parser.config.ItemListConfigs;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:35:23 PM Jul 26, 2014
 */
public class TaobaoItemListParser extends ItemListParser {

	public TaobaoItemListParser(HttpClientProvider provider, ParseShopTaskBean bean) {
		super(provider, bean);
	}

	@Override
	public ItemListConfig getItemListConfig() {
		return ItemListConfigs.getOrCreateTaobaoItemListConfig();
	}

	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTaobaoAuthConfig();
	}

	@Override
	public Map<String, ParserConfig> getParserConfigs() {
		return null;
	}
	
	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTaobaoShopConfig();
	}

}

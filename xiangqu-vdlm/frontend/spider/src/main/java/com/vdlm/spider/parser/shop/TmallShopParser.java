/**
 * 
 */
package com.vdlm.spider.parser.shop;

import java.util.Map;

import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:36:30 PM Jul 19, 2014
 */
public class TmallShopParser extends TaobaoShopParser {

	public TmallShopParser(HttpClientProvider provider, ParseShopTaskBean bean) {
		super(provider, bean);
	}

	@Override
	public Map<String, ParserConfig> getParserConfigs() {
		return ParserConfigs.getOrCreateTmallShopConfig();
	}

	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTmallAuthConfig();
	}

	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTmallShopConfig();
	}
}

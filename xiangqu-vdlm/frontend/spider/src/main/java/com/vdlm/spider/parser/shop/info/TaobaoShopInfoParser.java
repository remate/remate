/**
 * 
 */
package com.vdlm.spider.parser.shop.info;

import java.util.Map;

import com.vdlm.spider.bean.ParseShopInfoBean;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigs;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.service.ShopService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:25:59 PM Jul 22, 2014
 */
public class TaobaoShopInfoParser extends ShopInfoParser {

	public TaobaoShopInfoParser(HttpClientProvider provider, ParseShopInfoBean bean, ShopService service) {
		super(provider, bean, service);
	}

	@Override
	public ShopConfig getShopConfig() {
		return ShopConfigs.getOrCreateTaobaoShopConfig();
	}

	@Override
	public Map<String, ParserConfig> getParserConfigs() {
		return ParserConfigs.getOrCreateTaobaoShopInfoConfig();
	}

	@Override
	public Map<String, ParserConfig> getAuthConfigs() {
		return ParserConfigs.getOrCreateTaobaoAuthConfig();
	}
}

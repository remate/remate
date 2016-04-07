package com.vdlm.spider.parser.config;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.parser.spi.TaobaoParserConfigProvider;
import com.vdlm.spider.parser.spi.TmallParserConfigProvider;

/**
 *
 * @author: chenxi
 */

public class ParserConfigProviders {

	private static final Map<ShopType, ParserConfigProvider> providers = Maps.newHashMap();
	
	static {
		providers.put(ShopType.TAOBAO, new TaobaoParserConfigProvider());
		providers.put(ShopType.TMALL, new TmallParserConfigProvider());
		providers.put(ShopType.TAOBAO_OR_TMALL, new TaobaoParserConfigProvider());
	}
	
	public static Map<String, ParserConfig> getAuthConfigs(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getAuthConfigs();
	}	
	
	public static Map<String, ParserConfig> getParserShopConfigs(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getParserShopConfigs();
	}
	
	public static void setItemListProperties(ItemListTaskBean bean) {
		try {
			getParserConfigProvider(bean).setItemListProperties(bean);
		} 
		catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, ParserConfig> getParserItemConfigs(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getParserItemConfigs();
	}

	public static ShopConfig getShopConfig(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getShopConfig();
	}
	
	public static ItemListConfig getItemListConfig(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getItemListConfig();
	}
	
	public static ItemConfig getItemConfig(ItemListTaskBean bean) {
		return getParserConfigProvider(bean).getItemConfig();
	}
	
	private static ParserConfigProvider getParserConfigProvider(ItemListTaskBean bean) {
		return providers.get(bean.getShopType());
	}
	
	public static String getApiDescUrl(ItemListTaskBean bean, String htmlContent) throws Exception {
		final String descKey = ParserConfigProviders.getItemConfig(bean).getApiDescKey();
		return providers.get(bean.getShopType()).getApiDescUrl(htmlContent, descKey);
	}
	
	public static int getDetailImgMaxSize() {
		return providers.get(ShopType.TAOBAO).getItemConfig().getImgSize();
	}
	
}

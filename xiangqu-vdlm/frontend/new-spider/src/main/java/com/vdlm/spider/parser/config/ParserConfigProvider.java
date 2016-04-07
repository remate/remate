package com.vdlm.spider.parser.config;

import java.util.Map;

import com.vdlm.spider.bean.ItemListTaskBean;

/**
 *
 * @author: chenxi
 */

public interface ParserConfigProvider 
{
	/**
	 * <pre>
	 * 获取认证规则
	 * </pre>
	 * @return
	 */
	public Map<String, ParserConfig> getAuthConfigs();

	/**
	 * <pre>
	 * 获取shop抓取规则
	 * </pre>
	 * @return
	 */
	public Map<String, ParserConfig> getParserShopConfigs();
	
	/**
	 * <pre>
	 * 获取item抓取规则
	 * </pre>
	 * @return
	 */
	public Map<String, ParserConfig> getParserItemConfigs();
	
	/**
	 * <pre>
	 * 获取店铺配置
	 * </pre>
	 * @return
	 */
	public ShopConfig getShopConfig();
	
	/**
	 * <pre>
	 * 获取 itemList 配置
	 * </pre>
	 * 
	 * @return
	 */
	public ItemListConfig getItemListConfig();
	
	/**
	 * <pre>
	 * 获取 item 配置
	 * </pre>
	 * 
	 * @return
	 */
	public ItemConfig getItemConfig();
	
	public String getApiDescUrl(String htmlContent, String descKey) throws Exception;
	
	public void setItemListProperties(ItemListTaskBean bean);
}

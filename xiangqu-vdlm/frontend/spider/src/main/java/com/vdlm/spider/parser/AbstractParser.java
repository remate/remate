/**
 * 
 */
package com.vdlm.spider.parser;

import java.util.Map;

import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;

import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ShopConfig;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:13:19 AM Jul 20, 2014
 */
public abstract class AbstractParser implements Parser {

	protected static final int SUCCESS = 1;
	protected static final int FAILED = 2;
	protected static final int RETRY = 4;

	/**
	 * <pre>
	 * 判断是否认证的页面
	 * </pre>
	 * @param htmlContent
	 * @return
	 */
	public boolean isAuthHtml(String htmlContent) {
		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(htmlContent);
		try {
			final Map<String, ParserConfig> configs = this.getAuthConfigs();
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			return (nodeList != null && nodeList.size() > 0);
		}
		catch (Exception ignore) {
		}
		return false;
	}

	/**
	 * <pre>
	 * 获取认证规则
	 * </pre>
	 * @return
	 */
	public abstract Map<String, ParserConfig> getAuthConfigs();

	/**
	 * <pre>
	 * 获取页面抓取规则
	 * </pre>
	 * @return
	 */
	public abstract Map<String, ParserConfig> getParserConfigs();

	/**
	 * <pre>
	 * 获取店铺配置
	 * </pre>
	 * @return
	 */
	public abstract ShopConfig getShopConfig();

}

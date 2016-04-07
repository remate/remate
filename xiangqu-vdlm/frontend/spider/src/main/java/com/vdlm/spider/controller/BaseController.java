/**
 * 
 */
package com.vdlm.spider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.parser.config.ShopConfigs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:48:00 PM Aug 12, 2014
 */
public class BaseController {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * <pre>
	 * 根据店铺URL获取店铺类型
	 * </pre>
	 * @param url
	 * @return
	 */
	ShopType getShopTypeByUrl(String url) {
		// 以 http:// 开头
		final String shopUrl;
		if (url.startsWith(Statics.HTTP_URL_PREFIX)) {
			final int index = url.indexOf('/', Statics.HTTP_URL_PREFIX.length() + 1);
			if (index == -1) {
				shopUrl = url;
			}
			else {
				shopUrl = url.substring(0, index);
			}
		}
		else {
			final int index = url.indexOf('/');
			if (index == -1) {
				shopUrl = Statics.HTTP_URL_PREFIX + url;
			}
			else {
				shopUrl = Statics.HTTP_URL_PREFIX + url.substring(0, index);
			}
		}
		if (shopUrl.endsWith(ShopConfigs.getOrCreateTaobaoShopConfig().getUrlEndsWith())) {
			return ShopType.TAOBAO;
		}
		else if (shopUrl.endsWith(ShopConfigs.getOrCreateTmallShopConfig().getUrlEndsWith())) {
			return ShopType.TMALL;
		}
		else {
			return null;
		}
	}
}

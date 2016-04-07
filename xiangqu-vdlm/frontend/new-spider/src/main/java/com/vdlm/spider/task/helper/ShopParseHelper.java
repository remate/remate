package com.vdlm.spider.task.helper;

import org.apache.commons.lang3.StringUtils;

import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.config.ParserConfigProviders;

/**
 *
 * @author: chenxi
 */

public abstract class ShopParseHelper {

	public static String getCharset(ShopTaskBean bean, HttpInvokeResult result) {
		if (StringUtils.isNotBlank(ParserConfigProviders.getShopConfig(bean).getCharset())) {
			return ParserConfigProviders.getShopConfig(bean).getCharset();
		}
		if (result.getCharset() != null) {
			return result.getCharset().name();
		}
		return Statics.ENCODE;
	}
}

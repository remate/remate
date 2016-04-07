/**
 * 
 */
package com.vdlm.spider.bean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.ThreadLocalCache;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ItemConfig;
import com.vdlm.spider.parser.config.ItemConfigs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 7:15:55 PM Jul 16, 2014
 */
public class ParseItemTaskBean extends ParseShopTaskBean {

	private static final long serialVersionUID = 276579124023561604L;

	private String itemUrl;
	private String itemId;

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	@Override
	public ParseItemTaskBean clone() {
		final ParseItemTaskBean bean = new ParseItemTaskBean();
		BeanUtils.copyProperties(this, bean);
		return bean;
	}

	public static ParseItemTaskBean parse(byte[] bytes) {

		return JSON.parseObject(bytes, 0, bytes.length, ThreadLocalCache.getUTF8Decoder(), ParseItemTaskBean.class);
	}

	public String getItemId() {
		if (StringUtils.isNotBlank(this.itemId)) {
			return this.itemId;
		}
		final ItemConfig config = ItemConfigs.getOrCreateItemConfig(this.getShopType());

		if (config == null) {
			return null;
		}

		return this.itemId = ParserUtils.getUrlParam(this.itemUrl, config.getIdName());
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
}

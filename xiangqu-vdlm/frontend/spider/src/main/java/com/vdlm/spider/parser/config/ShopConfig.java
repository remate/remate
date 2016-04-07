/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:21:17 PM Jul 22, 2014
 */
public class ShopConfig implements Serializable {

	private static final long serialVersionUID = 8589642803960456238L;

	private String searchUrl;
	private String indexUrl;
	private String itemUrl;
	private String itemUrlStartsWith;
	private String urlEndsWith;
	private String[] ignoreParameters;
	private Integer maxLevel;
	private String charset;
	private String smsTpl;
	private String pushTplTitle;
	private String pushTplContent;
	private String pushTplImg;

	public String getPushTplImg() {
		return pushTplImg;
	}

	public void setPushTplImg(String pushTplImg) {
		this.pushTplImg = pushTplImg;
	}

	public String getPushTplTitle() {
		return pushTplTitle;
	}

	public void setPushTplTitle(String pushTplTitle) {
		this.pushTplTitle = pushTplTitle;
	}

	public String getPushTplContent() {
		return pushTplContent;
	}

	public void setPushTplContent(String pushTplContent) {
		this.pushTplContent = pushTplContent;
	}

	public String getSmsTpl() {
		return smsTpl;
	}

	public void setSmsTpl(String smsTpl) {
		this.smsTpl = smsTpl;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Integer getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}

	public String[] getIgnoreParameters() {
		return ignoreParameters;
	}

	public void setIgnoreParameters(String[] ignoreParameters) {
		this.ignoreParameters = ignoreParameters;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public String getIndexUrl(String shopUrl) {
		return StringUtils.replaceOnce(this.indexUrl, "{shopUrl}", shopUrl);
	}

	public void setIndexUrl(String indexUrl) {
		if (StringUtils.isBlank(indexUrl)) {
			throw new IllegalArgumentException("indexUrl can not be blank");
		}
		this.indexUrl = indexUrl;
	}

	public String getItemUrlStartsWith() {
		return itemUrlStartsWith;
	}

	public void setItemUrlStartsWith(String itemUrlStartsWith) {
		this.itemUrlStartsWith = itemUrlStartsWith;
	}

	public String getUrlEndsWith() {
		return urlEndsWith;
	}

	public void setUrlEndsWith(String urlEndsWith) {
		this.urlEndsWith = urlEndsWith;
	}

	public String getSearchUrl() {
		return searchUrl;
	}

	public String getSearchUrl(String shopUrl) {
		return StringUtils.replaceOnce(this.searchUrl, "{shopUrl}", shopUrl);
	}

	public void setSearchUrl(String searchUrl) {
		if (StringUtils.isBlank(searchUrl)) {
			throw new IllegalArgumentException("searchUrl can not be blank");
		}
		this.searchUrl = searchUrl;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public String getItemUrl(String itemId) {
		return StringUtils.replaceOnce(this.itemUrl, "{itemId}", itemId);
	}

	public void setItemUrl(String itemUrl) {
		if (StringUtils.isBlank(itemUrl)) {
			throw new IllegalArgumentException("itemUrl can not be blank");
		}
		this.itemUrl = itemUrl;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}

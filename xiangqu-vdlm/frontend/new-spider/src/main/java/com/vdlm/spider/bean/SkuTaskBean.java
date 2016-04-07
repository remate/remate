package com.vdlm.spider.bean;

import java.util.List;
import java.util.Map;

import com.vdlm.spider.entity.Sku;

/**
 *
 * @author: chenxi
 */

public class SkuTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004043543136287358L;

	private Long itemTaskId;
	private String skuUrl;
    private List<Sku> skus;
    private Map<String, Sku> skuStocks;
    private Map<String, Sku> skuPrices;
    
    private String referIp;
    private String userAgent;
	
	public Map<String, Sku> getSkuStocks() {
		return skuStocks;
	}

	public void setSkuStocks(Map<String, Sku> skuStocks) {
		this.skuStocks = skuStocks;
	}

	public Map<String, Sku> getSkuPrices() {
		return skuPrices;
	}

	public void setSkuPrices(Map<String, Sku> skuPrices) {
		this.skuPrices = skuPrices;
	}

	public List<Sku> getSkus() {
		return skus;
	}

	public void setSkus(List<Sku> skus) {
		this.skus = skus;
	}

	public Long getItemTaskId() {
		return itemTaskId;
	}

	public void setItemTaskId(Long itemTaskId) {
		this.itemTaskId = itemTaskId;
	}

	public String getSkuUrl() {
		return skuUrl;
	}
	
	public void setSkuUrl(String skuUrl) {
		this.skuUrl = skuUrl;
	}

	public String getReferIp() {
		return referIp;
	}

	public void setReferIp(String referIp) {
		this.referIp = referIp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public String getTaskName() {
		return "sku";
	}
}

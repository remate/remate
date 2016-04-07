package com.vdlm.spider.wdetail.parser;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vdlm.spider.Statics;
import com.vdlm.spider.entity.Item.SkuProps;
import com.vdlm.spider.entity.Sku;

/**
 *
 * @author: chenxi
 */

public class WItem {

	private String name;
	private Double price;
	private Integer amount;
	private String userId;
	private String shopId;
	
	private Set<String> groupImgs;
	private Set<String> skuImgs;
	private List<Sku> skus;
	
	private List<String> skuTypes;
	private List<List<String>> skuValues;
	private List<List<String>> skuTexts;
	private SkuProps skuProps;
	private String props;  // 详情版本最前面的型号说明
	
	private String apiDescUrl;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getShopId() {
		return shopId;
	}
	
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public List<Sku> getSkus() {
		return skus;
	}
	
	public void setDefaultSku(Sku sku) {
		skus = Lists.newArrayList();
		skus.add(sku);
		skuImgs = Sets.newHashSet();
	}

	public void setSkus(List<Sku> skus) {
		this.skus = skus;
		if (price == null) {
			for (final Sku sku : skus) {
				if (price == null) {
					price = sku.getPrice();
				} else {
					price = Math.min(price, sku.getPrice());
				}
			}
		}
	}

	public Set<String> getGroupImgs() {
		return groupImgs;
	}

	public void setGroupImgs(Set<String> groupImgs) {
		this.groupImgs = groupImgs;
	}

	public Set<String> getSkuImgs() {
		return skuImgs;
	}

	public void setSkuImgs(Set<String> skuImgs) {
		this.skuImgs = skuImgs;
	}

	public List<String> getSkuTypes() {
		return skuTypes;
	}

	public void setSkuTypes(List<String> skuTypes) {
		this.skuTypes = skuTypes;
	}

	public List<List<String>> getSkuValues() {
		return skuValues;
	}

	public void setSkuValues(List<List<String>> skuValues) {
		this.skuValues = skuValues;
	}

	public List<List<String>> getSkuTexts() {
		return skuTexts;
	}

	public void setSkuTexts(List<List<String>> skuTexts) {
		this.skuTexts = skuTexts;
	}

	public String getApiDescUrl() {
		return apiDescUrl;
	}

	public void setApiDescUrl(String apiDescUrl) {
		this.apiDescUrl = apiDescUrl;
	}

	public int getStatus() {
		if (amount == null || amount == 0) {
			return Statics.SOLD_OUT;
		}
		
		for (final Sku sku : skus) {
			if (sku.getAmount() == null || sku.getAmount() == 0) {
				return Statics.INCOMPLETED_INFO;
			}
			if (sku.getPrice() == null || sku.getPrice() == 0) {
				return Statics.INCOMPLETED_INFO;
			}
		}

		if (price == null) {
			return Statics.INCOMPLETED_INFO;
		}
		
		return Statics.NORMAL;
	}

	public SkuProps getSkuProps() {
		return skuProps;
	}

	public void setSkuProps(SkuProps skuProps) {
		this.skuProps = skuProps;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}


}

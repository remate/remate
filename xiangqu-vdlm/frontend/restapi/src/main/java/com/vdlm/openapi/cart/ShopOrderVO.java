package com.vdlm.openapi.cart;

import java.util.List;

public class ShopOrderVO {
	
	private String shopId;
	
	private List<OrderSkuVO> skus;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public List<OrderSkuVO> getSkus() {
		return skus;
	}

	public void setSkus(List<OrderSkuVO> skus) {
		this.skus = skus;
	}
	
}

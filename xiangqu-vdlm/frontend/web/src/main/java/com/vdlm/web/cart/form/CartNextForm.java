package com.vdlm.web.cart.form;

import java.util.List;

/**
 * @author tonghu
 */
public class CartNextForm {
	
	private List<String> skuId;
	
	private String shopId;

	private int qty;//直接下单购买数量	
     
	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public List<String> getSkuId() {
        return skuId;
    }

    public void setSkuId(List<String> skuId) {
        this.skuId = skuId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
	
}

package com.vdlm.openapi.cart;

/**
 *订单页面用于修改购物车数量时使用
 * @author zhuyin
 *
 */
public class OrderSkuVO {
	
	private String cartItemId;

	private String skuId;
	   
	private int qty;

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	   
	
}

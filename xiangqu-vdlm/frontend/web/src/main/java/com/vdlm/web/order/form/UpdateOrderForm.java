package com.vdlm.web.order.form;

import java.util.List;

public class UpdateOrderForm {
	private String shopId;
	private List<OrderSkuVO> orderSkuVO;
	
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public List<OrderSkuVO> getOrderSkuVO() {
		return orderSkuVO;
	}
	public void setOrderSkuVO(List<OrderSkuVO> orderSkuVO) {
		this.orderSkuVO = orderSkuVO;
	}
	
}

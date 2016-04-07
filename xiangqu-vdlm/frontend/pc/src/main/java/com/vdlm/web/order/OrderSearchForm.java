package com.vdlm.web.order;

import com.vdlm.dal.status.OrderStatus;

public class OrderSearchForm {
	
	private String shopId_kwd;
	
	private OrderStatus status_kwd;
	
	public String getShopId_kwd() {
		return shopId_kwd;
	}
	public void setShopId_kwd(String shopId_kwd) {
		this.shopId_kwd = shopId_kwd;
	}
	
	public OrderStatus getStatus_kwd() {
		return status_kwd;
	}
	public void setStatus_kwd(OrderStatus status_kwd) {
		this.status_kwd = status_kwd;
	}
	

}

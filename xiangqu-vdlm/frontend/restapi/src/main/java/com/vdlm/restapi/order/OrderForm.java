package com.vdlm.restapi.order;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 订单Form表单逻辑
 * 
 * @author odin
 */
public class OrderForm {
	
	@NotBlank(message = "{orderId.notBlank.message}")
	String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}

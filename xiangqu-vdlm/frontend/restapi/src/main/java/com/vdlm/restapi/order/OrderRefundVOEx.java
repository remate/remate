package com.vdlm.restapi.order;

import org.springframework.beans.BeanUtils;

import com.vdlm.dal.vo.OrderRefundVO;

public class OrderRefundVOEx extends OrderRefundVO {

	private static final long serialVersionUID = 1L;
	String requestData;
	
	public OrderRefundVOEx() {
		
	}
	
	public OrderRefundVOEx(OrderRefundVO order) {
		BeanUtils.copyProperties(order, this);
	}
	
	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
}
package com.vdlm.bos.orderRefund;

public class OrderRefundSearchForm {
	private String order_no_kwd;
	private String status_kwd;
	private String refund_valid1_kwd;
	private String refund_valid2_kwd;
	private String payType_kwd;
	
	
	public String getPayType_kwd() {
		return payType_kwd;
	}

	public void setPayType_kwd(String payType_kwd) {
		this.payType_kwd = payType_kwd;
	}

	public String getOrder_no_kwd() {
		return order_no_kwd;
	}
	
	public void setOrder_no_kwd(String order_no_kwd) {
		this.order_no_kwd = order_no_kwd;
	}
	
	public String getStatus_kwd() {
		return status_kwd;
	}

	public void setStatus_kwd(String status_kwd) {
		this.status_kwd = status_kwd;
	}

	public String getRefund_valid1_kwd() {
		return refund_valid1_kwd;
	}
	
	public void setRefund_valid1_kwd(String refund_valid1_kwd) {
		this.refund_valid1_kwd = refund_valid1_kwd;
	}
	
	public String getRefund_valid2_kwd() {
		return refund_valid2_kwd;
	}
	
	public void setRefund_valid2_kwd(String refund_valid2_kwd) {
		this.refund_valid2_kwd = refund_valid2_kwd;
	}
	
}

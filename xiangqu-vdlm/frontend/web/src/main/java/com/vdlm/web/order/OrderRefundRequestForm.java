package com.vdlm.web.order;

public class OrderRefundRequestForm {
	
	String id;
	String orderId;
	Integer buyerRequire;	//买家要求
    Integer buyerReceived;	//买家是否已收到货
	String refundReason;  //退款原因 
	String refundFee;	  //退款金额
	String refundMemo;	  //退款说明
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getBuyerRequire() {
		return buyerRequire;
	}
	public void setBuyerRequire(Integer buyerRequire) {
		this.buyerRequire = buyerRequire;
	}
	public Integer getBuyerReceived() {
		return buyerReceived;
	}
	public void setBuyerReceived(Integer buyerReceived) {
		this.buyerReceived = buyerReceived;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public String getRefundFee() {
		return refundFee;
	}
	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}
	public String getRefundMemo() {
		return refundMemo;
	}
	public void setRefundMemo(String refundMemo) {
		this.refundMemo = refundMemo;
	}
	
	
}

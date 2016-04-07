package com.vdlm.web.order.form;

import java.util.List;

import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;

public class OrderPayAgainForm {

	private String orderId;
	private String payAgreementId;
	private PaymentMode payType;
	private List<Coupon> coupon;
	private String bankCode;
    private PaymentChannel cardType;
//    private String bankName;
//    
    private String hongbaoId="";
    private String hongbaoAmount="0";
    private String hongbaoName="";

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayAgreementId() {
		return payAgreementId;
	}

	public void setPayAgreementId(String payAgreementId) {
		this.payAgreementId = payAgreementId;
	}

	public List<Coupon> getCoupon() {
		return coupon;
	}

	public void setCoupon(List<Coupon> coupon) {
		this.coupon = coupon;
	}

	public PaymentMode getPayType() {
		return payType;
	}

	public void setPayType(PaymentMode payType) {
		this.payType = payType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public PaymentChannel getCardType() {
		return cardType;
	}

	public void setCardType(PaymentChannel cardType) {
		this.cardType = cardType;
	}

	public String getHongbaoId() {
		return hongbaoId;
	}

	public void setHongbaoId(String hongbaoId) {
		this.hongbaoId = hongbaoId;
	}

	public String getHongbaoAmount() {
		return hongbaoAmount;
	}

	public void setHongbaoAmount(String hongbaoAmount) {
		this.hongbaoAmount = hongbaoAmount;
	}

	public String getHongbaoName() {
		return hongbaoName;
	}

	public void setHongbaoName(String hongbaoName) {
		this.hongbaoName = hongbaoName;
	}

//	public String getBankName() {
//		return bankName;
//	}
//
//	public void setBankName(String bankName) {
//		this.bankName = bankName;
//	}
}

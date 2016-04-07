package com.vdlm.web.payment;

import com.vdlm.dal.type.PaymentMode;

public class CashitemForm {

	private String orderId;
	private String payAgreementId;
	private PaymentMode payType;
	private String bankCode;
    private String cardType;
    
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

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
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

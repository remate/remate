package com.vdlm.web.payBank;

import java.math.BigDecimal;

import com.vdlm.dal.type.BankCardType;
import com.vdlm.dal.type.PaymentMode;

public class PayBankForm {

	private String tradeNo;
	private BigDecimal totalFee;
	private PaymentMode payType;
	private String bankCode;
	private BankCardType cardType;
	private String bankName;
	
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public BankCardType getCardType() {
		return cardType;
	}

	public void setCardType(BankCardType cardType) {
		this.cardType = cardType;
	}

	public PaymentMode getPayType() {
		return payType;
	}

	public void setPayType(PaymentMode payType) {
		this.payType = payType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}

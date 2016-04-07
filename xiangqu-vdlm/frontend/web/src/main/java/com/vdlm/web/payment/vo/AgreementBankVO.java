package com.vdlm.web.payment.vo;

import com.vdlm.dal.type.BankCardType;

public class AgreementBankVO {
	
	private String bankName;
	
	private String img;
	
	private String bankAccount;
	
	private String agreementId;
	
	private BankCardType cartType;

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(String agreementId) {
		this.agreementId = agreementId;
	}

	public BankCardType getCartType() {
		return cartType;
	}

	public void setCartType(BankCardType cartType) {
		this.cartType = cartType;
	}

}

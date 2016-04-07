package com.vdlm.restapi.bank;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class UserBankForm {
	
	private String id;
	
	@NotBlank(message = "{valid.notBlank.message}")
	@Size(max = 18, message = "{valid.bank.idCardNum.message}")
	private String idCardNum;

	@NotBlank(message = "{valid.notBlank.message}")
	@Size(min = 5, max = 40, message = "{valid.bank.accountNumber.message}")
    private String accountNumber;

	@NotBlank(message = "{valid.notBlank.message}")
	@Size(min = 2, max = 64, message = "{valid.bank.accountName.message}")
    private String accountName;

	@NotBlank(message = "{valid.notBlank.message}")
	@Size(min = 2, max = 64, message = "{valid.bank.openingBank.message}")
    private String openingBank;

	@Size(min = 6, max = 6, message = "{valid.userbank.smscode.message}")
	private String smsCode;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getOpeningBank() {
		return openingBank;
	}

	public void setOpeningBank(String openingBank) {
		this.openingBank = openingBank;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}
}

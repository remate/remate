package com.vdlm.bos.bank;

import com.vdlm.dal.type.AccountType;

public class TradeDetailsSearchForm {

	private String bizId;
	private AccountType accountType;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

}

package com.vdlm.bos.tradeDetails;

import com.vdlm.dal.type.AccountType;
import com.vdlm.dal.type.PayRequestPayType;

public class TradeDetailsSearchForm {

	private String userId;
	private String bizId;
	private AccountType accountType;
	private PayRequestPayType payType;
	private String startTime="";
	private String endTime="";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public PayRequestPayType getPayType() {
		return payType;
	}

	public void setPayType(PayRequestPayType payType) {
		this.payType = payType;
	}
}

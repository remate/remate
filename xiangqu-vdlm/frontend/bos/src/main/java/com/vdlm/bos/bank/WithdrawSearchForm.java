package com.vdlm.bos.bank;

import java.math.BigDecimal;

import com.vdlm.dal.status.WithdrawApplyStatus;

public class WithdrawSearchForm {
	private String phone_kwd;
	private WithdrawApplyStatus withdraw_status_kwd;
	private String type_kwd;
	private String seller_name_kwd;
	
	private String fee_operator;
	private BigDecimal minimum_fee;
	private String startDateKwd = "";
	private String endDateKwd = "";
	
	private String pay1_date_kwd;
	private String pay2_date_kwd;
	private String checkingChannel_kwd;
	
	private String apply_no_kwd;
	private String batch_pay_no_kwd;
	
	private int lock_kwd;
	
	public String getPay1_date_kwd() {
		return pay1_date_kwd;
	}

	public void setPay1_date_kwd(String pay1_date_kwd) {
		this.pay1_date_kwd = pay1_date_kwd;
	}

	public String getPay2_date_kwd() {
		return pay2_date_kwd;
	}

	public void setPay2_date_kwd(String pay2_date_kwd) {
		this.pay2_date_kwd = pay2_date_kwd;
	}

	public String getCheckingChannel_kwd() {
		return checkingChannel_kwd;
	}

	public void setCheckingChannel_kwd(String checkingChannel_kwd) {
		this.checkingChannel_kwd = checkingChannel_kwd;
	}

	public String getPhone_kwd() {
		return phone_kwd;
	}

	public void setPhone_kwd(String phone_kwd) {
		this.phone_kwd = phone_kwd;
	}

	public WithdrawApplyStatus getWithdraw_status_kwd() {
		return withdraw_status_kwd;
	}

	public void setWithdraw_status_kwd(WithdrawApplyStatus withdraw_status_kwd) {
		this.withdraw_status_kwd = withdraw_status_kwd;
	}

	public String getType_kwd() {
		return type_kwd;
	}

	public void setType_kwd(String type_kwd) {
		this.type_kwd = type_kwd;
	}

	public String getSeller_name_kwd() {
		return seller_name_kwd;
	}

	public void setSeller_name_kwd(String seller_name_kwd) {
		this.seller_name_kwd = seller_name_kwd;
	}

	public String getFee_operator() {
		return fee_operator;
	}

	public void setFee_operator(String fee_operator) {
		this.fee_operator = fee_operator;
	}

	public BigDecimal getMinimum_fee() {
		return minimum_fee;
	}

	public void setMinimum_fee(BigDecimal minimum_fee) {
		this.minimum_fee = minimum_fee;
	}

	public String getStartDateKwd() {
		return startDateKwd;
	}

	public void setStartDateKwd(String startDateKwd) {
		this.startDateKwd = startDateKwd;
	}

	public String getEndDateKwd() {
		return endDateKwd;
	}

	public void setEndDateKwd(String endDateKwd) {
		this.endDateKwd = endDateKwd;
	}

	public String getApply_no_kwd() {
		return apply_no_kwd;
	}

	public void setApply_no_kwd(String apply_no_kwd) {
		this.apply_no_kwd = apply_no_kwd;
	}

	public String getBatch_pay_no_kwd() {
		return batch_pay_no_kwd;
	}

	public void setBatch_pay_no_kwd(String batch_pay_no_kwd) {
		this.batch_pay_no_kwd = batch_pay_no_kwd;
	}

	public int getLock_kwd() {
		return lock_kwd;
	}

	public void setLock_kwd(int lock_kwd) {
		this.lock_kwd = lock_kwd;
	}
	
}

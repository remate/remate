package com.vdlm.bos.commission;

import com.vdlm.dal.status.CommissionStatus;
import com.vdlm.dal.type.CommissionType;

public class CommissionSearchForm {
	private String orderNo_kwd;
	private CommissionStatus commission_status_kwd;
	private String commission_partner_kwd;
	private String userPhone_kwd;
	private CommissionType type_kwd;

	public String getUserPhone_kwd() {
		return userPhone_kwd;
	}

	public void setUserPhone_kwd(String userPhone_kwd) {
		this.userPhone_kwd = userPhone_kwd;
	}

	public CommissionStatus getCommission_status_kwd() {
		return commission_status_kwd;
	}

	public void setCommission_status_kwd(CommissionStatus commission_status_kwd) {
		this.commission_status_kwd = commission_status_kwd;
	}

	public String getOrderNo_kwd() {
		return orderNo_kwd;
	}

	public void setOrderNo_kwd(String orderNo_kwd) {
		this.orderNo_kwd = orderNo_kwd;
	}

	public String getCommission_partner_kwd() {
		return commission_partner_kwd;
	}

	public void setCommission_partner_kwd(
			String commission_partner_kwd) {
		this.commission_partner_kwd = commission_partner_kwd;
	}

	public CommissionType getType_kwd() {
		return type_kwd;
	}

	public void setType_kwd(CommissionType type_kwd) {
		this.type_kwd = type_kwd;
	}

}

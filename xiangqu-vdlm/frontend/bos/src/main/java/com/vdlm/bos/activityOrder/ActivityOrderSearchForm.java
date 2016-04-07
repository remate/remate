package com.vdlm.bos.activityOrder;

import com.vdlm.dal.status.ActivityOrderStatus;

public class ActivityOrderSearchForm {

	private String order_no_kwd;
	private String seller_phone_kwd;
	private String buyer_phone_kwd;
	private ActivityOrderStatus status_kwd;

	public String getOrder_no_kwd() {
		return order_no_kwd;
	}

	public void setOrder_no_kwd(String order_no_kwd) {
		this.order_no_kwd = order_no_kwd;
	}

	public String getSeller_phone_kwd() {
		return seller_phone_kwd;
	}

	public void setSeller_phone_kwd(String seller_phone_kwd) {
		this.seller_phone_kwd = seller_phone_kwd;
	}

	public String getBuyer_phone_kwd() {
		return buyer_phone_kwd;
	}

	public void setBuyer_phone_kwd(String buyer_phone_kwd) {
		this.buyer_phone_kwd = buyer_phone_kwd;
	}

	public ActivityOrderStatus getStatus_kwd() {
		return status_kwd;
	}

	public void setStatus_kwd(ActivityOrderStatus status_kwd) {
		this.status_kwd = status_kwd;
	}
}

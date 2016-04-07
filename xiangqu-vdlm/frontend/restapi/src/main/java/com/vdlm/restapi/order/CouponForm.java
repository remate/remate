package com.vdlm.restapi.order;

public class CouponForm {
	
	private String id;			// 开店优惠id
	private String extCouponId;	 // 想去优惠id
	private String activityCode;	// 活动
	private String couponCode;		// 优惠码 ，暂时不用
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExtCouponId() {
		return extCouponId;
	}
	public void setExtCouponId(String extCouponId) {
		this.extCouponId = extCouponId;
	}
	public String getActivityCode() {
		return activityCode;
	}
	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	
}

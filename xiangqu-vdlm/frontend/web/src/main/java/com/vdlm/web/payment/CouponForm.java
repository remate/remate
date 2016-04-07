package com.vdlm.web.payment;

public class CouponForm {
    private String couponType;
	private String extHongbaoId;
	private String discountCode;
	private float discount;
	private String couponId;
	
	public String getCouponType() {
		return couponType;
	}
	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}
	
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	public String getExtHongbaoId() {
		return extHongbaoId;
	}
	public void setExtHongbaoId(String extHongbaoId) {
		this.extHongbaoId = extHongbaoId;
	}
	public String getDiscountCode() {
		return discountCode;
	}
	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	
}

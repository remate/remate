package com.vdlm.bos.coupon;

public class CouponSearchForm {
	private String code_kwd;
	private String valid1_kwd;
	private String valid2_kwd;
	private String status_kwd;
	private String activity_title;
	private String cashieritem1_kwd;
	private String cashieritem2_kwd;
	
	public String getCashieritem1_kwd() {
		return cashieritem1_kwd;
	}
	public void setCashieritem1_kwd(String cashieritem1_kwd) {
		this.cashieritem1_kwd = cashieritem1_kwd;
	}
	public String getCashieritem2_kwd() {
		return cashieritem2_kwd;
	}
	public void setCashieritem2_kwd(String cashieritem2_kwd) {
		this.cashieritem2_kwd = cashieritem2_kwd;
	}
	public String getCode_kwd() {
		return code_kwd;
	}
	public void setCode_kwd(String code_kwd) {
		this.code_kwd = code_kwd;
	}
	
	public String getValid1_kwd() {
		return valid1_kwd;
	}
	public void setValid1_kwd(String valid1_kwd) {
		this.valid1_kwd = valid1_kwd;
	}
	
	public String getValid2_kwd() {
		return valid2_kwd;
	}
	public void setValid2_kwd(String valid2_kwd) {
		this.valid2_kwd = valid2_kwd;
	}
	
	public String getStatus_kwd() {
		return status_kwd;
	}
	public void setStatus_kwd(String status_kwd) {
		this.status_kwd = status_kwd;
	}
	
	public String getActivity_title(){
		return activity_title;
	}
	
	public void setActivity_title(String activity_title){
		this.activity_title = activity_title;
	}

}

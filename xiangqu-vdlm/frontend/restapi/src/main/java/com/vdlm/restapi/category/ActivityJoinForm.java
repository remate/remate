package com.vdlm.restapi.category;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


public class ActivityJoinForm {

	private String id;
	private String reason;
	@Max(value=3, message="{invalid.argument}")
	@Min(value=1, message="{invalid.argument}")
	private Integer preferentialType;
	private Float discount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getPreferentialType() {
		return preferentialType;
	}

	public void setPreferentialType(Integer preferentialType) {
		this.preferentialType = preferentialType;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

}

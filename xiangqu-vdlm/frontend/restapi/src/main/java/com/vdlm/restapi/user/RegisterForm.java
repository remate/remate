package com.vdlm.restapi.user;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class RegisterForm {

	private static final String NOT_BLANK_MESSAGE = "{valid.notBlank.message}";
	private static final String MOBILE_MESSAGE = "{valid.mobile.message}";

    @NotBlank(message = NOT_BLANK_MESSAGE)
	@Pattern(regexp="^1(3\\d|47|5([0-3]|[5-9])|8(0|2|[5-9]))\\d{8}$", message = MOBILE_MESSAGE)
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}

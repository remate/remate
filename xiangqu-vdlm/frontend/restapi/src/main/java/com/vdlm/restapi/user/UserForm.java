package com.vdlm.restapi.user;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class UserForm {

	@NotBlank(message = "{valid.notBlank.message}")
	@Pattern(regexp = "^1((3|4|5|7|8)\\d)\\d{8}$", message = "{valid.mobile.message}")
	private String mobile;

	@Size(min = 6, max = 6)
	private String smsCode;

	@Size(min = 6, max = 50)
	private String pwd;
	
	private Long baiduChannelId;
	
	private String baiduUserId;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public Long getBaiduChannelId() {
		return baiduChannelId;
	}

	public void setBaiduChannelId(Long baiduChannelId) {
		this.baiduChannelId = baiduChannelId;
	}

	public String getBaiduUserId() {
		return baiduUserId;
	}

	public void setBaiduUserId(String baiduUserId) {
		this.baiduUserId = baiduUserId;
	}

}

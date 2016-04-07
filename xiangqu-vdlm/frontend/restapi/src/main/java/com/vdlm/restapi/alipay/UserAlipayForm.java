package com.vdlm.restapi.alipay;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class UserAlipayForm {
	
	private String id;
	
	@NotBlank(message = "{valid.notAlipay.message}")
    private String account;

    private String name;
    
    @Size(min = 6, max = 6, message = "{valid.alipay.smscode.message}")
	private String smsCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
}

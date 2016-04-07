package com.vdlm.restapi.common;

import org.hibernate.validator.constraints.NotBlank;

public class FeedbackForm {
	
	private static final String NOT_BLANK_MESSAGE = "{valid.notBlank.message}";
	
	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String name;
	
	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String contact;
	
	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String content;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContact() {
		return contact;
	}
	
	public void setContact(String contact) {
		this.contact = contact;
	}
}

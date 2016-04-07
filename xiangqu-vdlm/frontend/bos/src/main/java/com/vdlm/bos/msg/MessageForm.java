package com.vdlm.bos.msg;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.vdlm.dal.type.PushMessageDeviceType;


/**
 * 运营消息发送FORM 
 * @author odin
 */
public class MessageForm {

	private String title;

	@NotBlank(message = "{valid.notBlank.message}")
	@Size(min = 2, max = 64, message = "{valid.userMessage.content}")
	private String content;
	
	private String url;
	
	private PushMessageDeviceType plantForm;
	
	private String pushType;
	
	private String appName;
	
	@NotBlank(message = "{valid.notBlank.message}")
	@Pattern(regexp = "^1((3|4|5|7|8)\\d)\\d{8}$", message = "{valid.mobile.message}")
	private String toUserPhone;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	public PushMessageDeviceType getPlantForm() {
		return plantForm;
	}

	public void setPlantForm(PushMessageDeviceType plantForm) {
		this.plantForm = plantForm;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getToUserPhone() {
		return toUserPhone;
	}

	public void setToUserPhone(String toUserPhone) {
		this.toUserPhone = toUserPhone;
	}
}

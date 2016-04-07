package com.vdlm.bos.feedback;

import com.vdlm.dal.model.FeedbackType;
import com.vdlm.dal.status.FeedbackStatus;

public class FeedbackSearchForm {
	private String phone_kwd;
	private FeedbackStatus feedback_status_kwd;
	private FeedbackType type_kwd;
	
	public String getPhone_kwd() {
		return phone_kwd;
	}
	public void setPhone_kwd(String phone_kwd) {
		this.phone_kwd = phone_kwd;
	}
	public FeedbackStatus getFeedback_status_kwd() {
		return feedback_status_kwd;
	}
	public void setFeedback_status_kwd(FeedbackStatus feedback_status_kwd) {
		this.feedback_status_kwd = feedback_status_kwd;
	}
	public FeedbackType getType_kwd() {
		return type_kwd;
	}
	public void setType_kwd(FeedbackType type_kwd) {
		this.type_kwd = type_kwd;
	}
	
}

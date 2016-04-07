package com.vdlm.restapi.partnerActivity;

import java.util.Date;

import com.vdlm.dal.status.ActivityTicketAuditStatus;

public class PartnerActivityProductAuditForm {

	private String productId;
	private String activityId;
	private Date actStartTime;
	private Date actEndTime;
	private ActivityTicketAuditStatus auditState;
	private String auditReason;
	private String auditor;

	// private String auditTime; --now()
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Date getActStartTime() {
		return actStartTime;
	}

	public void setActStartTime(Date actStartTime) {
		this.actStartTime = actStartTime;
	}

	public Date getActEndTime() {
		return actEndTime;
	}

	public void setActEndTime(Date actEndTime) {
		this.actEndTime = actEndTime;
	}

	public ActivityTicketAuditStatus getAuditState() {
		return auditState;
	}

	public void setAuditState(ActivityTicketAuditStatus auditState) {
		this.auditState = auditState;
	}

//	public ActivityChannel getChannel() {
//		return channel;
//	}
//
//	public void setChannel(ActivityChannel channel) {
//		this.channel = channel;
//	}

	public String getAuditReason() {
		return auditReason;
	}

	public void setAuditReason(String auditReason) {
		this.auditReason = auditReason;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

}

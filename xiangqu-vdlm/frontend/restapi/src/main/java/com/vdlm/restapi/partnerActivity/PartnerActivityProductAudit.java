package com.vdlm.restapi.partnerActivity;

import com.vdlm.dal.status.ActivityTicketAuditStatus;

public class PartnerActivityProductAudit {

	private String ticketId;
	private String productId;
	private String actId;
	private Long startTime;
	private Long endTime;
	private ActivityTicketAuditStatus auditState;
	private String auditReason;
	private String auditor;
	
	private String productBrand;
	private String shortName;
	private Integer sort;
	private String imagePc;
	private String imageApp;

	// private String auditTime; --now()
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
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

	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}
	
	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getImagePc() {
		return imagePc;
	}

	public void setImagePc(String imagePc) {
		this.imagePc = imagePc;
	}

	public String getImageApp() {
		return imageApp;
	}

	public void setImageApp(String imageApp) {
		this.imageApp = imageApp;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

}

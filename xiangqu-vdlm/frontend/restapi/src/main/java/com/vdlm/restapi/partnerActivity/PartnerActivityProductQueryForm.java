package com.vdlm.restapi.partnerActivity;

import com.vdlm.dal.status.ActivityTicketAuditStatus;

public class PartnerActivityProductQueryForm {
	private String actId;
	private int sortType = 0;
	private ActivityTicketAuditStatus auditState;
	private String productName;
	private String productBrand; 
	private String shortName;
	private Long startTime;   // 审核通过允许商品展示时间
	private Long endTime;

	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	public ActivityTicketAuditStatus getAuditState() {
		return auditState;
	}

	public void setAuditState(ActivityTicketAuditStatus auditState) {
		this.auditState = auditState;
	}

	public int getSortType() {
		return sortType;
	}

	public void setSortType(int sortType) {
		this.sortType = sortType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
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

}

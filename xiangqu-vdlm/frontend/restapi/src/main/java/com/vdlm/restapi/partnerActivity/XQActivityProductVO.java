package com.vdlm.restapi.partnerActivity;

import java.math.BigDecimal;

import com.vdlm.dal.vo.CampaignProductEX;

public class XQActivityProductVO  extends PartnerActivityProductAudit {

	private String auditId;
	private String shopName;
	private Float discount;
	private Float reduction;
	private BigDecimal oldPrice;
	private BigDecimal actPrice;
	private String reason;
	
	public XQActivityProductVO(CampaignProductEX ex){
		auditId = ex.getTicketId();
		reduction = ex.getReduction()==null?0:ex.getReduction();
		discount = ex.getDiscount()==null?0:ex.getDiscount();
		
		setShortName(ex.getShortName());
		setProductId(ex.getProductId());
		setActId(ex.getActivityId());
		setAuditState(ex.getAuditStatus());
		setAuditReason(ex.getAuditReason());  // 审核原因
		setReason(ex.getReason());  				 // 报名理由
		setAuditor(ex.getAuditor());
		setProductBrand(ex.getProductBrand());
		setImageApp(ex.getImageApp());
		setImagePc(ex.getImagePc());
		setSort(ex.getSort());
		setStartTime(ex.getActivityStartTime() != null ? ex.getActivityStartTime().getTime() : null);
		setEndTime(ex.getActivityEndTime() != null ? ex.getActivityEndTime().getTime() : null);
	}

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public BigDecimal getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(BigDecimal oldPrice) {
		this.oldPrice = oldPrice;
	}

	public BigDecimal getActPrice() {
		return actPrice;
	}

	public void setActPrice(BigDecimal actPrice) {
		this.actPrice = actPrice;
	}

	public Float getDiscount() {
		return discount;
	}

	public void setDiscount(Float discount) {
		this.discount = discount;
	}

	public Float getReduction() {
		return reduction;
	}

	public void setReduction(Float reduction) {
		this.reduction = reduction;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}

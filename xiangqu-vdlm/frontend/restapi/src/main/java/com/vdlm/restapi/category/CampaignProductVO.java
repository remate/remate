package com.vdlm.restapi.category;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.status.ActivityTicketAuditStatus;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.dal.util.json.JsonResourceUrlSerializer;

public class CampaignProductVO extends Product {
    
    private static final long serialVersionUID = 6615224705524050787L;

    private String ticketId;

    // 活动id
    private String activityId;
    
    private String productId;
    
    // 折扣，多少的折扣off
    private Float discount;
    
    // 优惠，减多少钱
    private Float reduction;
    
    // 活动库存
    private Integer activityAmount;
    
    @JsonSerialize(using = JsonResourceUrlSerializer.class)         
    private String imgUrl;
    
    private ActivityStatus activityStatus;
	
	private ActivityTicketAuditStatus auditStatus;
	
	private Date activityStartTime;
	
	private Date activityEndTime;
	
	private String feedback;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public Integer getActivityAmount() {
        return activityAmount;
    }

    public void setActivityAmount(Integer activityAmount) {
        this.activityAmount = activityAmount;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

	public ActivityStatus getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(ActivityStatus activityStatus) {
		this.activityStatus = activityStatus;
	}

	public ActivityTicketAuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(ActivityTicketAuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getActivityStartTime() {
		return activityStartTime;
	}

	public void setActivityStartTime(Date activityStartTime) {
		this.activityStartTime = activityStartTime;
	}

	public Date getActivityEndTime() {
		return activityEndTime;
	}

	public void setActivityEndTime(Date activityEndTime) {
		this.activityEndTime = activityEndTime;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}

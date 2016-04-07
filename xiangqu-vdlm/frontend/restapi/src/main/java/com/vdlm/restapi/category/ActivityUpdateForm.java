package com.vdlm.restapi.category;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ActivityUpdateForm {
    
	@NotBlank
    private String id;
    // 活动名称
	@NotBlank(message = "{activity.name.not.null}")
    private String name;

    // 提醒
    private Boolean remind;
    
    // 活动开始时间
    @NotNull(message = "{activity.startTime.not.null}")
    private Long startTime;
    
    // 活动结束时间
    @NotNull(message = "{activity.endTime.not.null}")
    private Long endTime;
    
    // 优惠类型
    @NotNull
    @Max(value=3, message="{invalid.argument}")
	@Min(value=1, message="{invalid.argument}")
    private Integer preferentialType;
    
    // 折扣
    private Float discount;
    
    // 优惠立即减
    private Float reduction;

    // 活动简述
    private String summary;
    
    // 活动描述
    private String details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRemind() {
        return remind;
    }

    public void setRemind(Boolean remind) {
        this.remind = remind;
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

    public Integer getPreferentialType() {
        return preferentialType;
    }

    public void setPreferentialType(Integer preferentialType) {
        this.preferentialType = preferentialType;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    
    

}
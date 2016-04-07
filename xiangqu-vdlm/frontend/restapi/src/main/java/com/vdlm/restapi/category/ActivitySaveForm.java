package com.vdlm.restapi.category;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityType;

public class ActivitySaveForm {
	
    // 活动名称
    @NotBlank(message = "{activity.name.not.null}")
    private String name;
    
    @NotNull(message = "{activity.startTime.not.null}")
    private Long startTime;
    
    @NotNull(message = "{activity.endTime.not.null}")
    private Long endTime;
    
    private String banner;
    
    private String img;
    
    // 提醒
    private Boolean remind;
    
    // 优惠类型
    @NotNull
    @Max(value=3, message="{invalid.argument}")
	@Min(value=1, message="{invalid.argument}")
    private Integer preferentialType;
    
    // 优惠折扣
    private Float discount;
    
    private Float reduction;
    
    // 活动描述
    private String details;
    
    private Long applyStartTime;
    
    private Long applyEndTime;
    
    private ActivityType type;
    
    private String url;
    
    private String reason;

    private String summary;
    
    // 私有接口渠道可以直接设置默认渠道
    private ActivityChannel channel = ActivityChannel.PRIVATE;
    
    private String tagImage;
    
    private String id;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Long getApplyStartTime() {
		return applyStartTime;
	}

	public void setApplyStartTime(Long applyStartTime) {
		this.applyStartTime = applyStartTime;
	}

	public Long getApplyEndTime() {
		return applyEndTime;
	}

	public void setApplyEndTime(Long applyEndTime) {
		this.applyEndTime = applyEndTime;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public ActivityChannel getChannel() {
		return channel;
	}

	public void setChannel(ActivityChannel channel) {
		this.channel = channel;
	}

	public String getTagImage() {
		return tagImage;
	}

	public void setTagImage(String tagImage) {
		this.tagImage = tagImage;
	}

	public Float getReduction() {
		return reduction;
	}

	public void setReduction(Float reduction) {
		this.reduction = reduction;
	}

}
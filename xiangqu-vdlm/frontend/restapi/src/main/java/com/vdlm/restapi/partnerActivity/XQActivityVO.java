package com.vdlm.restapi.partnerActivity;

import com.vdlm.dal.model.Activity;
import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.dal.type.ActivityType;

public class XQActivityVO {

	private String actId; // 活动ID
	private String actTitle; // 活动标题
	private String actDesc;   // 活动描述
	private Long actStartTime;  // 活动开始时间
	private Long actEndTime;	// 活动结束时间
	private ActivityChannel actChannel; // 活动所属平台
	private ActivityType actType; // 活动类型
	private ActivityStatus actState; // 活动状态
	private String actTagImage; // 活动图片
	private Integer actTagType;    // 标签图位置1=下 2=上
	private String actUrl; // 下发活动url
	private String createOper; // 创建人
	private String extUid;// 第三方用户id
	private Long createTime;   // 创建时间
	private Long updateTime; // 更新时间
	
	private Long applyStartTime;  // 报名开始时间
	private Long applyEndTime;
	private String applyDesc;  // 卖家报名要求
	private String shopId;

	
	public XQActivityVO(Activity vo){
		this.actId = vo.getId();
		this.actTitle = vo.getName();
		this.actDesc = vo.getDetails();
		this.setActStartTime(vo.getStartTime() != null ?  vo.getStartTime().getTime() : null);
		this.setActEndTime(vo.getEndTime() != null ? vo.getEndTime().getTime() : null);
		this.setApplyStartTime(vo.getApplyStartTime() != null ? vo.getApplyStartTime().getTime() : null);
		this.setApplyEndTime(vo.getApplyEndTime() != null ? vo.getApplyEndTime().getTime() : null);
		this.actDesc = vo.getDetails();
		this.applyDesc = vo.getApplyDesc();
		this.actChannel = vo.getChannel();
		this.actType = vo.getType();
		this.actState = vo.getStatus();
		this.actTagImage = vo.getTagImage();
		this.actTagType = vo.getActTagType();
		this.actUrl = vo.getUrl();
		this.createOper = vo.getCreatorId();
		if (vo.getCreatedAt()!=null) this.createTime = vo.getCreatedAt().getTime();
		if (vo.getUpdatedAt()!=null) this.updateTime = vo.getUpdatedAt().getTime();
	}
	
	public XQActivityVO(){ }

	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	public String getActTitle() {
		return actTitle;
	}

	public void setActTitle(String actTitle) {
		this.actTitle = actTitle;
	}

	public String getActDesc() {
		return actDesc;
	}

	public void setActDesc(String actDesc) {
		this.actDesc = actDesc;
	}

	public String getApplyDesc() {
		return applyDesc;
	}

	public void setApplyDesc(String applyDesc) {
		this.applyDesc = applyDesc;
	}

	public ActivityChannel getActChannel() {
		return actChannel;
	}

	public void setActChannel(ActivityChannel actChannel) {
		this.actChannel = actChannel;
	}

	public ActivityType getActType() {
		return actType;
	}

	public void setActType(ActivityType actType) {
		this.actType = actType;
	}

	public ActivityStatus getActState() {
		return actState;
	}

	public void setActState(ActivityStatus actState) {
		this.actState = actState;
	}

	public String getActTagImage() {
		return actTagImage;
	}

	public void setActTagImage(String actTagImage) {
		this.actTagImage = actTagImage;
	}

	public String getActUrl() {
		return actUrl;
	}

	public void setActUrl(String actUrl) {
		this.actUrl = actUrl;
	}

	public Integer getActTagType() {
		return actTagType;
	}

	public void setActTagType(Integer actTagType) {
		this.actTagType = actTagType;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateOper() {
		return createOper;
	}

	public void setCreateOper(String createOper) {
		this.createOper = createOper;
	}

	public Long getActStartTime() {
		return actStartTime;
	}

	public void setActStartTime(Long actStartTime) {
		this.actStartTime = actStartTime;
	}

	public Long getActEndTime() {
		return actEndTime;
	}

	public void setActEndTime(Long actEndTime) {
		this.actEndTime = actEndTime;
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

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getExtUid() {
		return extUid;
	}

	public void setExtUid(String extUid) {
		this.extUid = extUid;
	}
	
	
	 

}

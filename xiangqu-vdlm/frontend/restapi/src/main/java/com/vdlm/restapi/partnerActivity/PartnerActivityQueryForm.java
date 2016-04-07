package com.vdlm.restapi.partnerActivity;

import javax.validation.constraints.NotNull;

import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityStatus;

public class PartnerActivityQueryForm {

	private String title;
	@NotNull
	private ActivityChannel actChannel;
	private Long actStartTime;
	private Long actEndTime;
	private Long applyStartTime;
	private Long applyEndTime;
	private ActivityStatus actState;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ActivityChannel getActChannel() {
		return actChannel;
	}

	public void setActChannel(ActivityChannel actChannel) {
		this.actChannel = actChannel;
	}

	public ActivityStatus getActState() {
		return actState;
	}

	public void setActState(ActivityStatus actState) {
		this.actState = actState;
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

}

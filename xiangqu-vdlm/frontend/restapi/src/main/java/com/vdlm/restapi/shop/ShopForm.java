package com.vdlm.restapi.shop;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import com.vdlm.dal.model.ThirdCommission;
import com.vdlm.dal.status.ShopStatus;

public class ShopForm {
	
	private String id;
	
	//@NotBlank(message = "{valid.notBlank.message}")
	@Size(min = 2, max = 20, message = "{valid.shop.name.message}")
	private String name;

	//@NotBlank(message = "{valid.shop.img.notBlank.message}")
	@Size(min = 4, max = 60, message = "{valid.shop.img.message}")
	private String img;

	@Size(min = 4, max = 200, message = "{valid.shop.description.message}")
	private String description;// 店铺说明

	@Size(min = 0, max = 40, message = "{valid.shop.wechat.message}")
	private String wechat; // 微信号
	
	@Size(min = 0, max = 200, message = "{valid.shop.bulletin.message}")
	private String bulletin;
	
	private Date bulletinAt; //公告日期

	private String banner;// 店铺招牌
	
	private Boolean danbao; //是否开能担保交易，默认为不开通

	private ShopStatus status; // 状态
	
	private BigDecimal commisionRate; // 佣金比例
	
	private ThirdCommission thirdCommission;  // 第三方佣金设置
	
	private Boolean postageStatus;	// 是否设置了邮费

	private String freeZone;		// 免邮地区
	
	@Digits(integer=10, fraction=2, message = "{valid.postage.range}")
	private BigDecimal postage;	// 邮费

	private Long provinceId;
	
	private Long cityId;
	
	private String ownerId; // 店铺拥有者

	
	public Date getBulletinAt() {
		return bulletinAt;
	}

	public void setBulletinAt(Date bulletinAt) {
		this.bulletinAt = bulletinAt;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public Boolean getDanbao() {
		return danbao;
	}

	public void setDanbao(Boolean danbao) {
		this.danbao = danbao;
	}

	public ShopStatus getStatus() {
		return status;
	}

	public void setStatus(ShopStatus status) {
		this.status = status;
	}

	public BigDecimal getCommisionRate() {
		return commisionRate;
	}

	public void setCommisionRate(BigDecimal commisionRate) {
		this.commisionRate = commisionRate;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Boolean getPostageStatus() {
		return postageStatus;
	}

	public void setPostageStatus(Boolean postageStatus) {
		this.postageStatus = postageStatus;
	}

	public String getFreeZone() {
		return freeZone;
	}

	public void setFreeZone(String freeZone) {
		this.freeZone = freeZone;
	}

	public BigDecimal getPostage() {
		return postage;
	}

	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getBulletin() {
		return bulletin;
	}

	public void setBulletin(String bulletin) {
		this.bulletin = bulletin;
		this.bulletinAt = new Date();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public ThirdCommission getThirdCommission() {
		return thirdCommission;
	}

	public void setThirdCommission(ThirdCommission thirdCommission) {
		this.thirdCommission = thirdCommission;
	}
}
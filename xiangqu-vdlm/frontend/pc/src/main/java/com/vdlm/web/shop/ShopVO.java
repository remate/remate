

package com.vdlm.web.shop;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.ThirdCommission;
import com.vdlm.dal.util.json.JsonResourceUrlSerializer;

public class ShopVO extends Shop {

	private static final long serialVersionUID = 1L;

	private String shopUrl;

	@JsonSerialize(using = JsonResourceUrlSerializer.class)			
	private String imgUrl;
	
	@JsonSerialize(using = JsonResourceUrlSerializer.class)			
	private String bannerUrl;
	
	private List<ThirdCommission> thirdCommissions;

	private Long countDraft;
	
	private Long countForsale;

	private Long countOnsale;
	
	private Long countOutofstock;
	
	private Long countOfOrderSubmitted;
	
	private Long countOfOrderPaid;
	
	private Long countOfOrderShipped;
	
	private Long countOfOrderSuccess;
	
	private Long countOfOrderClose;

	private String localName;//所在地
	
	private List<String> postFreeRegions;	// 免邮地区
	
	public List<String> getPostFreeRegions() {
		return postFreeRegions;
	}

	public void setPostFreeRegions(List<String> postFreeRegions) {
		this.postFreeRegions = postFreeRegions;
	}

	public ShopVO(Shop shop, UrlHelper helper) {
		BeanUtils.copyProperties(shop, this);
		shopUrl = helper.genShopUrl(shop.getId());
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public Long getCountDraft() {
		return countDraft;
	}

	public void setCountDraft(Long countDraft) {
		this.countDraft = countDraft;
	}

	public Long getCountForsale() {
		return countForsale;
	}

	public void setCountForsale(Long countForsale) {
		this.countForsale = countForsale;
	}

	public Long getCountOutofstock() {
		return countOutofstock;
	}

	public void setCountOutofstock(Long countOutofstock) {
		this.countOutofstock = countOutofstock;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public Long getCountOnsale() {
		return countOnsale;
	}

	public void setCountOnsale(Long countOnsale) {
		this.countOnsale = countOnsale;
	}

	public Long getCountOfOrderSubmitted() {
		return countOfOrderSubmitted;
	}

	public void setCountOfOrderSubmitted(Long countOfOrderSubmitted) {
		this.countOfOrderSubmitted = countOfOrderSubmitted;
	}

	public Long getCountOfOrderPaid() {
		return countOfOrderPaid;
	}

	public void setCountOfOrderPaid(Long countOfOrderPaid) {
		this.countOfOrderPaid = countOfOrderPaid;
	}

	public Long getCountOfOrderShipped() {
		return countOfOrderShipped;
	}

	public void setCountOfOrderShipped(Long countOfOrderShipped) {
		this.countOfOrderShipped = countOfOrderShipped;
	}

	public Long getCountOfOrderSuccess() {
		return countOfOrderSuccess;
	}

	public void setCountOfOrderSuccess(Long countOfOrderSuccess) {
		this.countOfOrderSuccess = countOfOrderSuccess;
	}

	public Long getCountOfOrderClose() {
		return countOfOrderClose;
	}

	public void setCountOfOrderClose(Long countOfOrderClose) {
		this.countOfOrderClose = countOfOrderClose;
	}

	public List<ThirdCommission> getThirdCommissions() {
		return thirdCommissions;
	}

	public void setThirdCommissions(List<ThirdCommission> thirdCommissions) {
		this.thirdCommissions = thirdCommissions;
	}
	
}


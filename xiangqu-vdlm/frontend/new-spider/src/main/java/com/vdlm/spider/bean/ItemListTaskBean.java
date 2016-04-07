package com.vdlm.spider.bean;

import org.springframework.beans.BeanUtils;

import com.vdlm.spider.ShopType;

/**
 *
 * @author: chenxi
 */

public class ItemListTaskBean extends TaskBean implements TaskIdentiable<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8054629051431257538L;
	
	private String ouerUserId;
	private String ouerShopId;

	private ShopType shopType;
	private String shopUrl;
	private String shopName;

	private String requestUrl;
	private String refererUrl;
	
	private String nickname;
	private String tUserId;
	
	private long shopId;
	
	private Integer option;
	
	// TODO
	public String getOuerUserId() {
		return ouerUserId;
	}
	
	public void setOuerUserId(String ouerUserId) {
		this.ouerUserId = ouerUserId;
	}
	
	public String getOuerShopId() {
		return ouerShopId;
	}
	
	public void setOuerShopId(String ouerShopId) {
		this.ouerShopId = ouerShopId;
	}
	
	public ShopType getShopType() {
		return shopType;
	}
	
	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}
	
	public String getShopUrl() {
		return shopUrl;
	}
	
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	
	public String getShopName() {
		return shopName;
	}
	
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	public String getRequestUrl() {
		return requestUrl;
	}
	
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	public String getRefererUrl() {
		return refererUrl;
	}
	
	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getTUserId() {
		return tUserId;
	}
	
	public void setTUserId(String tUserId) {
		this.tUserId = tUserId;
	}

	@Override
	public ItemListTaskBean clone() {
		final ItemListTaskBean bean = new ItemListTaskBean();
		BeanUtils.copyProperties(this, bean);
		return bean;
	}

	@Override
	public String getId() {
		return ouerShopId;
	}
	
	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public String getTaskName() {
		return "itemlist";
	}

	public Integer getOption() {
		return option;
	}

	public void setOption(Integer option) {
		this.option = option;
	}
}

/**
 * 
 */
package com.vdlm.spider.entity;

import java.io.Serializable;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 7:01:16 PM Aug 7, 2014
 */
public class Shop implements Serializable {

	private static final long serialVersionUID = -6690380597977580873L;

	private Long id;
	private String ouerUserId;
	private String ouerShopId;
	private ReqFrom reqFrom;
	private String userId;
	private String shopId;
	private String shopUrl;
	private ShopType shopType;
	private String name;
	private String nickname;
	private String score;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

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

	public ReqFrom getReqFrom() {
		return reqFrom;
	}

	public void setReqFrom(ReqFrom reqFrom) {
		this.reqFrom = reqFrom;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "Shop [id=" + id + ", ouerUserId=" + ouerUserId
				+ ", ouerShopId=" + ouerShopId + ", reqFrom=" + reqFrom
				+ ", userId=" + userId + ", shopId=" + shopId + ", shopUrl="
				+ shopUrl + ", shopType=" + shopType + ", name=" + name
				+ ", nickname=" + nickname + ", score=" + score + "]";
	}

}

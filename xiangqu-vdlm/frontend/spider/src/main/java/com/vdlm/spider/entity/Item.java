/**
 * 
 */
package com.vdlm.spider.entity;

import java.io.Serializable;
import java.util.List;

import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:58:12 PM Jul 17, 2014
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -3393159029537000364L;
	private Long id;
	private String ouerUserId;
	private String ouerShopId;
	private String name;
	private Double price;// 最小价格
	private Integer amount;

	private String itemUrl;
	private ShopType shopType;
	private String itemId;
	private Integer status;
	private ReqFrom reqFrom;
	private String userId;
	private String shopId;

	private String imgs; // 多个之间用逗号分隔
	
	private List<String> skuTypes;
	private List<List<String>> skuTexts;
	
	private String details; //详情
	private List<FragmentVO> fragments; //详情片段

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
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

	public ReqFrom getReqFrom() {
		return reqFrom;
	}

	public void setReqFrom(ReqFrom reqFrom) {
		this.reqFrom = reqFrom;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}

	public ShopType getShopType() {
		return shopType;
	}

	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	public List<String> getSkuTypes() {
		return skuTypes;
	}
	
	public void setSkuTypes(List<String> skuTypes) {
		this.skuTypes = skuTypes;
	}
	
	public List<List<String>> getSkuTexts() {
		return skuTexts;
	}
	
	public void setSkuTexts(List<List<String>> skuTexts) {
		this.skuTexts = skuTexts;
	}
	
	public String getDetails(){
		return details;
	}
	
	public void setDetails(String details){
		this.details = details;
	}
	
	public List<FragmentVO> getFragments() {
		return fragments;
	}
	
	public void setFragments(List<FragmentVO> fragments) {
		this.fragments = fragments;
	}
}

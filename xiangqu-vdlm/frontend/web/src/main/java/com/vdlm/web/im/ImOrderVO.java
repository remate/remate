package com.vdlm.web.im;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.vdlm.dal.model.OrderAddress;

public class ImOrderVO {
	private String buyerId;
	private String sellerId;
	private String avatar;
	private String nickName;
	private String id; //订单ID
	private String imgUrl;//订单图片链接
	private String orderNo;//订单号码
	private String shopId;//店铺号
	private List<ImProductVO> orderItems;
	private OrderAddress address;  // 收货人地址信息
	
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public OrderAddress getAddress() {
		return address;
	}
	public void setAddress(OrderAddress address) {
		this.address = address;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public List<ImProductVO> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<ImProductVO> orderItems) {
		this.orderItems = orderItems;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}

//package com.vdlm.spider.entity;
//
//import java.io.Serializable;
//
///**
// *
// * @author: chenxi
// */
//
//public class ShopProcess implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -6477323799084081069L;
//
//	private Long id;
//	private Long shopId;
//	
//	private int itemCount;
//	private int curItemCount = 0;
//	
//	private boolean partially = false;
//	
//	public Long getId() {
//		return id;
//	}
//	
//	public void setId(Long id) {
//		this.id = id;
//	}
//	
//	public Long getShopId() {
//		return shopId;
//	}
//	
//	public void setShopId(Long shopId) {
//		this.shopId = shopId;
//	}
//	
//	public int getItemCount() {
//		return itemCount;
//	}
//	
//	public void setItemCount(int itemCount) {
//		this.itemCount = itemCount;
//	}
//	
//	public int getCurItemCount() {
//		return curItemCount;
//	}
//	
//	public void setCurItemCount(int curItemCount) {
//		this.curItemCount = curItemCount;
//	}
//	
//	public boolean completed() {
//		return (itemCount == curItemCount) && !partially;
//	}
//
//	public boolean isPartially() {
//		return partially;
//	}
//
//	public void setPartially(boolean partially) {
//		this.partially = partially;
//	}
//
//	@Override
//	public String toString() {
//		return "ShopProcess [id=" + id + ", shopId=" + shopId + ", itemCount="
//				+ itemCount + ", curItemCount=" + curItemCount + ", partially="
//				+ partially + "]";
//	}
//
//}

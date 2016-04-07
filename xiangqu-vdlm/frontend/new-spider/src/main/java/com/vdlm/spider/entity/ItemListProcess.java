package com.vdlm.spider.entity;

import java.io.Serializable;

import com.vdlm.spider.SpideItemType;

/**
 *
 * @author: chenxi
 */

public class ItemListProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6477323799084081069L;

	private Long id;
	private Long shopId;
	
	private int itemCount;
	private int curItemCount = 0;
	
	private boolean partially = false;
	
	private SpideItemType type = SpideItemType.ITEM; // default type
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getShopId() {
		return shopId;
	}
	
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	
	public int getItemCount() {
		return itemCount;
	}
	
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
	public int getCurItemCount() {
		return curItemCount;
	}
	
	public void setCurItemCount(int curItemCount) {
		this.curItemCount = curItemCount;
	}
	
	public boolean completed() {
		return (curItemCount >= itemCount) && !partially;
	}

	public boolean isPartially() {
		return partially;
	}

	public void setPartially(boolean partially) {
		this.partially = partially;
	}

	public SpideItemType getType() {
		return type;
	}

	public void setType(SpideItemType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ItemListProcess [id=" + id + ", shopId=" + shopId + ", itemCount="
				+ itemCount + ", curItemCount=" + curItemCount + ", partially="
				+ partially + "]";
	}

}

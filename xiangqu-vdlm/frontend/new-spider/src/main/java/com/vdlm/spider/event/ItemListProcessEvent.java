package com.vdlm.spider.event;

import com.vdlm.spider.entity.ItemListProcess;

/**
 *
 * @author: chenxi
 */

public class ItemListProcessEvent {

	private final ItemListProcessEventType type;
	private ItemListProcess initData;
	
	private long shopId;
	private String ouerShopId;
	private boolean partially;
	
	public ItemListProcessEvent(ItemListProcessEventType type) {
		this.type = type;
	}

	public ItemListProcess getInitData() {
		return initData;
	}

	public void setInitData(ItemListProcess initData) {
		this.initData = initData;
	}

	public ItemListProcessEventType getType() {
		return type;
	}

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public String getOuerShopId() {
		return ouerShopId;
	}

	public void setOuerShopId(String ouerShopId) {
		this.ouerShopId = ouerShopId;
	}

	public boolean isPartially() {
		return partially;
	}

	public void setPartially(boolean partially) {
		this.partially = partially;
	}
	
}

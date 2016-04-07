package com.vdlm.spider.event;

import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ItemProcess;

/**
 *
 * @author: chenxi
 */

public class ItemProcessEvent {

	private final ItemProcessEventType type;
	private ItemProcess initData;
	private Img img;
	private int detailImgCount;
	
	private long itemId;
	
	private long cleanShopId;
	
	public ItemProcessEvent(ItemProcessEventType type) {
		this.type = type;
	}

	public ItemProcessEventType getType() {
		return type;
	}

	public Img getImg() {
		return img;
	}

	public void setImg(Img img) {
		this.img = img;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public ItemProcess getInitData() {
		return initData;
	}

	public void setInitData(ItemProcess initData) {
		this.initData = initData;
	}

	public int getDetailImgCount() {
		return detailImgCount;
	}

	public void setDetailImgCount(int detailImgCount) {
		this.detailImgCount = detailImgCount;
	}

	public long getCleanShopId() {
		return cleanShopId;
	}

	public void setCleanShopId(long cleanShopId) {
		this.cleanShopId = cleanShopId;
	}
	
}

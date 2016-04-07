package com.vdlm.spider.event;

import java.io.InputStream;
import java.util.List;

import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;

/**
 *
 * @author: chenxi
 */

public class ItemEvent {

	private final ItemEventType type;
	private Item item;
	
	private Long itemId;
	private InputStream imgInput;
	private Img img;
	private List<Sku> skus;
	
	private String failedDesc;
	
	private boolean needRetry = true;
	
	// this fields is true when user manually update skus information of a item, default is false
	private boolean onlyUpdateSkus = false;
	
	public ItemEvent(ItemEventType type) {
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public InputStream getImgInput() {
		return imgInput;
	}

	public void setImgInput(InputStream imgInput) {
		this.imgInput = imgInput;
	}

	public Img getImg() {
		return img;
	}

	public void setImg(Img img) {
		this.img = img;
	}

	public List<Sku> getSkus() {
		return skus;
	}

	public void setSkus(List<Sku> skus) {
		this.skus = skus;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getFailedDesc() {
		return failedDesc;
	}

	public void setFailedDesc(String failedDesc) {
		this.failedDesc = failedDesc;
	}

	public boolean isNeedRetry() {
		return needRetry;
	}

	public void setNeedRetry(boolean needRetry) {
		this.needRetry = needRetry;
	}

	public ItemEventType getType() {
		return type;
	}

	public boolean isOnlyUpdateSkus() {
		return onlyUpdateSkus;
	}

	public void setOnlyUpdateSkus(boolean onlyUpdateSkus) {
		this.onlyUpdateSkus = onlyUpdateSkus;
	}
	
}

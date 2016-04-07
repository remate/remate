package com.vdlm.spider.event;


import com.vdlm.spider.SpideItemType;

/**
 *
 * @author: chenxi
 */

public class ItemCompletedEvent {

	private final Long itemId;
	private SpideItemType type = SpideItemType.ITEM;
	private Integer option;
	
	public ItemCompletedEvent(Long itemId) {
		this.itemId = itemId;
	}
	
	public ItemCompletedEvent(Long itemId, SpideItemType type) {
		this.itemId = itemId;
		this.type = type;
		this.option = 1;
	}
	
	public ItemCompletedEvent(Long itemId, SpideItemType type, Integer option) {
		this.itemId = itemId;
		this.type = type;
		this.option = option;
	}

	public Long getItemId() {
		return itemId;
	}

	public SpideItemType getType() {
		return type;
	}

	public void setType(SpideItemType type) {
		this.type = type;
	}

	public Integer getOption() {
		return option;
	}

	public void setOption(Integer option) {
		this.option = option;
	}
}

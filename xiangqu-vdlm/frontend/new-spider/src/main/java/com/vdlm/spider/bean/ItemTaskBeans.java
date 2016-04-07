package com.vdlm.spider.bean;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class ItemTaskBeans {

	private final List<ItemTaskBean> items;
	
	public ItemTaskBeans(List<ItemTaskBean> items) {
		this.items = items;
	}

	public List<ItemTaskBean> getItems() {
		return items;
	}
}

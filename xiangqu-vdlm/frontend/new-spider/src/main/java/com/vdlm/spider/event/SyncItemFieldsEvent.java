package com.vdlm.spider.event;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class SyncItemFieldsEvent {

	private final String itemId;
	private final List<String> fields;
	
	public SyncItemFieldsEvent(String itemId, List<String> fields) {
		this.itemId = itemId;
		this.fields = fields;
	}

	public String getItemId() {
		return itemId;
	}

	public List<String> getFields() {
		return fields;
	}
}

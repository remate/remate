package com.vdlm.spider.event;

/**
 *
 * @author: chenxi
 */

public enum ItemEventType {

	SAVE_ITEM,
	UPLOAD_IMG,
	SAVE_SKUS,
	TOLERANCE_IMG,  // when download img results 404, spider flow continues but do filter work in syncItem process 
	MAX_RETRIES,
	NEED_RETRY,
	
	SAVE_ITEM_SKUS;
}

package com.vdlm.spider.event;

/**
 *
 * @author: chenxi
 */

public class PItemEvent {

	private final PItemEventType type;
	private final String itemId;
	private final ItemInfo itemInfo;
	
	public PItemEvent(PItemEventType type, String itemId) {
		this.type = type;
		this.itemId = itemId;
		itemInfo = new ItemInfo();
	}

	public PItemEventType getType() {
		return type;
	}

	public String getItemId() {
		return itemId;
	}
	
	public ItemInfo getItemInfo() {
		return itemInfo;
	}

	public static class ItemInfo {
		private Long id;
		private int groupImgCnt;
		private int skuImgCnt;
		private int detailImgCnt;
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public int getGroupImgCnt() {
			return groupImgCnt;
		}
		
		public void setGroupImgCnt(int groupImgCnt) {
			this.groupImgCnt = groupImgCnt;
		}
		
		public int getSkuImgCnt() {
			return skuImgCnt;
		}
		
		public void setSkuImgCnt(int skuImgCnt) {
			this.skuImgCnt = skuImgCnt;
		}
		
		public int getDetailImgCnt() {
			return detailImgCnt;
		}
		
		public void setDetailImgCnt(int detailImgCnt) {
			this.detailImgCnt = detailImgCnt;
		}
		
	}
}

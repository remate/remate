package com.vdlm.spider.entity;

import java.io.Serializable;
import java.sql.Date;

import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.parser.config.ParserConfigProviders;

/**
 *
 * @author: chenxi
 */

public class ItemProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5205679932930880613L;

	private Long id;
	private Long itemId;
	private Long shopId;
	
	private boolean descParsed;
	private boolean skuParsed;
	
	private int groupImgCount;
	private int skuImgCount;
	private int detailImgCount = 99; // set a big number for init
	
	private int curGroupImgCount;
	private int curSkuImgCount;
	private int curDetailImgCount;
	
	private SpideItemType type = SpideItemType.ITEM; // default type
	private Integer option = 1; // 0: just create,  1: create and update   in kkkd
	
	private Date createAt;
	private Date updateAt;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getItemId() {
		return itemId;
	}
	
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	public Long getShopId() {
		return shopId;
	}
	
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	
	public boolean isDescParsed() {
		return descParsed;
	}
	
	public void setDescParsed(boolean descParsed) {
		this.descParsed = descParsed;
	}
	
	public boolean isSkuParsed() {
		return skuParsed;
	}
	
	public void setSkuParsed(boolean skuParsed) {
		this.skuParsed = skuParsed;
	}
	
	public int getGroupImgCount() {
		return groupImgCount;
	}
	
	public void setGroupImgCount(int groupImgCount) {
		this.groupImgCount = groupImgCount;
	}
	
	public int getSkuImgCount() {
		return skuImgCount;
	}
	
	public void setSkuImgCount(int skuImgCount) {
		this.skuImgCount = skuImgCount;
	}
	
	public int getDetailImgCount() {
		return detailImgCount;
	}
	
	public void setDetailImgCount(int detailImgCount) {
		this.detailImgCount = detailImgCount;
	}
	
	public int getCurGroupImgCount() {
		return curGroupImgCount;
	}
	
	public void setCurGroupImgCount(int curGroupImgCount) {
		this.curGroupImgCount = curGroupImgCount;
	}
	
	public int getCurSkuImgCount() {
		return curSkuImgCount;
	}
	
	public void setCurSkuImgCount(int curSkuImgCount) {
		this.curSkuImgCount = curSkuImgCount;
	}
	
	public int getCurDetailImgCount() {
		return curDetailImgCount;
	}
	
	public void setCurDetailImgCount(int curDetailImgCount) {
		this.curDetailImgCount = curDetailImgCount;
	}
	
	public SpideItemType getType() {
		return type;
	}
	
	public void setType(SpideItemType type) {
		this.type = type;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public Date getUpdateAt() {
		return updateAt;
	}
	
	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
	
	public boolean completed() {
		return descParsed && skuParsed 
				&& (curGroupImgCount >= groupImgCount)
				&& (curSkuImgCount >= skuImgCount)
				&& ((curDetailImgCount >= detailImgCount) || (curDetailImgCount >= ParserConfigProviders.getDetailImgMaxSize()));
	}
	
	@Override
	public String toString() {
		return "ItemProcess [id=" + id + ", itemId=" + itemId + ", descParsed="
				+ descParsed + ", skuParsed=" + skuParsed + ", groupImgCount="
				+ groupImgCount + ", skuImgCount=" + skuImgCount
				+ ", detailImgCount=" + detailImgCount + ", curGroupImgCount="
				+ curGroupImgCount + ", curSkuImgCount=" + curSkuImgCount
				+ ", curDetailImgCount=" + curDetailImgCount + ", createAt="
				+ createAt + ", updateAt=" + updateAt + "]";
	}
	public Integer getOption() {
		return option;
	}
	public void setOption(Integer option) {
		this.option = option;
	}
	
}

package com.vdlm.spider.wdetail;

import java.util.Set;

/**
 *
 * @author: chenxi
 */

public class ItemInfoModel {

	private String itemId;
	private String title;
	private Set<String> picsPath;
	private String favcount;
	private String stuffStatus;
	private String itemUrl;
	private boolean sku;
	private String location;
	private String saleLine;
	private String categoryId;
	private String itemTypeName;
	
	public String getItemId() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Set<String> getPicsPath() {
		return picsPath;
	}
	
	public void setPicsPath(Set<String> picsPath) {
		this.picsPath = picsPath;
	}
	
	public String getFavcount() {
		return favcount;
	}
	
	public void setFavcount(String favcount) {
		this.favcount = favcount;
	}
	
	public String getStuffStatus() {
		return stuffStatus;
	}
	
	public void setStuffStatus(String stuffStatus) {
		this.stuffStatus = stuffStatus;
	}
	
	public String getItemUrl() {
		return itemUrl;
	}
	
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	
	public boolean isSku() {
		return sku;
	}
	
	public void setSku(boolean sku) {
		this.sku = sku;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getSaleLine() {
		return saleLine;
	}
	
	public void setSaleLine(String saleLine) {
		this.saleLine = saleLine;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getItemTypeName() {
		return itemTypeName;
	}
	
	public void setItemTypeName(String itemTypeName) {
		this.itemTypeName = itemTypeName;
	}
	
}

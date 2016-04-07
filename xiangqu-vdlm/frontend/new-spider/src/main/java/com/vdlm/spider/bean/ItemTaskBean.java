package com.vdlm.spider.bean;

import org.springframework.beans.BeanUtils;

/**
 *
 * @author: chenxi
 */

public class ItemTaskBean extends ShopTaskBean implements TaskIdentiable<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5143630927071924982L;

	private String itemUrl;
	
	// indicate whether the taks is an manually task which only applies this single item task, default is false
	private boolean add = true;
	
	public String getItemUrl() {
		return itemUrl;
	}
	
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	
	public boolean isAdd() {
		return add;
	}

	public void setAdd(boolean add) {
		this.add = add;
	}
	
	@Override
	public ItemTaskBean clone() {
		final ItemTaskBean bean = new ItemTaskBean();
		BeanUtils.copyProperties(this, bean);
		return bean;
	}
	
	@Override
	public String getId() {
		return getItemId();
	}
	
	@Override
	public String getTaskName() {
		return "item";
	}
}

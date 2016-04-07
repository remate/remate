package com.vdlm.spider.bean;

import org.springframework.beans.BeanUtils;

/**
 *
 * @author: chenxi
 */

public class ShopTaskBean extends ItemListTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String rnd;
	private String mobilePhone;
	private String itemId;
	
	private boolean useProxy = true;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getRnd() {
		return rnd;
	}

	public void setRnd(String rnd) {
		this.rnd = rnd;
	}

	@Override
	public ShopTaskBean clone() {
		final ShopTaskBean bean = new ShopTaskBean();
		BeanUtils.copyProperties(this, bean);
		return bean;
	}
	
	@Override
	public String getTaskName() {
		return "shop";
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
}

package com.vdlm.spider.bean;

/**
 *
 * @author: chenxi
 */

public class DescTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 981440089614920105L;

	private Long itemTaskId;
	private String descUrl;
	private int imgIndex;
	private String props;  // 详情最前面的型号说明
	// TODO

	public String getDescUrl() {
		return descUrl;
	}

	public Long getItemTaskId() {
		return itemTaskId;
	}

	public void setItemTaskId(Long itemTaskId) {
		this.itemTaskId = itemTaskId;
	}

	public void setDescUrl(String descUrl) {
		this.descUrl = descUrl;
	}

	public int getImgIndex() {
		return imgIndex;
	}

	public void setImgIndex(int imgIndex) {
		this.imgIndex = imgIndex;
	}
	
	@Override
	public String getTaskName() {
		return "desc";
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}

}

package com.vdlm.spider.bean;

import com.vdlm.spider.entity.Img;

/**
 *
 * @author: chenxi
 */

public class ImgTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Img img;
	// TODO

	public Img getImg() {
		return img;
	}

	public void setImg(Img img) {
		this.img = img;
	}
	
	@Override
	public String getTaskName() {
		return "img";
	}
}

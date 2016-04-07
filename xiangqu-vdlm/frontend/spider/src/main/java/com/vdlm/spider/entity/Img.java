/**
 * 
 */
package com.vdlm.spider.entity;

import java.io.Serializable;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:52:53 AM Jul 23, 2014
 */
public class Img implements Serializable {

	private static final long serialVersionUID = -5905328450860380978L;
	private Long id;
	private Long itemId;
	private String imgUrl;
	private String img;
	private Integer orderNum;
	private String md5;
	private Integer type; //1-组图,2-sku图,3-详情图

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

}

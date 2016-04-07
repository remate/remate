package com.vdlm.spider.wdetail.parser;

/**
 *
 * @author: chenxi
 */

public class SkuInfo {

	private Double price;
	private Integer amount;
	private String spec;// 颜色:黄色;尺寸:35
	private String origSpec;// 20549:30106;1627207:28320
	private String skuImg;
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	public String getSpec() {
		return spec;
	}
	
	public void setSpec(String spec) {
		this.spec = spec;
	}
	
	public String getOrigSpec() {
		return origSpec;
	}
	
	public void setOrigSpec(String origSpec) {
		this.origSpec = origSpec;
	}

	public String getSkuImg() {
		return skuImg;
	}

	public void setSkuImg(String skuImg) {
		this.skuImg = skuImg;
	}
	
}

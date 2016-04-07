/**
 * 
 */
package com.vdlm.spider.entity;

import java.io.Serializable;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:11:35 PM Jul 17, 2014
 */
public class Sku implements Serializable {

	@Override
	public String toString() {
		return "Sku [id=" + id + ", itemId=" + itemId + ", price=" + price
				+ ", amount=" + amount + ", spec=" + spec + ", origSpec="
				+ origSpec + "]";
	}

	private static final long serialVersionUID = 1425203494692960060L;
	private Long id;
	private Long itemId;
//	private String thirdItemId;
	private Double price;
	private Integer amount;
	private String spec;// 颜色:黄色;尺寸:35
	private String origSpec;// 20549:30106;1627207:28320
	private String imgUrl;
	
	private String skuId;

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

}

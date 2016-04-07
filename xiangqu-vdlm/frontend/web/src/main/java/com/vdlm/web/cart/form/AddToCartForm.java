package com.vdlm.web.cart.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 购物车Form表单逻辑
 * 只在新增购物车的时候验证，查询购物车的时候不验证
 * 
 * @author odin
 * @author tonghu
 */
public class AddToCartForm {
	
	@NotBlank(message = "{valid.notBlank.message}")
	private String skuId;
	
	@NotNull(message = "{valid.notBlank.message}")
	private Integer amount;
	
	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}

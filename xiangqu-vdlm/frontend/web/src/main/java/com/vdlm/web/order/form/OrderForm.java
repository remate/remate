package com.vdlm.web.order.form;

import javax.validation.constraints.NotNull;

import com.vdlm.web.cart.form.AddToCartForm;

/**
 * 下单Form表单逻辑
 * 只在新增购物车的时候验证，查询购物车的时候不验证
 * 
 * @author odin
 */
public class OrderForm {
	
	/**
	 * 单个商品信息（productId,skuId,amount）
	 */
	@NotNull(message = "{valid.notBlank.message}")
	private AddToCartForm[] productSkus;
	
	/**
	 * 备注
	 */
	private String remark;
	
	public AddToCartForm[] getProductSkus() {
		return productSkus;
	}

	public void setProductSkus(AddToCartForm[] productSkus) {
		this.productSkus = productSkus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}

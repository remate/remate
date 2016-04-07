package com.vdlm.web.cart.form;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class CartPricingForm {
    // 商品skuId数组
    @NotEmpty
    private List<String> skuIds;
    // 收件地址区域
    private String zoneId;
    // 使用的优惠券
    private String couponId;
    
    private Integer	qty;
    
    public String getZoneId() {
        return zoneId;
    }
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
    public String getCouponId() {
        return couponId;
    }
    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
    public List<String> getSkuIds() {
        return skuIds;
    }
    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }
	public Integer getQty() {
		return qty;
	}
	public void setQty(Integer qty) {
		this.qty = qty;
	}
    
}

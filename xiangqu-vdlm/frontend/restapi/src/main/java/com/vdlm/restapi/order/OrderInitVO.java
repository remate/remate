package com.vdlm.restapi.order;

import java.util.List;

import com.vdlm.dal.model.Address;
import com.vdlm.restapi.ShopCartItemVo;
import com.vdlm.service.pricing.CouponVO;

public class OrderInitVO {
	
	private List<ShopCartItemVo> shops;
	private Address address;
	private List<CouponVO> coupons;
	public List<ShopCartItemVo> getShops() {
		return shops;
	}
	public void setShops(List<ShopCartItemVo> shops) {
		this.shops = shops;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public List<CouponVO> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<CouponVO> coupons) {
		this.coupons = coupons;
	}
	
}

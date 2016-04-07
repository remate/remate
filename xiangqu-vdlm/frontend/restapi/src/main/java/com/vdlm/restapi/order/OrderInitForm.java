package com.vdlm.restapi.order;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSON;

public class OrderInitForm {

	private List<SkuForm> skuIds;
	
	private String skuIdsJson;
	
	private String addressId;
	
	private String fromBuy;
	
	private String couponsJson;
	
	public List<SkuForm> getSkuIds() {
		if (StringUtils.isNotBlank(this.getSkuIdsJson())) {
			return JSON.parseArray(this.getSkuIdsJson(), SkuForm.class);
		}
		return skuIds;
	}

	public void setSkuIds(List<SkuForm> skuIds) {
		this.skuIds = skuIds;
	}
	
	public String getSkuIdsJson() {
		return skuIdsJson;
	}
	public void setSkuIdsJson(String skuIdsJson) {
		this.skuIdsJson = skuIdsJson;
	}
	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	public String getFromBuy() {
		return fromBuy;
	}
	public void setFromBuy(String fromBuy) {
		this.fromBuy = fromBuy;
	}
	public String getCouponsJson() {
		return couponsJson;
	}
	public void setCouponsJson(String couponsJson) {
		this.couponsJson = couponsJson;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
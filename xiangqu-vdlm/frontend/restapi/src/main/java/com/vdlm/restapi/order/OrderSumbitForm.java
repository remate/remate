package com.vdlm.restapi.order;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vdlm.dal.type.PaymentMode;

/**
 * 提交订单Form表单逻辑 扩展下单Form，新增支付方式
 * 
 * @author odin
 */
public class OrderSumbitForm extends OrderForm {

	private String orderId;

	private String shopId;
	/**
	 * 购买购物车中的单一项目
	 */
	// @NotNull
	// private List<SkuForm> skuIds;
	private String skuIdsJson;

	private List<SkuForm> skuIds;

	// private List<RemarkFrom> remarks;
	private String remarksJson;

	private List<RemarkFrom> remarks;

	private String couponsJson;

	private List<CouponForm> coupons;

	/**
	 * 直接下单，购买数量
	 */
	// private int qty;

	/**
	 * 买家留言
	 */
	private String remark;
	// /**
	// * 支付方式
	// */
	// @NotNull(message = "{payType.notBlank.message}")
	private PaymentMode payType;

	/**
	 * 协议支付号
	 */
	private String payAgreementId;

	private String fromBuy;

	/**
	 * 地址id
	 */
	private String addressId;

	/**
	 * 是否是担保交易
	 */
	private Boolean danbao;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getSkuIdsJson() {
		return skuIdsJson;
	}

	public void setSkuIdsJson(String skuIdsJson) {
		this.skuIdsJson = skuIdsJson;
	}

	public List<SkuForm> getSkuIds() {
		if (StringUtils.isNotBlank(this.getSkuIdsJson())) {
			return JSON.parseArray(this.getSkuIdsJson(), SkuForm.class);
		}
		return skuIds;
	}

	public void setSkuIds(List<SkuForm> skuIds) {
		this.skuIds = skuIds;
	}

	public String getRemarksJson() {
		return remarksJson;
	}

	public void setRemarksJson(String remarksJson) {
		this.remarksJson = remarksJson;
	}

	public List<RemarkFrom> getRemarks() {
		if (StringUtils.isNotBlank(this.getRemarksJson())) {
			return JSON.parseArray(this.getRemarksJson(), RemarkFrom.class);
		}
		return remarks;
	}

	public void setRemarks(List<RemarkFrom> remarks) {
		this.remarks = remarks;
	}

	public String getCouponsJson() {
		return couponsJson;
	}

	public void setCouponsJson(String couponsJson) {
		this.couponsJson = couponsJson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PaymentMode getPayType() {
		return payType;
	}

	public void setPayType(PaymentMode payType) {
		this.payType = payType;
	}

	public String getPayAgreementId() {
		return payAgreementId;
	}

	public void setPayAgreementId(String payAgreementId) {
		this.payAgreementId = payAgreementId;
	}

	public String getFromBuy() {
		return fromBuy;
	}

	public void setFromBuy(String fromBuy) {
		this.fromBuy = fromBuy;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public Boolean getDanbao() {
		return danbao;
	}

	public void setDanbao(Boolean danbao) {
		this.danbao = danbao;
	}

	public void setCoupons(List<CouponForm> coupons) {
		this.coupons = coupons;
	}

	public List<CouponForm> getCoupons() {
		if (StringUtils.isNotBlank(this.getCouponsJson())) {
			return JSON.parseArray(this.getCouponsJson(), CouponForm.class);
		}
		return coupons;
	}

}
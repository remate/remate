package com.vdlm.web.order.form;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 提交订单Form表单逻辑 扩展下单Form，新增支付方式
 * 
 * @author odin
 */
public class OrderSumbitForm extends OrderForm {

    private static final String MOBILE_MESSAGE = "{valid.mobile.message}";

    private String orderId;
    
    private String shopId;
    /**
     * 购买购物车中的单一项目
     */
    @NotNull
    private List<String> skuIds;
    
    private String skuId;
    
	/**
     *  直接下单，购买数量
     */
    private int qty;
   
	/**
     * 买家留言
     */
    private String remark;
//    /**
//     * 支付方式
//     */
//    @NotNull(message = "{payType.notBlank.message}")
//    private PaymentMode payType;

//    /**
//     * 协议支付号
//     */
//    private String payAgreementId;

    /**
     * 收货人
     */
//    @NotBlank(message = "{consignee.notBlank.message}")
    private String consignee;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1(3\\d|47|5([0-3]|[5-9])|8(0|2|[5-9]))\\d{8}$", message = MOBILE_MESSAGE)
    private String phone;

    /**
     * 地区数字
     */
//    @NotBlank(message = "{zoneId.notBlank.message}")
    private String zoneId;

    /**
     * 详细地址
     */
//    @NotBlank(message = "{street.notBlank.message}")
    private String street;

    /**
     * 微信号
     */
    private String weixinId;

    /**
     * 地址id
     */
    private String addressId;
//
//    /**
//     * 优惠活动id
//     */
//    private String promotionId;

    /**
     * 使用的优惠券
     */
    private String couponId;

    /**
     * 是否是担保交易
     */
    private Boolean danbao;

//    private String bankCode;
//    private String cardType;
//    private String bankName;
    
    private String hongbaoId="";
    private String hongbaoAmount="0";
    private String hongbaoName="";

    public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

//    public String getPromotionId() {
//        return promotionId;
//    }
//
//    public void setPromotionId(String promotionId) {
//        this.promotionId = promotionId;
//    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Boolean isDanbao() {
        return danbao;
    }

    public void setDanbao(Boolean danbao) {
        this.danbao = danbao;
    }

	public String getHongbaoId() {
		return hongbaoId;
	}

	public void setHongbaoId(String hongbaoId) {
		this.hongbaoId = hongbaoId;
	}

	public String getHongbaoAmount() {
		return hongbaoAmount;
	}

	public void setHongbaoAmount(String hongbaoAmount) {
		this.hongbaoAmount = hongbaoAmount;
	}

	public String getHongbaoName() {
		return hongbaoName;
	}

	public void setHongbaoName(String hongbaoName) {
		this.hongbaoName = hongbaoName;
	}

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }
    
    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    
    public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	/**
     * 直接下单，购买数量
     */
	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
}
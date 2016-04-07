package com.vdlm.web.order.form;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;

/**
 * 提交订单Form表单逻辑 扩展下单Form，新增支付方式
 * 
 * @author odin
 */
public class XqOrderSumbitForm {
 
    @NotNull
    private String addressId;

    private List<String> skuIds;
    
    private Map<String, String> remarks;

    private List<Coupon> coupon;
    
    @NotNull
    private PaymentMode payType;

    private String payAgreementId;
    
	private String bankCode;
	 
	private PaymentChannel cardType;
	
	private int qty;//直接下单购买数量
    
//    private List<String> couponId;
//
//    private String hongbaoId="";
//
//    private String hongbaoAmount="0";
//    
//    private String hongbaoName="";

    public List<Coupon> getCoupon() {
		return coupon;
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

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public PaymentChannel getCardType() {
		return cardType;
	}

	public void setCardType(PaymentChannel cardType) {
		this.cardType = cardType;
	}

	public void setCoupon(List<Coupon> coupon) {
		this.coupon = coupon;
	}

	public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public List<String> getSkuIds() {
        return skuIds;
    }

    public void setSkuIds(List<String> skuIds) {
        this.skuIds = skuIds;
    }

    public Map<String, String> getRemarks() {
        return remarks;
    }

    public void setRemarks(Map<String, String> remarks) {
        this.remarks = remarks;
    }

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

//    public List<String> getCouponId() {
//        return couponId;
//    }
//
//    public void setCouponId(List<String> couponId) {
//        this.couponId = couponId;
//    }
//
//    public String getHongbaoId() {
//        return hongbaoId;
//    }
//
//    public void setHongbaoId(String hongbaoId) {
//        this.hongbaoId = hongbaoId;
//    }
//
//    public String getHongbaoAmount() {
//        return hongbaoAmount;
//    }
//
//    public void setHongbaoAmount(String hongbaoAmount) {
//        this.hongbaoAmount = hongbaoAmount;
//    }
//
//    public String getHongbaoName() {
//        return hongbaoName;
//    }
//
//    public void setHongbaoName(String hongbaoName) {
//        this.hongbaoName = hongbaoName;
//    }

}
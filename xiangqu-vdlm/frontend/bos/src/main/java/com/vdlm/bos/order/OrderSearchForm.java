package com.vdlm.bos.order;

import java.math.BigDecimal;

import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.PayStatus;
import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.type.OrderType;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.type.RefundType;
import com.vdlm.dal.type.UserPartnerType;

public class OrderSearchForm {
	private String orderNo_kwd;

	private String buyerPhone_kwd;

	private String sellerPhone_kwd;

	private String shopName_kwd;

	private String logisticsOrderNo_kwd;

	private OrderType type_kwd;

	private PaymentMode payType_kwd;

	private PayStatus paidStatus_kwd;
	
	private RefundType refundType_kwd;

	private OrderStatus[] status_kwd;

	private LogisticsCompany logisticsCompany_kwd;

	private String startTime = "";

	private String endTime = "";

	private String partner_kwd;

	private String dateTypeKwd;
	
	private String startDateKwd = "";

	private String endDateKwd = "";

	private BigDecimal minimum_fee;
	
	private String fee_operator;
	
	private String refundFee_operator;
	
	private BigDecimal refund_fee;
	
	private String shipped_status;
	
	public String getOrderNo_kwd() {
		return orderNo_kwd;
	}

	public void setOrderNo_kwd(String orderNo_kwd) {
		this.orderNo_kwd = orderNo_kwd;
	}

	public String getBuyerPhone_kwd() {
		return buyerPhone_kwd;
	}

	public void setBuyerPhone_kwd(String buyerPhone_kwd) {
		this.buyerPhone_kwd = buyerPhone_kwd;
	}

	public String getSellerPhone_kwd() {
		return sellerPhone_kwd;
	}

	public void setSellerPhone_kwd(String sellerPhone_kwd) {
		this.sellerPhone_kwd = sellerPhone_kwd;
	}

	public String getShopName_kwd() {
		return shopName_kwd;
	}

	public void setShopName_kwd(String shopName_kwd) {
		this.shopName_kwd = shopName_kwd;
	}

	public String getLogisticsOrderNo_kwd() {
		return logisticsOrderNo_kwd;
	}

	public void setLogisticsOrderNo_kwd(String logisticsOrderNo_kwd) {
		this.logisticsOrderNo_kwd = logisticsOrderNo_kwd;
	}

	public OrderType getType_kwd() {
		return type_kwd;
	}

	public void setType_kwd(OrderType type_kwd) {
		this.type_kwd = type_kwd;
	}

	public PaymentMode getPayType_kwd() {
		return payType_kwd;
	}

	public void setPayType_kwd(PaymentMode payType_kwd) {
		this.payType_kwd = payType_kwd;
	}

	public PayStatus getPaidStatus_kwd() {
		return paidStatus_kwd;
	}

	public void setPaidStatus_kwd(PayStatus paidStatus_kwd) {
		this.paidStatus_kwd = paidStatus_kwd;
	}

	public OrderStatus[] getStatus_kwd() {
		return status_kwd;
	}

	public void setStatus_kwd(OrderStatus[] status_kwd) {
		this.status_kwd = status_kwd;
	}

	public LogisticsCompany getLogisticsCompany_kwd() {
		return logisticsCompany_kwd;
	}

	public void setLogisticsCompany_kwd(LogisticsCompany logisticsCompany_kwd) {
		this.logisticsCompany_kwd = logisticsCompany_kwd;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPartner_kwd() {
		return partner_kwd;
	}

	public void setPartner_kwd(String partner_kwd) {
		this.partner_kwd = partner_kwd;
	}

	public String getDateTypeKwd() {
		return dateTypeKwd;
	}

	public void setDateTypeKwd(String dateTypeKwd) {
		this.dateTypeKwd = dateTypeKwd;
	}

	public String getStartDateKwd() {
		return startDateKwd;
	}

	public void setStartDateKwd(String startDateKwd) {
		this.startDateKwd = startDateKwd;
	}

	public String getEndDateKwd() {
		return endDateKwd;
	}

	public void setEndDateKwd(String endDateKwd) {
		this.endDateKwd = endDateKwd;
	}

	public BigDecimal getMinimum_fee() {
		return minimum_fee;
	}

	public void setMinimum_fee(BigDecimal minimum_fee) {
		this.minimum_fee = minimum_fee;
	}

	public String getFee_operator() {
		return fee_operator;
	}

	public void setFee_operator(String fee_operator) {
		this.fee_operator = fee_operator;
	}

	public String getRefundFee_operator() {
		return refundFee_operator;
	}

	public void setRefundFee_operator(String refundFee_operator) {
		this.refundFee_operator = refundFee_operator;
	}

	public BigDecimal getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(BigDecimal refund_fee) {
		this.refund_fee = refund_fee;
	}

	public RefundType getRefundType_kwd() {
		return refundType_kwd;
	}

	public void setRefundType_kwd(RefundType refundType_kwd) {
		this.refundType_kwd = refundType_kwd;
	}

	public String getShipped_status() {
		return shipped_status;
	}

	public void setShipped_status(String shipped_status) {
		this.shipped_status = shipped_status;
	}

}

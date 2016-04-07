package com.vdlm.web.order;

public class OrderRefundShipForm {

	String id;
	String logisticsCompany;	//物流公司
    String logisticsNo;	//物流编号
	String logisticsMemo;  //说明
	private String orderId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLogisticsCompany() {
		return logisticsCompany;
	}
	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}
	public String getLogisticsNo() {
		return logisticsNo;
	}
	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}
	public String getLogisticsMemo() {
		return logisticsMemo;
	}
	public void setLogisticsMemo(String logisticsMemo) {
		this.logisticsMemo = logisticsMemo;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
}

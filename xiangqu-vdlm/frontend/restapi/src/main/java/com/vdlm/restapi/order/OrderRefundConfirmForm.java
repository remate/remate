package com.vdlm.restapi.order;

import java.util.List;

public class OrderRefundConfirmForm {
	String id;	//退款申请ID
	String agree;	//	1-同意；0-不同意
	String returnAddress; // 退货地址
	String returnPhone;	// 退款人电话
	String returnName;	// 退款人名称
	String returnMemo; // 退货备注
	
	String refuseReason; // 拒绝原因
	String refuseDetail; // 拒绝详情
	List<String> refuseEvidences; //拒绝凭证, 图片链接地址
	String requestData;	//服务端下发数据结构，客户端原样上传
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgree() {
		return agree;
	}
	public void setAgree(String agree) {
		this.agree = agree;
	}
	public String getReturnAddress() {
		return returnAddress;
	}
	public void setReturnAddress(String returnAddress) {
		this.returnAddress = returnAddress;
	}
	
	public String getReturnPhone() {
		return returnPhone;
	}
	public void setReturnPhone(String returnPhone) {
		this.returnPhone = returnPhone;
	}
	public String getReturnName() {
		return returnName;
	}
	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}
	public String getReturnMemo() {
		return returnMemo;
	}
	public void setReturnMemo(String returnMemo) {
		this.returnMemo = returnMemo;
	}
	public String getRefuseReason() {
		return refuseReason;
	}
	public void setRefuseReason(String refuseReason) {
		this.refuseReason = refuseReason;
	}
	public String getRefuseDetail() {
		return refuseDetail;
	}
	public void setRefuseDetail(String refuseDetail) {
		this.refuseDetail = refuseDetail;
	}
	public List<String> getRefuseEvidences() {
		return refuseEvidences;
	}
	public void setRefuseEvidences(List<String> refuseEvidences) {
		this.refuseEvidences = refuseEvidences;
	}
	public String getRequestData() {
		return requestData;
	}
	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
	@Override
	public String toString() {
		return "OrderRefundConfirmForm [id=" + id + ", agree=" + agree
				+ ", returnAddress=" + returnAddress + ", returnPhone="
				+ returnPhone + ", returnName=" + returnName + ", returnMemo="
				+ returnMemo + ", refuseReason=" + refuseReason
				+ ", refuseDetail=" + refuseDetail + ", refuseEvidences="
				+ refuseEvidences + ", requestData=" + requestData + "]";
	}

	
}

package com.vdlm.restapi.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdlm.dal.util.json.JsonResourceUrlSerializer;

public class OrderMsgVO {
	 //订单ID
    private String orderId;
    //商品名称
    private String title;
    //商品图片
    @JsonSerialize(using = JsonResourceUrlSerializer.class)
    private String imgUrl;
    //留言时间
    private long msgTime;
    //买家留言
    private String buyerContent;
    //卖家留言
    private String sellerContent;
    //商品id
    private String productId;
    
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public long getMsgTime() {
		return msgTime;
	}
	public void setMsgTime(long msgTime) {
		this.msgTime = msgTime;
	}
	public String getBuyerContent() {
		return buyerContent;
	}
	public void setBuyerContent(String buyerContent) {
		this.buyerContent = buyerContent;
	}
	public String getSellerContent() {
		return sellerContent;
	}
	public void setSellerContent(String sellerContent) {
		this.sellerContent = sellerContent;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
    
    
}

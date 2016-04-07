package com.vdlm.web.im;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;

public class ImProductVO {
		private String id;//商品ID
		private String buyerId;
		private String sellerId;
		private String avatar;
		private String nickName;
		private String name;//商品名称 
		private String productName;//商品名称 
		private String productImgUrl;//商品图片地址
		private String imgUrl;//商品图片地址
		private String description;//商品详情
		private Integer sales;//销量
		private BigDecimal price;//价格
		private Integer amount;//库存
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getSales() {
			return sales;
		}
		public void setSales(int sales) {
			this.sales = sales;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
		public String getProductUrl() {
			return productUrl;
		}
		public void setProductUrl(String productUrl) {
			this.productUrl = productUrl;
		}
		public String getBuyerId() {
			return buyerId;
		}
		public void setBuyerId(String buyerId) {
			this.buyerId = buyerId;
		}
		public String getSellerId() {
			return sellerId;
		}
		public void setSellerId(String sellerId) {
			this.sellerId = sellerId;
		}
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductImgUrl() {
			return productImgUrl;
		}
		public void setProductImgUrl(String productImgUrl) {
			this.productImgUrl = productImgUrl;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getImgUrl() {
			return imgUrl;
		}
		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}
		private String productUrl;//商品预览地址
		
}

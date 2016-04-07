package com.vdlm.restapi.product;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.vdlm.dal.model.Sku;
import com.vdlm.dal.status.ProductStatus;

public class ProdutSaveForm {
	private String id;
	private String name;
	private String userId;// 我们的混淆 id
	private String extUid; // 第三方用户 id
	private String shopId;
	private BigDecimal price; //最低价格
	private Integer amount;
	private ProductStatus status;
	private String description;
	private String img ;
	private Sku[] skus;
	private BigInteger partnerProductId; //第三方同步id
	private Boolean enableDesc;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public ProductStatus getStatus() {
		return status;
	}
	public void setStatus(ProductStatus status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Sku[] getSkus() {
		return skus;
	}
	public void setSkus(Sku[] skus) {
		this.skus = skus;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getImg() {
		if(img == null)
			return "qn|xaya|Fj7ACPGnVV_LFyFDKAWd_65eQuFh";
		else
			return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public BigInteger getPartnerProductId() {
		return partnerProductId;
	}
	public void setPartnerProductId(BigInteger partnerProductId) {
		this.partnerProductId = partnerProductId;
	}
	public String getExtUid() {
		return extUid;
	}
	public void setExtUid(String extUid) {
		this.extUid = extUid;
	}
	public Boolean getEnableDesc() {
		return enableDesc;
	}
	public void setEnableDesc(Boolean enableDesc) {
		this.enableDesc = enableDesc;
	}

}

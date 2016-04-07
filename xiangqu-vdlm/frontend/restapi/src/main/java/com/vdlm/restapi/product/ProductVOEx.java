package com.vdlm.restapi.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vdlm.dal.util.json.JsonResourceUrlSerializer;
import com.vdlm.service.product.vo.ProductVO;

public class ProductVOEx extends ProductVO {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = JsonResourceUrlSerializer.class)
	private String imgUrl;
	
	private String productUrl;
	
	private String activityName;

	public ProductVOEx(ProductVO product, String productUrl, String imgUrl, String activityName) {
		super(product);
		this.setProductUrl(productUrl);
		this.setImgUrl(imgUrl);
		this.setActivityName(activityName);
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	
	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

}

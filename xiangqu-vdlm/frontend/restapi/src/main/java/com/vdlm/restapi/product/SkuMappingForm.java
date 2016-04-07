package com.vdlm.restapi.product;

import javax.validation.constraints.Size;

public class SkuMappingForm {
	
	private String id;
	
    private String productId;

    @Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
    private String specKey;

    @Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
    private String specName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSpecKey() {
		return specKey;
	}

	public void setSpecKey(String specKey) {
		this.specKey = specKey;
	}

	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

}

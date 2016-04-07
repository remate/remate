package com.vdlm.restapi.fragment;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ProductFragmentForm {

	@NotNull(message = "{valid.notBlank.message}")
	private String productId;
	
	private List<String> fragmentIds;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public List<String> getFragmentIds() {
		return fragmentIds;
	}

	public void setFragmentIds(List<String> fragmentIds) {
		this.fragmentIds = fragmentIds;
	}

}

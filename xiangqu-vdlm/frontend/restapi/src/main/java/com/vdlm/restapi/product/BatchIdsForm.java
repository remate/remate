package com.vdlm.restapi.product;

import java.util.List;

import javax.validation.constraints.NotNull;

public class BatchIdsForm {
	
	@NotNull(message = "{valid.notBlank.message}")
	private List<String> ids;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}		
	
	
}

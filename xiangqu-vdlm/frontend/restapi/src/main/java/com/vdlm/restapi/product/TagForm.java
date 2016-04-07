package com.vdlm.restapi.product;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TagForm {
	String id;
	
	@NotNull(message = "{valid.tag.notBlank.message}")
	@Size(min = 0, max = 20, message = "{valid.product.tag.message}")
	String tag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}

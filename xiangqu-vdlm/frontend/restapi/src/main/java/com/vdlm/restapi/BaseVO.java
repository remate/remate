package com.vdlm.restapi;

import com.vdlm.dal.BaseEntity;

@SuppressWarnings("serial")
public abstract class BaseVO implements BaseEntity {

	private String id;

	public BaseVO(BaseEntity e) {
		if (e == null)
			return;
		this.id = e.getId();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

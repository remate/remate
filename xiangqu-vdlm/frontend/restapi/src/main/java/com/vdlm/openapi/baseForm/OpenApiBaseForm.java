package com.vdlm.openapi.baseForm;

import javax.validation.constraints.NotNull;

public class OpenApiBaseForm {

	private String extUid;
	@NotNull
	private String domain;

	public String getExtUid() {
		return extUid;
	}

	public void setExtUid(String extUid) {
		this.extUid = extUid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}

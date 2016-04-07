package com.vdlm.spider.wdetail;

/**
 *
 * @author: chenxi
 */

public class ComboInfo {

	private AsynUrl asynUrl;
	private H5Url h5Url;
	
	public AsynUrl getAsynUrl() {
		return asynUrl;
	}

	public void setAsynUrl(AsynUrl asynUrl) {
		this.asynUrl = asynUrl;
	}

	public H5Url getH5Url() {
		return h5Url;
	}

	public void setH5Url(H5Url h5Url) {
		this.h5Url = h5Url;
	}

	static class AsynUrl {
		
		private String name;
		private String value;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	static class H5Url {
		
		private String name;
		private String value;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
	}
}

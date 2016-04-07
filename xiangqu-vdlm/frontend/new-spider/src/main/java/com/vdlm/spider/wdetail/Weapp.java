package com.vdlm.spider.wdetail;

/**
 *
 * @author: chenxi
 */

public class Weapp {

	private String identifier;
	private MtopModel mtopModel;
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public MtopModel getMtopModel() {
		return mtopModel;
	}

	public void setMtopModel(MtopModel mtopModel) {
		this.mtopModel = mtopModel;
	}

	static class MtopModel {
		private String API_NAME;
		private String VERSION;
		private Params params;
		private boolean needLogin;
		private boolean needEcode;
		
		public String getAPI_NAME() {
			return API_NAME;
		}
		
		public void setAPI_NAME(String aPI_NAME) {
			API_NAME = aPI_NAME;
		}
		
		public String getVERSION() {
			return VERSION;
		}
		
		public void setVERSION(String vERSION) {
			VERSION = vERSION;
		}
		
		public Params getParams() {
			return params;
		}
		
		public void setParams(Params params) {
			this.params = params;
		}
		
		public boolean isNeedLogin() {
			return needLogin;
		}
		
		public void setNeedLogin(boolean needLogin) {
			this.needLogin = needLogin;
		}
		
		public boolean isNeedEcode() {
			return needEcode;
		}
		
		public void setNeedEcode(boolean needEcode) {
			this.needEcode = needEcode;
		}
		
	}
	
	static class Params {
		private String itemId;
		private String sellerId;
		private String jumpUrl;
		
		public String getItemId() {
			return itemId;
		}
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		
		public String getSellerId() {
			return sellerId;
		}
		
		public void setSellerId(String sellerId) {
			this.sellerId = sellerId;
		}
		
		public String getJumpUrl() {
			return jumpUrl;
		}
		
		public void setJumpUrl(String jumpUrl) {
			this.jumpUrl = jumpUrl;
		}
		
	}
}

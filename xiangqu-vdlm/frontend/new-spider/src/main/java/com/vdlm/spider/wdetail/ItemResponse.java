package com.vdlm.spider.wdetail;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class ItemResponse {

	private final static String SUCCESS_RET_HEADER = "SUCCESS::";
	
	private String api;
	private String v;
	private List<String> ret;
	private Data data;
	
	public String getApi() {
		return api;
	}
	
	public void setApi(String api) {
		this.api = api;
	}
	
	public String getV() {
		return v;
	}
	
	public void setV(String v) {
		this.v = v;
	}
	
	public List<String> getRet() {
		return ret;
	}
	
	public void setRet(List<String> ret) {
		this.ret = ret;
	}
	
	public Data getData() {
		return data;
	}
	
	public void setData(Data data) {
		this.data = data;
	}
	
	public boolean isSuccess() {
		if (ret == null || ret.isEmpty()) {
			return false;
		}
		final String result = ret.get(0);
		if (result.startsWith(SUCCESS_RET_HEADER)) {
			return true;
		}
		return false;
	}
}

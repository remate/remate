package com.vdlm.web.vo;

/**
 * 用户后台向前台返回的JSON对象
 */
public class Json implements java.io.Serializable {

	private static final long serialVersionUID = -2073084046088942499L;

	public final static int RC_SUCCESS = 1; // 返回成功结果
	public final static int RC_FAILURE = 0; // 返回失败结果
	private int rc = RC_SUCCESS;
	private String msg = "";
	private Object obj = null;
	
	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}

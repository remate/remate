package com.vdlm.spider.bean;

/**
 *
 * @author: chenxi
 */

public class WItemTaskBean extends ItemTaskBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1771515839887364946L;

	// hard code for the moment
	public static final String WDETAIL_PREFIX = "http://hws.m.taobao.com/cache/wdetail/5.0?id=";

	@Override
	public String getTaskName() {
		return "witem";
	}
}

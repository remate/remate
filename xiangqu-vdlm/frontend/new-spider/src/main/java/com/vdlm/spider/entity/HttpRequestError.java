package com.vdlm.spider.entity;

import java.util.Date;

/**
 *
 * @author: chenxi
 */

public class HttpRequestError {

	private Long id;
	private String url;
	private int statusCode;
	private Date createAt;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	@Override
	public String toString() {
		return "HttpRequestError [id=" + id + ", url=" + url + ", statusCode="
				+ statusCode + ", createAt=" + createAt + "]";
	}
	
}

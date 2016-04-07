package com.vdlm.spider.proxy;

import java.util.Date;

/**
 *
 * @author: chenxi
 */

public class Proxy {

	private String ip;
	private int port;
	private String type;
	private ProxyStatus status = ProxyStatus.NEW;
	private Date checkTime;
	private boolean checkTime2Current = false;
	private String source;
	
	private int currentPage = 0;
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public ProxyStatus getStatus() {
		return status;
	}

	public Date getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public void setStatus(ProxyStatus status) {
		this.status = status;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isCheckTime2Current() {
		return checkTime2Current;
	}

	public void setCheckTime2Current(boolean checkTime2Current) {
		this.checkTime2Current = checkTime2Current;
	}

	@Override
	public String toString() {
		return "Proxy [ip=" + ip + ", port=" + port + ", type=" + type
				+ ", status=" + status + ", checkTime=" + checkTime
				+ ", source=" + source + ", currentPage=" + currentPage + "]";
	}
	
}

package com.vdlm.restapi.product;

import java.util.Date;
import java.util.List;

import com.vdlm.dal.model.Product;

public class ForsaleListVO {
	Date date;
	String serverTime;
	List<Product> list;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	public List<Product> getList() {
		return list;
	}
	public void setList(List<Product> list) {
		this.list = list;
	}
	

}

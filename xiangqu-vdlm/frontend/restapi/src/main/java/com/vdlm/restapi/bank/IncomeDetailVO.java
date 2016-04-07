package com.vdlm.restapi.bank;

import java.math.BigDecimal;
import java.util.Date;

public class IncomeDetailVO {
	private String status; 
	private String msg; 
	private Date date;
	private BigDecimal money;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	
	
}

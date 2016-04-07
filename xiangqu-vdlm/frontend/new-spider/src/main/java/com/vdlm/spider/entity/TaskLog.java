/**
 * 
 */
package com.vdlm.spider.entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;

/**
 * @author xushixiang
 */
public class TaskLog implements Serializable {
 
	private static final long serialVersionUID = 6891566154560622836L;
	private long id;
	private long taskId; 
	private String ouer_user_id;
	private String ouer_shop_id;
	private ReqFrom req_from; 
	private ShopType shopType; 
	private Integer status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public String getOuer_user_id() {
		return ouer_user_id;
	}
	public void setOuer_user_id(String ouer_user_id) {
		this.ouer_user_id = ouer_user_id;
	}
	public String getOuer_shop_id() {
		return ouer_shop_id;
	}
	public void setOuer_shop_id(String ouer_shop_id) {
		this.ouer_shop_id = ouer_shop_id;
	}
	public ReqFrom getReq_from() {
		return req_from;
	}
	public void setReq_from(ReqFrom req_from) {
		this.req_from = req_from;
	}
	public ShopType getShopType() {
		return shopType;
	}
	public void setShopType(ShopType shopType) {
		this.shopType = shopType;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
 
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}

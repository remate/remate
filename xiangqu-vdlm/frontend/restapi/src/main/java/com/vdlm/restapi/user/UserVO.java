package com.vdlm.restapi.user;

import org.springframework.util.StringUtils;

import com.vdlm.dal.model.User;
import com.vdlm.restapi.BaseVO;

public class UserVO extends BaseVO {

	private static final long serialVersionUID = 1L;
	
	private String phone;

	private String name;

	private String avatar;

	/**
	 * 为空则表示未创建店铺
	 */
	private String shopUrl;

	private boolean hasPwd;
	
	private String idCardNum;

	public UserVO() {
		super(null);
	}

	public UserVO(User u, String shopUrl) {
		super(u);
		name = u.getName();
		phone = u.getPhone();
		avatar = u.getAvatar();
		hasPwd = StringUtils.hasLength(u.getPassword());
		idCardNum = u.getIdCardNum();
		this.shopUrl = shopUrl;
	}

	public String getName() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getShopUrl() {
		return shopUrl;
	}

	public boolean isHasPwd() {
		return hasPwd;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdCardNum() {
		return idCardNum;
	}

	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}

}

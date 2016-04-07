package com.vdlm.web.address;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddressForm {
    private String id;
    
    @NotNull(message = "{valid.notBlank.message}")
	private String consignee;
	
    @NotNull(message = "{valid.notBlank.message}")
    private String phone;
    
    @NotNull(message = "{valid.notBlank.message}")
	private String zoneId;
    
    @NotNull(message = "{valid.notBlank.message}")
    @Size(min = 1, max = 100)
	private String street;
    
	private String zipcode;
	
	private String weixinId;

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public String getWeixinId() {
		return weixinId;
	}

	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}

}

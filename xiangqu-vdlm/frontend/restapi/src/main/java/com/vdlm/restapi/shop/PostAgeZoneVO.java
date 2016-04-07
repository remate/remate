package com.vdlm.restapi.shop;

import java.util.List;

import com.vdlm.dal.model.Zone;

public class PostAgeZoneVO {
	
	private String zoneTag;
	
	private List<Zone> zones;

	public String getZoneTag() {
		return zoneTag;
	}

	public void setZoneTag(String zoneTag) {
		this.zoneTag = zoneTag;
	}

	public List<Zone> getZones() {
		return zones;
	}

	public void setZones(List<Zone> zones) {
		this.zones = zones;
	}
	
}

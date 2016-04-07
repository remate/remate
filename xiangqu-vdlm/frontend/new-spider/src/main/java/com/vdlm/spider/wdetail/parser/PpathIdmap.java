package com.vdlm.spider.wdetail.parser;

/**
 *
 * @author: chenxi
 */

public class PpathIdmap {

	private String origSpec;
	private String skuId;
	
	public String getOrigSpec() {
		return origSpec;
	}
	
	public void setOrigSpec(String origSpec) {
		this.origSpec = origSpec;
	}
	
	public String getSkuId() {
		return skuId;
	}
	
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	
//	private String propSize;
//	private String valueSize;
//	private String propColor;
//	private String valueColor;
//	private String value;
//	
//	public String getPropSize() {
//		return propSize;
//	}
//	
//	public void setPropSize(String propSize) {
//		this.propSize = propSize;
//	}
//	
//	public String getValueSize() {
//		return valueSize;
//	}
//	
//	public void setValueSize(String valueSize) {
//		this.valueSize = valueSize;
//	}
//	
//	public String getPropColor() {
//		return propColor;
//	}
//	
//	public void setPropColor(String propColor) {
//		this.propColor = propColor;
//	}
//	
//	public String getValueColor() {
//		return valueColor;
//	}
//	
//	public void setValueColor(String valueColor) {
//		this.valueColor = valueColor;
//	}
//	
//	public String getValue() {
//		return value;
//	}
//	
//	public void setValue(String value) {
//		this.value = value;
//	}
//	
//	public void setKey(String key) {
//		final String[] pair = key.split(";");
//		if (pair.length != 2) {
//			throw new IllegalArgumentException("invalid PpathIdmap key:" + key);
//		}
//		propSize = pair[0].split(":")[0];
//		valueSize = pair[0].split(":")[1];
//		propColor = pair[1].split(":")[0];
//		valueColor = pair[1].split(":")[1];
//	}
//
//	@Override
//	public String toString() {
//		return propSize + ":" + valueSize + ";" + propColor + ":" + valueColor + ":" + value;
//	}
	
	
}

package com.vdlm.spider.entity;

import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import com.vdlm.dal.vo.FragmentVO;

/**
 *
 * @author: chenxi
 */

public class SkuProp {

	private String propId;
	private String propName;
	private List<Value> values;
	
	public String getPropId() {
		return propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}
	
	public String getSkuImg(String valueId) {
		if (StringUtils.isEmpty(valueId)) {
			return null;
		}
		for (final Value value : values) {
			if (valueId.equals(value.getValueId())) {
				if (value.getImgUrl() != null) {
					return null;
				}
			}
		}
		return null;
	}
	
	public List<String> getTexts() {
		List<String> texts = Lists.newArrayList();
		for (Value value : getValues()) {
			//texts.add(getPropName() + ":" + value.getName());
			texts.add(value.getName());
		}
		return texts;
	}

	public static class Value {
		
		private String valueId;
		private String name;
		private String imgUrl;
		
		public String getValueId() {
			return valueId;
		}
		
		public void setValueId(String valueId) {
			this.valueId = valueId;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}
		
	}
}

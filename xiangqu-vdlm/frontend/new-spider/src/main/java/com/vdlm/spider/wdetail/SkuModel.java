package com.vdlm.spider.wdetail;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.vdlm.spider.entity.SkuProp;
import com.vdlm.spider.entity.SkuProp.Value;

/**
 *
 * @author: chenxi
 */

public class SkuModel {

	private List<SkuProp> skuProps;
	private JSONObject ppathIdmap;

	// FIXME ppathIdmap	
	
	public List<SkuProp> getSkuProps() {
		return skuProps;
	}

	public void setSkuProps(List<SkuProp> skuProps) {
		this.skuProps = skuProps;
	}

	public JSONObject getPpathIdmap() {
		return ppathIdmap;
	}

	public void setPpathIdmap(JSONObject ppathIdmap) {
		this.ppathIdmap = ppathIdmap;
	}
	
	public Set<String> getSkuImgs() {
		final Set<String> skuImgs = Sets.newHashSet();
		List<Value> values;
		for (final SkuProp skuProp : skuProps) {
			values = skuProp.getValues();
			for (final Value value : values) {
				if (!StringUtils.isEmpty(value.getImgUrl())) {
					skuImgs.add(value.getImgUrl());
				}
			}
		}
		return skuImgs;
	}
}

package com.vdlm.restapi.product;

import java.util.List;

import com.vdlm.dal.model.Sku;
import com.vdlm.service.product.vo.SkuMappingVO;

public class SkuWithMappingVO {
	List<Sku> skus;
	List<SkuMappingVO> skuMappings;
	
	public List<Sku> getSkus() {
		return skus;
	}
	public void setSkus(List<Sku> skus) {
		this.skus = skus;
	}
	public List<SkuMappingVO> getSkuMappings() {
		return skuMappings;
	}
	public void setSkuMappings(List<SkuMappingVO> skuMappings) {
		this.skuMappings = skuMappings;
	}
	
}

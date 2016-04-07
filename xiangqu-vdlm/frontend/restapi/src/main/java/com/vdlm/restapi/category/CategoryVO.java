package com.vdlm.restapi.category;

import org.springframework.beans.BeanUtils;

import com.vdlm.dal.model.Category;

public class CategoryVO extends Category {
    
	private long productCount;

	public CategoryVO() {
	}

	public CategoryVO(Category cat) {
        BeanUtils.copyProperties(cat, this);
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
    
}

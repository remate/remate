package com.vdlm.restapi.category;

import java.util.List;

import javax.validation.constraints.NotNull;

public class AddOrRemoveProductForm {
    @NotNull
    private String categoryId;
    @NotNull
    private List<String> productIds;
    
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public List<String> getProductIds() {
        return productIds;
    }
    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
    
}

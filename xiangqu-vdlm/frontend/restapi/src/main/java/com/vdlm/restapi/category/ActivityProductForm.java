package com.vdlm.restapi.category;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.vdlm.dal.model.CampaignProduct;

public class ActivityProductForm {
    
    @NotNull
    private String id;
    
    @NotEmpty
    private List<CampaignProduct> products;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<CampaignProduct> getProducts() {
        return products;
    }
    public void setProducts(List<CampaignProduct> products) {
        this.products = products;
    }
    
}

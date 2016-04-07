package com.vdlm.restapi.shop;

import java.util.List;

import javax.validation.constraints.NotNull;

public class CategoryIdxUpdateForm {
    @NotNull
    private List<String> categoryIds;
    private List<Integer> idxs;
    public List<String> getCategoryIds() {
        return categoryIds;
    }
    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }
    public List<Integer> getIdxs() {
        return idxs;
    }
    public void setIdxs(List<Integer> idxs) {
        this.idxs = idxs;
    } 
}

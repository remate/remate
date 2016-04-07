package com.vdlm.restapi.category;

import java.util.List;

import com.vdlm.dal.model.Category;

public class CategoryForm {

	private List<Category> categorys;

	public List<Category> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
	}

}
package com.vdlm.restapi.union;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.vdlm.restapi.product.SkuForm;

public class UnionSaveProductForm {

	private String id;

	@Size(min = 0, max = 60, message = "{valid.product.name.message}")
	private String name;

	@NotNull(message = "{valid.notBlank.message}")
	private int recommend;

	@NotBlank(message = "{valid.notBlank.message}")
	private String description;

	@Valid
	private List<SkuForm> skus;
	private List<String> imgs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SkuForm> getSkus() {
		return skus;
	}

	public void setSkus(List<SkuForm> skus) {
		this.skus = skus;
	}

	public List<String> getImgs() {
		return imgs;
	}

	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}
}

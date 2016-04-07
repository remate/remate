package com.vdlm.restapi.product;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.vdlm.dal.status.ProductStatus;

public class ProductForm {

	private String id;

	@Size(min = 0, max = 60, message = "{valid.product.name.message}")
	private String name;
	
	private Boolean enableDesc;

	// @Size(min = 0, max = 20)
	@NotNull(message = "{valid.notBlank.message}")
	private List<String> imgs;

	@NotNull(message = "{valid.notBlank.message}")
	private Integer recommend;

	/*
	 * @NotBlank(message = "{valid.notBlank.message}")
	 * 
	 * @Size(min = 0, max = 200) private String descImg;
	 */
	@NotNull(message = "{valid.notBlank.message}")
	private ProductStatus status;

	@NotBlank(message = "{valid.notBlank.message}")
	private String description;

	private Long forsaleDate;

	@Valid
	private List<SkuForm> skus;

	@Valid
	private List<SkuMappingForm> skuMappings;
	
	@Valid
	private List<TagForm> tags;
	// 域级别商品类别id字段 （可以不填）
	private String cat_id;
	
	// 商品类别id字段 （可以不填）
	private String category;

	// 是否延迟发货 （可以不填，默认为0）
	private Integer delayed = 0;

	// 延迟发货时间（天） （delayed = 0的时候才有效）
	private Integer delayAt = 0;

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}


	public List<SkuMappingForm> getSkuMappings() {
		return skuMappings;
	}

	public void setSkuMappings(List<SkuMappingForm> skuMappings) {
		this.skuMappings = skuMappings;
	}

	public List<SkuForm> getSkus() {
		return skus;
	}

	public void setSkus(List<SkuForm> skus) {
		this.skus = skus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TagForm> getTags() {
		return tags;
	}

	public void setTags(List<TagForm> tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getRecommend() {
		return recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	public List<String> getImgs() {
		return imgs;
	}

	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}

	/*
	 * public String getDescImg() { return descImg; }
	 * 
	 * public void setDescImg(String descImg) { this.descImg = descImg; }
	 */

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getForsaleDate() {
		return forsaleDate;
	}

	public void setForsaleDate(Long forsaleDate) {
		this.forsaleDate = forsaleDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getDelayed() {
		return delayed;
	}

	public void setDelayed(Integer delayed) {
		this.delayed = delayed;
	}

	public Integer getDelayAt() {
		return delayAt;
	}

	public void setDelayAt(Integer delayAt) {
		this.delayAt = delayAt;
	}

	public Boolean getEnableDesc() {
		return enableDesc;
	}

	public void setEnableDesc(Boolean enableDesc) {
		this.enableDesc = enableDesc;
	}

	public String getCat_id() {
		return cat_id;
	}

	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}
}

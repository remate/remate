package com.vdlm.restapi.product;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

public class SkuForm {
	
	private String id;

	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec;
	
	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec1;
	
	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec2;
	
	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec3;
	
	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec4;
	
	@Size(min = 0, max = 100, message = "{valid.sku.spec.message}")
	private String spec5;

	@NotNull(message = "{valid.tag.notBlank.message}")
	@Digits(integer=10, fraction=2, message = "{valid.sku.price.message}")
	private BigDecimal price;
	
	@NotNull(message = "{valid.notBlank.message}")
	@Range(min=0, max=99999999, message = "{valid.sku.amount.message}")
	private Integer amount;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getSpec1() {
		return spec1;
	}

	public void setSpec1(String spec1) {
		this.spec1 = spec1;
	}

	public String getSpec2() {
		return spec2;
	}

	public void setSpec2(String spec2) {
		this.spec2 = spec2;
	}

	public String getSpec3() {
		return spec3;
	}

	public void setSpec3(String spec3) {
		this.spec3 = spec3;
	}

	public String getSpec4() {
		return spec4;
	}

	public void setSpec4(String spec4) {
		this.spec4 = spec4;
	}

	public String getSpec5() {
		return spec5;
	}

	public void setSpec5(String spec5) {
		this.spec5 = spec5;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}
}

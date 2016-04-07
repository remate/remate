package com.vdlm.bos.product;

import com.vdlm.dal.status.ProductStatus;

public class ProductSearchForm {
	private String product_name_kwd;
	private String shop_name_kwd;
	private ProductStatus product_status_kwd;
	private Boolean archive_kwd;
	
	private Boolean contain_closed_shop_kwd = false;
	private String created1_kwd;
	private String created2_kwd;
	private String updated1_kwd;
	private String updated2_kwd;
	
	public String getProduct_name_kwd() {
		return product_name_kwd;
	}
	public void setProduct_name_kwd(String product_name_kwd) {
		this.product_name_kwd = product_name_kwd;
	}
	public String getShop_name_kwd() {
		return shop_name_kwd;
	}
	public void setShop_name_kwd(String shop_name_kwd) {
		this.shop_name_kwd = shop_name_kwd;
	}
	public ProductStatus getProduct_status_kwd() {
		return product_status_kwd;
	}
	public void setProduct_status_kwd(ProductStatus product_status_kwd) {
		this.product_status_kwd = product_status_kwd;
	}
	public Boolean getArchive_kwd() {
		return archive_kwd;
	}
	public void setArchive_kwd(Boolean archive_kwd) {
		this.archive_kwd = archive_kwd;
	}
	public Boolean getContain_closed_shop_kwd() {
		return contain_closed_shop_kwd;
	}
	public void setContain_closed_shop_kwd(Boolean contain_closed_shop_kwd) {
		this.contain_closed_shop_kwd = contain_closed_shop_kwd;
	}
	public String getCreated1_kwd(){
		return created1_kwd;
	}
	public void setCreated1_kwd(String created1_kwd){
		this.created1_kwd = created1_kwd;
	}
	public String getCreated2_kwd(){
		return created2_kwd;
	}
	public void setCreated2_kwd(String created2_kwd){
		this.created2_kwd = created2_kwd;
	}
	public String getUpdated1_kwd(){
		return updated1_kwd;
	}
	public void setUpdated1_kwd(String updated1_kwd){
		this.updated1_kwd = updated1_kwd;
	}
	public String getUpdated2_kwd(){
		return updated2_kwd;
	}
	public void setUpdated2_kwd(String updated2_kwd){
		this.updated2_kwd = updated2_kwd;
	}
}

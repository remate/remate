package com.vdlm.spider.wdetail;

import java.util.List;

/**
 *
 * @author: chenxi
 */

public class Data {

	private List<ApiStack> apiStack;
	private ItemInfoModel itemInfoModel;
	private SkuModel skuModel;
	private Seller seller;
	private GuaranteeInfo guaranteeInfo;
	private List<Prop> props;
	private DescInfo descInfo;
	private RateInfo rateInfo;
	private ComboInfo comboInfo;
	private List<Weapp> weappList;
	private TrackParams trackParams;
	private Extras extras;
	
	public List<ApiStack> getApiStack() {
		return apiStack;
	}
	
	public void setApiStack(List<ApiStack> apiStack) {
		this.apiStack = apiStack;
	}
	
	public ItemInfoModel getItemInfoModel() {
		return itemInfoModel;
	}
	
	public void setItemInfoModel(ItemInfoModel itemInfoModel) {
		this.itemInfoModel = itemInfoModel;
	}
	
	public SkuModel getSkuModel() {
		return skuModel;
	}
	
	public void setSkuModel(SkuModel skuModel) {
		this.skuModel = skuModel;
	}
	
	public Seller getSeller() {
		return seller;
	}
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public GuaranteeInfo getGuaranteeInfo() {
		return guaranteeInfo;
	}
	
	public void setGuaranteeInfo(GuaranteeInfo guaranteeInfo) {
		this.guaranteeInfo = guaranteeInfo;
	}
	
	public List<Prop> getProps() {
		return props;
	}
	
	public void setProps(List<Prop> props) {
		this.props = props;
	}
	
	public DescInfo getDescInfo() {
		return descInfo;
	}
	
	public void setDescInfo(DescInfo descInfo) {
		this.descInfo = descInfo;
	}
	
	public RateInfo getRateInfo() {
		return rateInfo;
	}
	
	public void setRateInfo(RateInfo rateInfo) {
		this.rateInfo = rateInfo;
	}
	
	public ComboInfo getComboInfo() {
		return comboInfo;
	}
	
	public void setComboInfo(ComboInfo comboInfo) {
		this.comboInfo = comboInfo;
	}
	
	public List<Weapp> getWeappList() {
		return weappList;
	}
	
	public void setWeappList(List<Weapp> weappList) {
		this.weappList = weappList;
	}
	
	public TrackParams getTrackParams() {
		return trackParams;
	}
	
	public void setTrackParams(TrackParams trackParams) {
		this.trackParams = trackParams;
	}
	
	public Extras getExtras() {
		return extras;
	}
	
	public void setExtras(Extras extras) {
		this.extras = extras;
	}
	
}

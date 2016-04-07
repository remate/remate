/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:20:50 AM Jul 20, 2014
 */
public class ItemConfig implements Serializable {

	private static final long serialVersionUID = -5295244546236279364L;

	private String remoteUrlStartWith;
	private String remoteStockKeyword;
	private String remotePriceKeyword;
	private String remoteStockKey;
	private String remotePriceKey;

	private String remoteDefaultStockKeyword;
	private String remoteDefaultPriceKeyword;
	private String remoteDefaultStockKey;
	private String remoteDefaultPriceKey;

	private String stockKeyword;
	private String priceKeyword;
	private String stockKey;
	private String priceKey;
	private String skuId;

	private int imgSize;
	private String idName;
	
	private String apiDescKey;
	
	private String nameFilterKey;

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getRemoteDefaultStockKeyword() {
		return remoteDefaultStockKeyword;
	}

	public void setRemoteDefaultStockKeyword(String remoteDefaultStockKeyword) {
		this.remoteDefaultStockKeyword = remoteDefaultStockKeyword;
	}

	public String getRemoteDefaultPriceKeyword() {
		return remoteDefaultPriceKeyword;
	}

	public void setRemoteDefaultPriceKeyword(String remoteDefaultPriceKeyword) {
		this.remoteDefaultPriceKeyword = remoteDefaultPriceKeyword;
	}

	public String getRemoteDefaultStockKey() {
		return remoteDefaultStockKey;
	}

	public void setRemoteDefaultStockKey(String remoteDefaultStockKey) {
		this.remoteDefaultStockKey = remoteDefaultStockKey;
	}

	public String getRemoteDefaultPriceKey() {
		return remoteDefaultPriceKey;
	}

	public void setRemoteDefaultPriceKey(String remoteDefaultPriceKey) {
		this.remoteDefaultPriceKey = remoteDefaultPriceKey;
	}

	public String getStockKeyword() {
		return stockKeyword;
	}

	public void setStockKeyword(String stockKeyword) {
		this.stockKeyword = stockKeyword;
	}

	public String getPriceKeyword() {
		return priceKeyword;
	}

	public void setPriceKeyword(String priceKeyword) {
		this.priceKeyword = priceKeyword;
	}

	public String getStockKey() {
		return stockKey;
	}

	public void setStockKey(String stockKey) {
		this.stockKey = stockKey;
	}

	public String getPriceKey() {
		return priceKey;
	}

	public void setPriceKey(String priceKey) {
		this.priceKey = priceKey;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public int getImgSize() {
		return imgSize;
	}

	public void setImgSize(int imgSize) {
		this.imgSize = imgSize;
	}

	public String getRemoteUrlStartWith() {
		return remoteUrlStartWith;
	}

	public void setRemoteUrlStartWith(String remoteUrlStartWith) {
		this.remoteUrlStartWith = remoteUrlStartWith;
	}

	public String getRemoteStockKeyword() {
		return remoteStockKeyword;
	}

	public void setRemoteStockKeyword(String remoteStockKeyword) {
		this.remoteStockKeyword = remoteStockKeyword;
	}

	public String getRemotePriceKeyword() {
		return remotePriceKeyword;
	}

	public void setRemotePriceKeyword(String remotePriceKeyword) {
		this.remotePriceKeyword = remotePriceKeyword;
	}

	public String getRemoteStockKey() {
		return remoteStockKey;
	}

	public void setRemoteStockKey(String remoteStockKey) {
		this.remoteStockKey = remoteStockKey;
	}

	public String getRemotePriceKey() {
		return remotePriceKey;
	}

	public void setRemotePriceKey(String remotePriceKey) {
		this.remotePriceKey = remotePriceKey;
	}
	
	public String getApiDescKey(){
		return apiDescKey;
	}
	
	public void setApiDescKey(String apiDescKey){
		this.apiDescKey = apiDescKey;
	}

	public String getNameFilterKey() {
		return nameFilterKey;
	}

	public void setNameFilterKey(String nameFilterKey) {
		this.nameFilterKey = nameFilterKey;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}

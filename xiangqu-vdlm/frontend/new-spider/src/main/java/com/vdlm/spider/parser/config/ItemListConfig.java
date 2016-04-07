/**
 * 
 */
package com.vdlm.spider.parser.config;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 12:20:50 AM Jul 20, 2014
 */
public class ItemListConfig implements Serializable {

	private static final long serialVersionUID = 8067463249057310238L;
	static final String[] searchList = { "{nickname}", "{startSize}" };
	static final String[] searchlist1 = {"{startSize}", "{endSize}", "{userId}"};

	private String url0;
	private String url1;
	private String url2;
	private String successStatusCode;
	private String refererUrl;

	//	#json index
	private String statusCode;//status.code
	private String totalPage;//page.totalPage
	private String currentPage;//page.currentPage
	private String pageSize;//page.pageSize
	private String itemList;//itemList
	private String itemId;//[].itemId
	private String itemUrl;//[].href
	private String auctions;//API.SSInshopApi.auctions
	private String auctionId;//[].nid
	

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	public String getSuccessStatusCode() {
		return successStatusCode;
	}

	public void setSuccessStatusCode(String successStatusCode) {
		this.successStatusCode = successStatusCode;
	}

	public String getUrl0() {
		return url0;
	}

	public String getUrl0(String nickName) {
		return StringUtils.replaceEach(url0, searchList, new String[] { nickName, "" });
	}

	public void setUrl0(String url0) {
		this.url0 = url0;
	}

	public String getUrl1() {
		return url1;
	}

	public String getUrl1(String nickName, String startSize) {
		return StringUtils.replaceEach(url1, searchList, new String[] { nickName, startSize });
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}
	
	public String getUrl2(){
		return url2;
	}
	
	public String getUrl2(String startSize, String endSize, String userId){
		return StringUtils.replaceEach(url2, searchlist1, new String[] { startSize, endSize, userId });
	}
	
	public void setUrl2(String url2){
		this.url2 = url2;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getItemList() {
		return itemList;
	}

	public void setItemList(String itemList) {
		this.itemList = itemList;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	
	public String getAuctions(){
		return auctions;
	}
	
	public void setAuctions(String auctions) {
		this.auctions = auctions;
	}
	
	public String getAuctionId(){
		return auctionId;
	}
	
	public void setAuctionId(String auctionId){
		this.auctionId = auctionId;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}

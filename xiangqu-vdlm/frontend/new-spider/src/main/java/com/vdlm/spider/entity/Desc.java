package com.vdlm.spider.entity;

import java.io.Serializable;
import java.util.List;

import com.vdlm.dal.vo.FragmentVO;

/**
 *
 * @author: chenxi
 */

public class Desc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4861757767296117864L;

	private Long id;
	private Long itemId;
	private String descUrl;
	private String details;
	private DescFragments fragments; //详情片段
	
	private List<String> imgs; // for wdetail brief desc api
	// TODO
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getItemId() {
		return itemId;
	}
	
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	public String getDescUrl() {
		return descUrl;
	}
	
	public void setDescUrl(String descUrl) {
		this.descUrl = descUrl;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public DescFragments getFragments() {
		return fragments;
	}
	
	public void setFragments(DescFragments fragments) {
		this.fragments = fragments;
	}

	public List<String> getImgs() {
		return imgs;
	}

	public void setImgs(List<String> imgs) {
		this.imgs = imgs;
	}

	@Override
	public String toString() {
		return "Desc [id=" + id + ", itemId=" + itemId + ", descUrl=" + descUrl
				+ ", details=" + details + "]";
	}
	
	static public class DescFragments {

		private List<FragmentVO> fragments;

		public List<FragmentVO> getFragments() {
			return fragments;
		}

		public void setFragments(List<FragmentVO> fragments) {
			this.fragments = fragments;
		}
		
	}
}

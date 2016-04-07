package com.vdlm.spider.wdetail;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;

/**
 *
 * @author: chenxi
 */

public class DescResponse {

	private String api;
	private String v;
	private List<String> ret;
	private Data data;
	
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	public List<String> getRet() {
		return ret;
	}

	public void setRet(List<String> ret) {
		this.ret = ret;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public static class Data {
		
		private String desc;
		private String summary;
		private List<String> pages;
		private List<String> images;

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public List<String> getPages() {
			return pages;
		}

		public void setPages(List<String> pages) {
			this.pages = pages;
		}

		public List<String> getImages() {
			return images;
		}

		public void setImages(List<String> images) {
			this.images = images;
		}
		
	}
	
	public List<FragmentVO> parseFragments() {
		final List<FragmentVO> fragments = Lists.newArrayList();
		final List<String> pages = getData().getPages();
		Element body;
		Elements children; 
		Element child;
		FragmentVO fragment;
		int index = 0;

		for (final String page : pages) {
			final Document doc = Jsoup.parse(page);
			body = Jsoup.parse(page).select("body").first();
			children = body.children();
			for (int i = 0; i < children.size(); i++) {
				child = children.get(i);
				if (child.tag().getName().equals("txt")) {
					fragment = createTxtFragment(index, child.text());
					fragments.add(fragment);
					index++;
				} else if (child.tag().getName().equals("img")) {
					fragment = createImgFragment(index, child.toString());
					fragments.add(fragment);
					index++;
				}
			}
		}
		
		return fragments;
	}

	private FragmentVO createTxtFragment(int index, String txt) {
		final FragmentVO fragment = new FragmentVO();
		fragment.setName("段落 " + index);
		fragment.setShowModel(true); // 默认为文字靠前
		fragment.setIdx(index);
		fragment.setDescription(txt);
		return fragment;
	}

	private FragmentVO createImgFragment(int index, String imgUrl) {
		final FragmentVO fragment = new FragmentVO();
		fragment.setName("段落 " + index);
		fragment.setShowModel(true); // 默认为文字靠前
		fragment.setIdx(index);
		final List<FragmentImageVO> fragImgs = new ArrayList<FragmentImageVO>();
		final FragmentImageVO fragImg = new FragmentImageVO();
		fragImg.setImgUrl(imgUrl);
		fragImgs.add(fragImg);
		fragment.setImgs(fragImgs);
		return fragment;
	}
	
}

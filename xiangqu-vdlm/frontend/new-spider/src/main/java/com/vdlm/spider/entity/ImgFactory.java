package com.vdlm.spider.entity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.config.ParserConfigProviders;

/**
 *
 * @author: chenxi
 */

public class ImgFactory {

	public static List<Img> createGroupImgs(Set<String> groupImgs) {
		return createGroupImgs(groupImgs, 0);
	}
	
	public static List<Img> createGroupImgs(Set<String> groupImgs, int t) {
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		for (final String u : groupImgs) {
			final String imgUrl = ParserUtils.formatImgUrl(u);
			// 组图不做size限制
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_GROUP);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(1);
				imgs.add(img);
				++t;
			}
		}
		return imgs;
	}
	
	public static List<Img> createWGroupImgs(Set<String> groupImgs) {
		return createWGroupImgs(groupImgs, 0);
	}
	
	public static List<Img> createWGroupImgs(Set<String> groupImgs, int t) {
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		for (final String imgUrl : groupImgs) {
//			final String imgUrl = ParserUtils.formatImgUrl(u);
			// 组图不做size限制
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_GROUP);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(1);
				imgs.add(img);
				++t;
			}
		}
		return imgs;
	}
	
	public static List<Img> createSkuImgs(Set<String> skuImgs) {
		return createSkuImgs(skuImgs, 0);
	}
	
	public static List<Img> createSkuImgs(Set<String> skuImgs, int t) {
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		// sku图不做size限制
		for (final String u : skuImgs) {
			final String imgUrl = ParserUtils.extractImgUrlFromStyle(u);
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_SKU);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(2);
				imgs.add(img);
				++t;
			}
		}
		return imgs;
	}
	
	public static List<Img> createWSkuImgs(Set<String> skuImgs) {
		return createWSkuImgs(skuImgs, 0);
	}
	
	public static List<Img> createWSkuImgs(Set<String> skuImgs, int t) {
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		// sku图不做size限制
		for (final String imgUrl : skuImgs) {
//			final String imgUrl = ParserUtils.extractImgUrlFromStyle(u);
			if (imgUrls.add(imgUrl)) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_SKU);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(2);
				imgs.add(img);
				++t;
			}
		}
		return imgs;
	}
	
	public static List<Img> createDetailImgs(ItemListTaskBean bean, Set<String> detailImgs) {
		return createDetailImgs(bean, detailImgs, 0);
	}
	
	public static List<Img> createDetailImgs(ItemListTaskBean bean, Set<String> detailImgs, int t) {
		final int imgSize = ParserConfigProviders.getItemConfig(bean).getImgSize();
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		int i = 0;
		for (final String u : detailImgs) {
			final String imgUrl = ParserUtils.formatUrl(u);
			if (imgUrls.add(imgUrl) && i < imgSize) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_DETAIL);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(3);
				imgs.add(img);
				++t;
				++i;
			}
		}
		return imgs;
	}
	
	public static List<Img> createWDetailImgs(ItemListTaskBean bean, Set<String> detailImgs) {
		return createDetailImgs(bean, detailImgs, 0);
	}
	
	public static List<Img> createWDetailImgs(ItemListTaskBean bean, Set<String> detailImgs, int t) {
		final int imgSize = ParserConfigProviders.getItemConfig(bean).getImgSize();
		final List<Img> imgs = new ArrayList<Img>();
		final Set<String> imgUrls = new LinkedHashSet<String>();
		int i = 0;
		for (final String imgUrl : detailImgs) {
//			final String imgUrl = ParserUtils.formatUrl(u);
			if (imgUrls.add(imgUrl) && i < imgSize) {
				final Img img = new Img();
				img.setImgUrl(imgUrl);
				img.setType(Statics.IMG_DETAIL);
				img.setOrderNum(t);
				img.setMd5(DigestUtils.md5Hex(imgUrl));
				img.setType(3);
				imgs.add(img);
				++t;
				++i;
			}
		}
		return imgs;
	}
	
}

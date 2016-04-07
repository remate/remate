package com.vdlm.spider.task.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import com.google.common.collect.Lists;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.parser.ParserUtils;

/**
 *
 * @author: chenxi
 */

public abstract class DescParseHelper {

	private static final String FRAG_TYPE_TXT = "txt";
	private static final String FRAG_TYPE_TEXT = "text";
	private static final String FRAG_TYPE_IMG = "img";
	
	/**
	 * 
	 * 解析desc信息 取var desc='xxx'里面的内容
	 */
	public static String formatDesc(String remoteDesc) {
		// for wdetail
		if (remoteDesc.indexOf("<html>") >= 0 && remoteDesc.indexOf("<body>") >= 0) {
			final Element body = Jsoup.parse(remoteDesc).select("body").get(0);
			body.select("script").remove();
			remoteDesc = body.html();
			return remoteDesc;
		}
		String desc = null;
		final int start = remoteDesc.indexOf("'") + 1; // '字符
		if (start <= 0) {
			return null;
		}
		final int end = remoteDesc.indexOf("'", start);
		desc = remoteDesc.substring(start, end);

		return desc;
	}
	
	/**
	 * 获取字符串中的图片列表
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getImgsFromString(String sString) {
		if (StringUtils.isBlank(sString)) {
			return Collections.EMPTY_LIST;
		}
		final List<String> list = new ArrayList<String>();
		final Pattern p = Pattern.compile("src=\"(.*?)\"");
		final Matcher m = p.matcher(sString);
		while (m.find()) {
			if (m.group(1) != null) {
				// 解析只需要url,摒弃gif格式图片
				if (m.group().contains(".gif")) {
					continue;
				}
				list.add(m.group(1));
			}
		}
		return list;
	}
	
	public static List<FragmentVO> getDescFragments(DescTaskBean bean, String sDesc) {
		if (StringUtils.isBlank(sDesc)) {
			return Collections.emptyList();
		}
		final List<FragmentVO> fragments = new ArrayList<FragmentVO>();
		List<FragmentImageVO> fragImgs = new ArrayList<FragmentImageVO>();
		FragmentVO fragment = null;

		String description = "";
		String lastMode = "";
		int i = 0;
		int j = 0;
		
		// 1, 先判断详情最前面没有没商品型号说明文本
		fragment = new FragmentVO();
		if (bean.getProps() != null) {
			fragment.setName("段落型号说明 ");
			fragment.setShowModel(true); // 默认为文字靠前
			fragment.setIdx(i);
			fragment.setShopId(bean.getOuerShopId());
			fragment.setDescription(bean.getProps());
			i++;
		}
		
		// 先解析<p></p>标签
		final Matcher ma = Pattern.compile("<p.*?>([\\s\\S]*?)</p>").matcher(sDesc);
		while (ma.find()) {
			String p = ma.group(1);
			if (p != null) {
				final String thisMode = getParagraphMode(p); // text, img
				if (thisMode.equals(FRAG_TYPE_TEXT)) {
					// 只提取文本
					p = getTextFromString(p);
				} else if (thisMode.equals(FRAG_TYPE_IMG)) {
					// 只提取url
					// 可能一个<p></p>中包含多个img
				}
				if (!thisMode.equals(lastMode)) {
					if (fragment != null) {
						fragments.add(fragment);
					}

					fragment = new FragmentVO();
					fragImgs = new ArrayList<FragmentImageVO>();
					description = "";
					i++;
				}
				fragment.setName("段落 " + i);
				fragment.setShowModel(true); // 默认为文字靠前
				fragment.setIdx(i);
				fragment.setShopId(bean.getOuerShopId());

				if (FRAG_TYPE_IMG.equals(thisMode)) {
					// 图片处理
					if (getImgsFromString(p).size() > 0) {
						for (int k = 0; k < getImgsFromString(p).size(); k++) {
							final FragmentImageVO fragmentImg = new FragmentImageVO();
							fragmentImg.setImgUrl(ParserUtils.formatUrl(getImgsFromString(p).get(k)));
							fragmentImg.setIdx(j++);
							fragImgs.add(fragmentImg);
						}
					}
					fragment.setImgs(fragImgs);
				} else {
					// 文本处理
					description += p + "\n";
					fragment.setDescription(description);
				}
				lastMode = thisMode;
			}
		}
		// 最后一个
		fragments.add(fragment);

		return fragments;
	}
	
	public static String getParagraphMode(String p) {
		return p.contains("<img") && p.contains(" src=") ? "img" : "text";
	}
	
	/**
	 * 获取字符串中标签文本，类 strip-tags
	 */
	public static String getTextFromString(String sString) {
		sString = sString.replaceAll("\\<.*?\\>", "");
		return sString.trim();
	}
	
	public static void main(String[] args) {
		String str = " <txt>木飞机 asdfasdf </txt> <txt>手心的玩物 </txt> <img>http://gw.alicdn.com/imgextra/i3/583887120/TB2mK3ybVXXXXb3XXXXXXXXXXXX_!!583887120.jpg <img>http://gw.alicdn.com/imgextra/i4/583887120/TB2mHIBbVXXXXaxXXXXXXXXXXXX_!!583887120.jpg <img>http://gw.alicdn.com/imgextra/i2/583887120/TB2VWMsbVXXXXaYXpXXXXXXXXXX_!!583887120.jpg <txt>木飞机<div> aaa <txt>手心的玩物 <img>http://gw.alicdn.com/imgextra/i3/583887120/TB2JhgubVXXXXawXpXXXXXXXXXX_!!583887120.jpg <img>http://gw.alicdn.com/imgextra/i2/583887120/TB2TAoCbVXXXXXUXXXXXXXXXXXX_!!583887120.jpg <img>http://gw.alicdn.com/imgextra/i1/583887120/TB2IUMzbVXXXXbgXXXXXXXXXXXX_!!583887120.jpg <img>http://gw.alicdn.com/imgextra/i1/583887120/TB2hBEtbVXXXXamXpXXXXXXXXXX_!!583887120.jpg ";
//		String[] arr = str.split("(</?txt>{1}|</?img>{1})");
//		final Matcher ma = Pattern.compile("(<txt>|<img>)").matcher(str);
//		int idx = 0;
//		while (ma.find()) {
//			String p = ma.group(1);
//			System.out.println(p + ":" + (StringUtils.isBlank(arr[idx]) ? arr[idx+=2] : arr[idx++]));
//		}
		List<String[]> rets = parseBodyTags(str, new String[] {"txt", "img"});
		System.out.println(rets.size());

	}
	
	// list<tag:data>
	public static List<String[]> parseBodyTags(String body, String tags[]) {
		List<String[]> rets = new ArrayList<String[]>();
		if (body == null) return Collections.emptyList();
		body = body.replaceAll("\\</*body\\>", "");  // remove body tag
		
		// 1: get content as order
		String splitStr = "(</?" + tags[0]+ ">{1}";
		for (int i = 1; i < tags.length; i++)
			splitStr += "|</?" + tags[i]+ ">{1}"; 
		splitStr += ")";
		//String[] arr = body.split("(</?txt>{1}|</?img>{1})");
		String[] arr = body.split(splitStr);
		// now <tag> data
		body = body.replaceAll("\\</.*?\\>", "");   // remove close tag
		
		String preTag = "(<" + tags[0]+ ">";
		for (int i = 1; i < tags.length; i++) {
			preTag += "|<" + tags[i]+ ">"; 
		}
		preTag += ")";
		
		// get tag as order
		//final Matcher ma = Pattern.compile("(<txt>|<img>)").matcher(body);
		final Matcher ma = Pattern.compile(preTag).matcher(body);
		int idx = 0;
		while (ma.find()) {
			String p = ma.group(1);
			String[] ret  = new String[2];
			if (p != null && preTag.contains(p)) {
				ret[0] = p.replaceAll("[<>]", "");
				if (StringUtils.isBlank(arr[idx]))
					idx++;
				ret[1] = arr[idx].replaceAll("\n", "").trim();
				idx++;
				//ret[1] = (StringUtils.isBlank(arr[idx]) ? arr[idx+=2] : arr[idx++]);
				rets.add(ret);
			}
		}
		return rets;
	}
	
	private static List<String[]> parserBody(Element body, String[] tags) {
		List<String[]> ret = new ArrayList<String[]>();
		if (body == null) return Collections.emptyList();;
		
		List<String[]> maps = parseBodyTags(body.toString(), tags);
		for (String[] aMap : maps) {
			for (String tag : tags) {
				if (aMap[0].equals(tag)) 
					ret.add(aMap);
			}
		}
		return ret;
	}
	
	public static List<FragmentVO> parseFragments(DescTaskBean bean, List<String> pages) {
		final List<FragmentVO> fragments = Lists.newArrayList();
		Element body;
		FragmentVO fragment;
		int index = 0;
		
		// 1, 先判断详情最前面没有没商品型号说明文本
		fragment = new FragmentVO();
		if (bean.getProps() != null) {
			fragment.setName("段落型号说明 ");
			fragment.setShowModel(true); // 默认为文字靠前
			fragment.setIdx(index++);
			fragment.setShopId(bean.getOuerShopId());
			fragment.setDescription(bean.getProps());
			index++;
		}
		fragments.add(fragment);
		
		for (final String page : pages) {
			body = Jsoup.parse(page).select("body").first();
			List<String[]> conts = parserBody(body, new String[] {FRAG_TYPE_TXT, FRAG_TYPE_IMG} );
			for (String cont[]  : conts) {
				if (FRAG_TYPE_TXT.equals(cont[0])) {
					fragment = createTxtFragment(index, cont[1]);
					fragment.setShopId(bean.getOuerShopId());
					fragments.add(fragment);
					index++;
				} else if (FRAG_TYPE_IMG.equals(cont[0])) {
					fragment = createImgFragment(index, cont[1]);
					fragment.setShopId(bean.getOuerShopId());
					fragments.add(fragment);
					index++;
				} else { continue; }
			}
		}
		
		return fragments;
	}

	private static FragmentVO createTxtFragment(int index, String txt) {
		final FragmentVO fragment = new FragmentVO();
		fragment.setName("段落 " + index);
		fragment.setShowModel(true); // 默认为文字靠前
		fragment.setIdx(index);
		fragment.setDescription(txt);
		return fragment;
	}

	private static FragmentVO createImgFragment(int index, String imgUrl) {
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

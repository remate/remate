/**
 * 
 */
package com.vdlm.spider.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.springframework.web.util.HtmlUtils;

import com.vdlm.spider.Statics;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.utils.CollectionTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:37:53 AM Jul 18, 2014
 */
public class ParserUtils {

	static final char[] LEFT_CHARS = { '(', '\'', '"' };
	static final char[] RIGHT_CHARS = { ')', '\'', '"' };
	static final String HTTP_PREFIX = "http://";
	static final String HTTP_SHORT = "//"; 

	// 请求url是否匹配http 20150521
	public static String formatUrl(String remoteUrl) {
		String retUrl = null;
		if (remoteUrl.indexOf("http:") < 0)
			retUrl = "http:" + remoteUrl;
		else
			retUrl = remoteUrl;

		return retUrl;
	}
	
	// 提取style中提图片
	public static String extractImgUrlFromStyle(String style) {
		if (StringUtils.isBlank(style)) {
			return null;
		}

		final int bgn = style.indexOf(HTTP_SHORT);
		if (bgn < 0) {
			return null;
		}
		
		final int start = style.indexOf(HTTP_SHORT) - 2;
		final int end = style.indexOf(")", start);

		return formatImgUrl(formatUrl(style.substring(bgn, end)));
	}

	public static String formatImgUrl(String imgUrl) {
		if (StringUtils.isBlank(imgUrl)) {
			return null;
		}

		final StringBuilder sb = new StringBuilder(formatUrl(imgUrl));
		while (true) {
			final int index = sb.lastIndexOf("_");
			if (index < 0) {
				return sb.toString();
			}
			boolean match = false;
			a: for (String suffix : Statics.IMG_SUFFIX) {
				if (index < suffix.length()) {
					continue;
				}

				for (int i = 0; i < suffix.length(); i++) {
					final char ch1 = suffix.charAt(suffix.length() - 1 - i);
					final char ch2 = sb.charAt(index - 1 - i);
					if (ch1 != ch2) {
						continue a;
					}
				}

				match = true;
				break;
			}

			if (match) {
				sb.delete(index, sb.length());
				continue;
			}
			else {
				return sb.toString();
			}
		}
	}

	/**
	 * <pre>
	 * Node是否匹配当前prop
	 * </pre>
	 * @param node
	 * @param prop
	 * @param configs
	 * @return
	 */
	public static List<ParserConfig> match(Node node, String prop, Map<String, ParserConfig> configs) {
		final List<ParserConfig> results = new ArrayList<ParserConfig>();
		for (Map.Entry<String, ParserConfig> entry : configs.entrySet()) {
			if (StringUtils.equals(entry.getValue().getProp(), prop)
					&& !StringUtils.endsWith(entry.getKey(), "-extract")) {
				if (node.getClass() == TagClasses.getTagClass(entry.getValue().getTag())) {
					if (StringUtils.isBlank(entry.getValue().getAttrsName())) {
						results.add(entry.getValue());
					}
					else if (match(node, entry.getValue())) {
						results.add(entry.getValue());
					}
				}
			}
		}
		return results;
	}

	static boolean match(final Node node, final ParserConfig config) {
		if (node instanceof Tag) {
			final Tag tag = (Tag) node;

			//			if (!StringUtils.equals(tag.getRawTagName(), config.getTag().getValue())) {
			//				return false;
			//			}
			//
			//			if (StringUtils.isBlank(config.getAttrsName())) {
			//				return true;
			//			}

			final Attribute attr = tag.getAttributeEx(config.getAttrsName());
			// no attribute
			if (attr == null) {
				return false;
			}

			if (StringUtils.isBlank(config.getAttrsValue())) {
				return true;
			}

			final String value = StringUtils.trim(config.getAttrsValue());
			final String av = StringUtils.trim(attr.getValue());

			if (StringUtils.equals(av, value)) {
				return true;
			}

			if (StringUtils.isNotBlank(av) && "class".equals(config.getAttrsName()) && value.contains(av)) {
				return StringUtils.startsWithIgnoreCase(value.replace(av, ""), "select");
			}
		}

		return false;
	}

	/**
	 * <pre>
	 * 获取匹配的集合, 空值忽略
	 * </pre>
	 * @param node
	 * @param dests
	 * @param configs
	 * @return
	 */
	public static List<String> getValues(Node node, List<ParserConfig> dests, Map<String, ParserConfig> configs) {
		final List<String> results = new ArrayList<String>();
		for (ParserConfig dest : dests) {
			results.addAll(getValues(node, dest, configs));
		}
		return results;
	}

	//
	//	public static List<String> extractChildren(Node node, String prop, Map<String, ParserConfig> configs) {
	//		final List<ParserConfig> dests = new ArrayList<ParserConfig>();
	//		for (Map.Entry<String, ParserConfig> entry : configs.entrySet()) {
	//			if (StringUtils.equals(entry.getValue().getProp(), prop)) {
	//				dests.add(entry.getValue());
	//			}
	//		}
	//		if (CollectionUtils.isEmpty(dests)) {
	//			return Collections.EMPTY_LIST;
	//		}
	//
	//		final NodeList nodeList = node.getChildren();
	//		if (nodeList == null || nodeList.size() == 0) {
	//			return Collections.EMPTY_LIST;
	//		}
	//
	//		final NodeList nodeList2;
	//		try {
	//			nodeList2 = nodeList.extractAllNodesThatMatch(NodeFilters.getOrCreateNodeFilter(dests), true);
	//		}
	//		catch (Exception e) {
	//			//ignore
	//			return Collections.EMPTY_LIST;
	//		}
	//		if (nodeList2 == null || nodeList2.size() == 0) {
	//			return Collections.EMPTY_LIST;
	//		}
	//
	//		final List<String> results = new ArrayList<String>(nodeList2.size());
	//
	//		for (int i = 0; i < nodeList2.size(); i++) {
	//			Node aNode = nodeList2.elementAt(i);
	//		}
	//		
	//		return results;
	//	}

	@SuppressWarnings("unchecked")
	public static List<String> getValues(Node node, ParserConfig dest, Map<String, ParserConfig> configs) {
		if (StringUtils.isBlank(dest.getExtractAttr())) {
			return extractChildren(node, configs.get(dest.getIndex() + "-extract"));
		}

		final String result = getValue0(node, dest);
		if (StringUtils.isNotBlank(result)) {
			return CollectionTools.asArrayList(result);
		}
		else {
			return Collections.EMPTY_LIST;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> extractChildren(Node node, ParserConfig dest) {
		if (dest == null) {
			return Collections.EMPTY_LIST;
		}
		final NodeList nodeList = node.getChildren();
		if (nodeList == null || nodeList.size() == 0) {
			return Collections.EMPTY_LIST;
		}

		final NodeList nodeList2;
		try {
			nodeList2 = nodeList.extractAllNodesThatMatch(NodeFilters.getOrCreateNodeFilter(dest), true);
		}
		catch (Exception e) {
			//ignore
			return Collections.EMPTY_LIST;
		}
		if (nodeList2 == null || nodeList2.size() == 0) {
			return Collections.EMPTY_LIST;
		}

		final List<String> results = new ArrayList<String>(nodeList2.size());

		for (int i = 0; i < nodeList2.size(); i++) {
			final String result = getValue0(nodeList2.elementAt(i), dest);
			if (StringUtils.isNotBlank(result)) {
				results.add(result);
			}
		}
		return results;
	}

	static void getStringText(Node node, List<String> texts) {
		final NodeList nodeList = node.getChildren();
		if (nodeList == null || nodeList.size() == 0) {
			if (node instanceof TextNode) {
				final String text = StringUtils.trim(node.getText());
				if (StringUtils.isNotBlank(text)) {
					texts.add(text);
				}
			}
		}
		else {
			for (int i = 0; i < nodeList.size(); i++) {
				getStringText(nodeList.elementAt(i), texts);
			}
		}

	}

	// 仅仅获取值
	static String getValue0(Node node, ParserConfig dest) {
		if ("strText.all".equals(dest.getExtractAttr())) {
			final String text = StringUtils.trimToEmpty(node.toPlainTextString());
			if (StringUtils.isBlank(text)) {
				return null;
			}
			final StringBuilder sb = new StringBuilder();
			for (String s : text.split("(\r)*\n")) {
				sb.append(' ').append(s.trim());
			}
			return HtmlUtils.htmlUnescape(sb.deleteCharAt(0).toString());
		}
		else if ("strText".equals(dest.getExtractAttr())) {
			final NodeList nodeList = node.getChildren();
			if (nodeList == null || nodeList.size() == 0) {
				return HtmlUtils.htmlUnescape(StringUtils.trim(node.getText()));
			}
			final List<String> texts = new ArrayList<String>();
			getStringText(node, texts);
			return HtmlUtils.htmlUnescape(texts.isEmpty() ? null : StringUtils.trim(texts.get(0)));
		}
		else {
			return HtmlUtils.htmlUnescape(StringUtils.trim(((TagNode) node).getAttribute(dest.getExtractAttr())));
		}
	}

	//	/**
	//	 * <pre>
	//	 *  获取值 
	//	 * </pre>
	//	 * @param node
	//	 * @param prop
	//	 * @param configs
	//	 * @return
	//	 */
	//	public static String getValue(Node node, String prop, Map<String, ParserConfig> configs) {
	//		final ParserConfig config = configs.get(prop);
	//		if (config == null) {
	//			return null;
	//		}
	//		else if (StringUtils.isBlank(config.getExtractAttr())) {
	//			final List<String> children = extractChildren(node, prop, configs);
	//			return children.isEmpty() ? null : children.get(0);
	//		}
	//		else if ("strText".equals(config.getExtractAttr())) {
	//			return StringUtils.trim(((CompositeTag) node).getStringText());
	//		}
	//		else {
	//			return ((TagNode) node).getAttribute(config.getExtractAttr());
	//		}
	//	}
	//
	//	static List<String> createArrayList() {
	//		return new ArrayList<String>(1);
	//	}
	//
	//	static List<String> createArrayList(String e) {
	//		if (StringUtils.isBlank(e)) {
	//			return createArrayList();
	//		}
	//		final List<String> results = createArrayList();
	//		results.add(StringUtils.trim(e));
	//		return results;
	//	}
	//
	//	public static List<String> extractChildren(Node node, String prop, Map<String, ParserConfig> configs) {
	//		final String extractProp = prop + "-extract";
	//		return extract2List(node, extractProp, configs);
	//	}
	//
	//	public static List<String> extract2List(Node node, String prop, Map<String, ParserConfig> configs) {
	//		final ParserConfig cnf = configs.get(prop);
	//		if (cnf == null) {
	//			return createArrayList();
	//		}
	//
	//		final NodeList nodeList = node.getChildren();
	//		if (nodeList == null || nodeList.size() == 0) {
	//			return createArrayList();
	//		}
	//
	//		final NodeList nodeList2;
	//		try {
	//			nodeList2 = nodeList.extractAllNodesThatMatch(NodeFilters.getOrCreateNodeFilter(cnf), true);
	//		}
	//		catch (Exception e) {
	//			//ignore
	//			return createArrayList();
	//		}
	//		if (nodeList2 == null || nodeList2.size() == 0) {
	//			return createArrayList();
	//		}
	//
	//		final List<String> results = new ArrayList<String>(nodeList2.size());
	//
	//		for (int i = 0; i < nodeList2.size(); i++) {
	//			final String result = getValue(nodeList2.elementAt(i), prop, configs);
	//			if (StringUtils.isNotBlank(result)) {
	//				results.add(result);
	//			}
	//		}
	//		return results;
	//	}
	//
	//	public static List<String> getValues(Node node, String prop, Map<String, ParserConfig> configs) {
	//		final ParserConfig config = configs.get(prop);
	//		if (config == null) {
	//			return createArrayList();
	//		}
	//		else if (StringUtils.isBlank(config.getExtractAttr())) {
	//			// 目前仅支持二级
	//			return extractChildren(node, prop, configs);
	//		}
	//		else if ("strText".equals(config.getExtractAttr())) {
	//			return createArrayList(((CompositeTag) node).getStringText());
	//		}
	//		else {
	//			return createArrayList(((TagNode) node).getAttribute(config.getExtractAttr()));
	//		}
	//	}

	/**
	 * <pre>
	 * 取最小值
	 * </pre>
	 * @param values
	 * @return
	 */
	public static Double getMinValue(List<String> values) {
		Double result = null;
		for (String value : values) {
			if (StringUtils.isBlank(value)) {
				continue;
			}
			Double d = null;
			try {
				d = Double.valueOf(value.trim());
			}
			catch (Exception e) {
				d = null;
			}
			if (d != null && (result == null || result.doubleValue() > d.doubleValue())) {
				result = d;
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 取最大值
	 * </pre>
	 * @param values
	 * @return
	 */
	public static Double getMaxValue(List<String> values) {
		Double result = null;
		for (String value : values) {
			if (StringUtils.isBlank(value)) {
				continue;
			}
			Double d = null;
			try {
				d = Double.valueOf(value.trim());
			}
			catch (Exception e) {
				d = null;
			}
			if (d != null && (result == null || d.doubleValue() > result.doubleValue())) {
				result = d;
			}
		}
		return result;
	}

	/**
	 * <pre>
	 * 取 URL 中的参数
	 * </pre>
	 * @param url
	 * @param name
	 * @return
	 */
	public static String getUrlParam(String url, String name) {
		String value;

		value = getUrlParam0(url, '?' + name + '=');
		if (StringUtils.isNotBlank(value)) {
			return value;
		}
		value = getUrlParam0(url, '&' + name + '=');
		return value;
	}

	static String getUrlParam0(String url, String name) {
		int start = url.indexOf(name);
		if (start >= 0) {
			int index = url.indexOf('&', start + 1);
			if (index >= 0) {
				return url.substring(start + name.length(), index);
			}
			index = url.indexOf('#', start + 1);
			if (index >= 0) {
				return url.substring(start + name.length(), index);
			}

			return url.substring(start + name.length());
		}
		return null;
	}

	/**
	 * <pre>
	 * 忽略url中的某些参数
	 * </pre>
	 * @param url
	 * @param ignoreParameters
	 * @return
	 */
	public static String ignoreUrlParameters(String url, String[] ignoreParameters) {
		if (ArrayUtils.isEmpty(ignoreParameters)) {
			return url;
		}
		final StringBuilder result = new StringBuilder(url);
		for (String ignoreParameter : ignoreParameters) {
			ignoreUrlParameter(result, ignoreParameter);
		}
		return result.toString();
	}

	static boolean ignoreUrlParameter(StringBuilder url, String ignoreParameter) {
		if (ignoreUrlParameter0(url, '?' + ignoreParameter + '=')) {
			return true;
		}
		if (ignoreUrlParameter0(url, '&' + ignoreParameter + '=')) {
			return true;
		}
		return false;
	}

	static boolean ignoreUrlParameter0(StringBuilder url, String ignoreParameter) {
		final int start = url.indexOf(ignoreParameter);

		if (start <= 0) {
			return false;
		}

		final char ch = url.charAt(start);

		final int end1 = url.indexOf("&", start + 1);
		final int end2 = url.indexOf("#", start + 1);

		// 最后的参数
		// http://g.cn?a=1#bd
		if (end1 < 0) {
			if (end2 < 0) {
				url.delete(start, url.length());
			}
			else {
				url.delete(start, end2);
			}
			return true;
		}

		// http://g.cn?a=1
		if (end2 < 0) {
			if (ch == '?') {
				url.delete(start + 1, end1 + 1);
			}
			else {
				url.delete(start, end1);
			}
			return true;
		}

		// # 在前
		// http://g.cn?a=1#..&b=3
		if (end1 > end2) {
			if (ch == '?') {
				url.delete(start + 1, end2);
			}
			else {
				url.delete(start, end2);
			}
			return true;
		}

		// # 在后
		// http://g.cn?a=1&b=2#bd

		if (ch == '?') {
			url.delete(start + 1, end1 + 1);
		}
		else {
			url.delete(start, end1);
		}
		return true;
	}
}

/**
 * 
 */
package com.vdlm.spider.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:41:05 PM Jul 20, 2014
 */
public abstract class JSONUtils {

	public static List<String> extractList(Object json, String[] keywords, String key) {
		final List<String> values = new ArrayList<String>();
		extractList(json, keywords, key, values);

		return values;
	}

	public static void extractList(Object json, String[] keywords, String key, List<String> values) {
		if (json == null) {

		}
		else if (json instanceof JSONObject) {
			for (String str : ((JSONObject) json).keySet()) {
				boolean exists = true;
				if (keywords != null) {
					for (String keyword : keywords) {
						if (!StringUtils.contains(str, keyword)) {
							exists = false;
							break;
						}
					}
				}
				final Object obj = ((JSONObject) json).get(str);

				if (obj == null) {
					continue;
				}

				if (exists) {
					if (StringUtils.equals(str, key)
							&& (obj instanceof Number || obj instanceof String || obj instanceof Character)) {
						values.add(obj.toString());
						continue;
					}
					extractList(((JSONObject) json).get(str), key, values);
				}

				extractList(((JSONObject) json).get(str), keywords, key, values);
			}
		}
		else if (json instanceof JSONArray) {
			final JSONArray arr = (JSONArray) json;
			for (int i = 0; i < arr.size(); i++) {
				extractList(arr.get(i), keywords, key, values);
			}
		}
	}

	public static void extractList(Object json, String key, List<String> values) {
		if (json == null) {

		}
		else if (json instanceof JSONObject) {
			for (String str : ((JSONObject) json).keySet()) {
				final Object obj = ((JSONObject) json).get(str);
				if (obj == null) {
					continue;
				}
				else if (StringUtils.equals(str, key) && (obj instanceof String || obj instanceof Number)) {
					values.add(obj.toString());
				}
				else {
					extractList(obj, key, values);
				}
			}
		}
		else if (json instanceof JSONArray) {
			final JSONArray arr = (JSONArray) json;
			for (int i = 0; i < arr.size(); i++) {
				extractList(arr.get(i), key, values);
			}
		}
	}

	/**
	 * <pre>
	 * 根据关键字 获取JSON 字符串
	 * </pre>
	 * 
	 * @param text
	 * @param kw
	 * @return
	 */
	public static String getJSONString(final String text, final String kw) {
		final int index = text.indexOf(kw);
		if (index < 0) {
			return null;
		}
		final int bgn = text.indexOf('{', index);
		if (index < 0) {
			return null;
		}

		int left = 0;
		int right = 0;
		int end;
		// 找到完整的json串
		for (int i = bgn;; i++) {
			final char ch = text.charAt(i);
			if (ch == '{') {
				++left;
			} else if (ch == '}') {
				++right;
			}
			if (left == right) {
				end = i;
				break;
			}
		}
		return text.substring(bgn, end + 1);
	}
	

}

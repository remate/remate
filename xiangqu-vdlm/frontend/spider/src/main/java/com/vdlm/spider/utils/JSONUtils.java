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
}

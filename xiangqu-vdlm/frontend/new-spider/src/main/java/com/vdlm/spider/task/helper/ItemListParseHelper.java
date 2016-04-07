package com.vdlm.spider.task.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author: chenxi
 */

public abstract class ItemListParseHelper {

	public static Object get(JSONObject json, String jsonIndex) {
		final String[] idxs = jsonIndex.split("\\.");

		Object obj = json;
		for (final String idx : idxs) {
			final int bgn = idx.lastIndexOf('[');
			final int end = idx.lastIndexOf(']');
			if (bgn < 0 || end < 0 || bgn > end) {
				obj = ((JSONObject) obj).get(idx);
				continue;
			}

			if (end == idx.length() - 1) {
				if (bgn != 0) {
					obj = ((JSONObject) obj).get(idx.substring(0, bgn));
				}
				obj = ((JSONArray) obj).get(Integer.parseInt(idx.substring(
						bgn + 1, end - 1)));
			} else {
				obj = ((JSONObject) obj).get(idx);
			}
		}

		return obj;
	}
	
	/*
	 * index:split分割合并位数(按第1位开始计算)
	 */
	public static JSONArray get(JSONObject json, String jsonIndex, Integer index) {
		final String[] idxs = new String[jsonIndex.split("\\.").length + 1];
		for (int i = 0; i < idxs.length - 1; i++) {
			idxs[i] = jsonIndex.split("\\.")[i];
		}
		idxs[idxs.length - 1] = jsonIndex.split("\\.")[jsonIndex.split("\\.").length - 1];

		Object obj = json;
		int i = 0;
		int j = 0;
		String str = "";
		for (final String idx : idxs) {
			if (i < index) {
				++i;
				str += idx + ".";
				continue;
			}
			if (str.equals("")) {
				str = idx;
			} else {
				if (str.split("\\.").length > 0) {
					if (j > 0) {
						str = idx;
					} else {
						str = str.substring(0, str.length() - 1);
					}
					++j;
				} else {
					str = idx;
				}
			}
			final int bgn = str.lastIndexOf('[');
			final int end = str.lastIndexOf(']');
			if (bgn < 0 || end < 0 || bgn > end) {
				obj = ((JSONObject) obj).get(str);
				continue;
			}

			if (end == str.length() - 1) {
				if (bgn != 0) {
					obj = ((JSONObject) obj).get(str.substring(0, bgn));
				}
				obj = ((JSONArray) obj).get(Integer.parseInt(str.substring(
						bgn + 1, end - 1)));
			} else {
				obj = ((JSONObject) obj).get(str);
			}
		}
		if (!(obj instanceof JSONArray)) {
			obj = null;
		}

		return (JSONArray) obj;
	}
}

/**
 * 
 */
package com.vdlm.spider.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:35:29 PM Aug 11, 2014
 */
public class MapTools {

	public static Map<String, Object> asHashMap(String k, Object v) {
		final HashMap<String, Object> result = new HashMap<String, Object>(1);
		result.put(k, v);
		return result;
	}
	
	public static Map<String, Object> asHashMap(String k, Object v, String k1, Object v1) {
		final HashMap<String, Object> result = new HashMap<String, Object>(1);
		result.put(k, v);
		result.put(k1, v1);
		return result;
	}
}

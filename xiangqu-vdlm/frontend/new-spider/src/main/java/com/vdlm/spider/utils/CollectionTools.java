/**
 * 
 */
package com.vdlm.spider.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:02:52 AM Jul 23, 2014
 */
public class CollectionTools {

	public static <T> List<T> asArrayList(T e) {
		final ArrayList<T> result = new ArrayList<T>(1);
		result.add(e);
		return result;
	}

	public static String asStringIgnoreNull(Collection<?> coll) {
		return asStringIgnoreNull(coll, ',');
	}
	
	public static String asStringIgnoreNull(Collection<?> coll, char ch) {
		if (CollectionUtils.isEmpty(coll)) {
			return StringUtils.EMPTY;
		}

		final StringBuilder result = new StringBuilder();
		for (Object e : coll) {
			if (e == null) {
				continue;
			}
			result.append(ch).append(e);
		}

		if (result.length() > 0) {
			result.deleteCharAt(0);
		}

		return result.toString();
	}
}

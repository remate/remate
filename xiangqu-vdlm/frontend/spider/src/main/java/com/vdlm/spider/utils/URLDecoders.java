/**
 * 
 */
package com.vdlm.spider.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:41:51 PM Aug 12, 2014
 */
public abstract class URLDecoders {

	static boolean hasEncoded(String text) {
		int index = 0;
		for (int i = 0; i < text.length(); i++) {
			final int end = text.indexOf('%', index);
			if (end == -1) {
				return false;
			}
			if (end + 2 > text.length() - 1) {
				return false;
			}
			final int ch1 = text.charAt(end + 1);
			final int ch2 = text.charAt(end + 2);
			if (((ch1 >= 65 && ch1 <= 70) || (ch1 >= 97 && ch1 <= 102) || (ch1 >= 48 && ch1 <= 57))
					&& ((ch2 >= 65 && ch2 <= 70) || (ch2 >= 97 && ch2 <= 102) || (ch2 >= 48 && ch2 <= 57))) {
				return true;
			}

			index = end + 1;
		}
		return false;
	}

	public static String decode(String text, String charset) throws UnsupportedEncodingException {
		if (hasEncoded(text)) {
			return URLDecoder.decode(text, charset);
		}
		return text;
	}
}

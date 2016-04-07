/**
 * 
 */
package com.vdlm.spider.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:47:18 PM Aug 12, 2014
 */
public class URLDecodersTests {

	@Test
	public void testDecode() throws UnsupportedEncodingException {
		Assert.assertEquals(URLDecoders.decode(URLEncoder.encode("%ab_c*", "utf-8"), "utf-8"),"%ab_c*");
		Assert.assertEquals(URLDecoders.decode(URLEncoder.encode("ab_c*", "utf-8"), "utf-8"),"ab_c*");
		Assert.assertEquals(URLDecoders.decode("ab_c*", "utf-8"),"ab_c*");
		Assert.assertEquals(URLDecoders.decode(URLEncoder.encode("中文*", "utf-8"), "utf-8"),"中文*");
		Assert.assertEquals(URLDecoders.decode("中文*", "utf-8"),"中文*");
	}
}

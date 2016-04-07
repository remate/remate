/**
 * 
 */
package com.vdlm.spider.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:38:25 AM Jul 18, 2014
 */
public class ParserUtilsTests {

	@Test
	public void testExtractImgUrlFromStyle() {

		Assert.assertNull(ParserUtils.extractImgUrlFromStyle(null));

		Assert.assertEquals(
				ParserUtils
						.extractImgUrlFromStyle("background:url(http://gi2.md.alicdn.com/bao/uploaded/i2/1574863805/T2z4fnXvxaXXXXXXXX_!!1574863805.jpg_30x30q90.jpg) center no-repeat;"),
				"http://gi2.md.alicdn.com/bao/uploaded/i2/1574863805/T2z4fnXvxaXXXXXXXX_!!1574863805.jpg");

		Assert.assertEquals(
				ParserUtils
						.extractImgUrlFromStyle("background:url(http://gi2.md.alicdn.com/bao/uploaded/i2/1574863805/T2z4fnXvxaXXXXXXXX_!!1574863805.jpg) center no-repeat;"),
				"http://gi2.md.alicdn.com/bao/uploaded/i2/1574863805/T2z4fnXvxaXXXXXXXX_!!1574863805.jpg");

	}

	@Test
	public void testFormatImgUrl() {
		Assert.assertEquals(
				ParserUtils
						.formatImgUrl("http://img04.taobaocdn.com/imgextra/i4/1786199931/T2nId.XY8XXXXXXXXX_!!1786199931.jpg_50x50.jpg"),
				"http://img04.taobaocdn.com/imgextra/i4/1786199931/T2nId.XY8XXXXXXXXX_!!1786199931.jpg");
		Assert.assertEquals(
				ParserUtils
						.formatImgUrl("http://img04.taobaocdn.com/imgextra/i4/1786199931/T2nId.XY8XXXXXXXXX_!!1786199931.jpg.png_50x50.jpg"),
				"http://img04.taobaocdn.com/imgextra/i4/1786199931/T2nId.XY8XXXXXXXXX_!!1786199931.jpg.png");
	}

	@Test
	public void testGetUrlParam() {

		Assert.assertEquals(ParserUtils.getUrlParam("http://detail.tmall.com/item.htm?id=19907038456", "id"),
				"19907038456");
		Assert.assertEquals(ParserUtils.getUrlParam("http://detail.tmall.com/item.htm?id=19907038456#detail", "id"),
				"19907038456");
		Assert.assertEquals(ParserUtils.getUrlParam("http://detail.tmall.com/item.htm?rn=123456&id=19907038456", "id"),
				"19907038456");
	}

	@Test
	public void testIgnoreUrlParameter() {
		StringBuilder sb1 = new StringBuilder("http://g.cn?b=1");
		Assert.assertTrue(!ParserUtils.ignoreUrlParameter(sb1, "a"));
		Assert.assertEquals(sb1.toString(), "http://g.cn?b=1");

		StringBuilder sb2 = new StringBuilder("http://g.cn?a=1");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb2, "a"));
		Assert.assertEquals(sb2.toString(), "http://g.cn");

		StringBuilder sb3 = new StringBuilder("http://g.cn?a=1#bd");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb3, "a"));
		Assert.assertEquals(sb3.toString(), "http://g.cn#bd");

		StringBuilder sb4 = new StringBuilder("http://g.cn?a=1#bd&b=1");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb4, "a"));
		Assert.assertEquals(sb4.toString(), "http://g.cn?#bd&b=1");

		StringBuilder sb5 = new StringBuilder("http://g.cn?a=1&b=1#bd");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb5, "a"));
		Assert.assertEquals(sb5.toString(), "http://g.cn?b=1#bd");

		StringBuilder sb6 = new StringBuilder("http://g.cn?a=1&b=1#bd");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb6, "b"));
		Assert.assertEquals(sb6.toString(), "http://g.cn?a=1#bd");

		StringBuilder sb7 = new StringBuilder("http://g.cn?a=1&b=1&c=1#bd");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb7, "b"));
		Assert.assertEquals(sb7.toString(), "http://g.cn?a=1&c=1#bd");

		StringBuilder sb8 = new StringBuilder("http://g.cn?a=1&b=1&c=1#bd");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb8, "a"));
		Assert.assertEquals(sb8.toString(), "http://g.cn?b=1&c=1#bd");

		StringBuilder sb9 = new StringBuilder("http://g.cn?a=1&b=1&c=1");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb9, "a"));
		Assert.assertEquals(sb9.toString(), "http://g.cn?b=1&c=1");

		StringBuilder sb10 = new StringBuilder("http://g.cn?a=1&b=1&c=1");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb10, "b"));
		Assert.assertEquals(sb10.toString(), "http://g.cn?a=1&c=1");

		StringBuilder sb11 = new StringBuilder("http://g.cn?a=1&b=1&c=1");
		Assert.assertTrue(ParserUtils.ignoreUrlParameter(sb11, "c"));
		Assert.assertEquals(sb11.toString(), "http://g.cn?a=1&b=1");
	}
}

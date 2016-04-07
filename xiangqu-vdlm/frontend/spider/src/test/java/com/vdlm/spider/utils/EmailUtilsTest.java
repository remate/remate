/**
 * 
 */
package com.vdlm.spider.utils;

import org.junit.Test;

import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:33:40 PM Apr 19, 2015
 */
public class EmailUtilsTest {

	@Test
	public void testSend() {
		final StringBuilder warning = new StringBuilder();
		warning.append("\t").append(ShopType.TAOBAO).append("\n");

		EmailUtils.sendQuietly("爬虫当前无有效的代理ip可用", "以下站点没有可用代理ip：\n" + warning);
	}
}

/**
 * 
 */
package com.vdlm.spider.bean;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 8:07:10 PM Jul 22, 2014
 */
public class ParseShopTaskBeanTests {

	@Test
	public void testToJSONBytes() {
		final ParseShopTaskBean bean = new ParseShopTaskBean();
		bean.setTaskId(UUID.randomUUID().toString());
		bean.setOuerShopId("1");
		bean.setOuerUserId("2");
		bean.setShopType(ShopType.TAOBAO);
		bean.setShopUrl("http://diplomat.tmall.com");
		bean.setRequestUrl("http://diplomat.tmall.com/search.htm");

		final byte[] bytes = bean.toJSONBytes();

		Assert.assertArrayEquals(ParseShopTaskBean.parse(bytes).toJSONBytes(), bytes);
	}
}

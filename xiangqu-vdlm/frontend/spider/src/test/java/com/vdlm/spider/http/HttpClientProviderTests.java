/**
 * 
 */
package com.vdlm.spider.http;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:59:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class HttpClientProviderTests {

	@TestedObject
	HttpClientProvider testObject;

	@Mock
	IpPools ipPools;

	@Before
	public void before() {
		this.testObject = new HttpClientProvider();
		this.testObject.setIpPools(ipPools);
		this.testObject.setCookieStoreProvider(new CookieStoreProvider());
		this.testObject.init();
	}

	@Test
	public void testProvide() {
		final String ip = HttpStatics.LOCAL_IP;

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		final HttpClientInvoker invoker = this.testObject.provide(ShopType.TAOBAO, null);

		final HttpInvokeResult result = invoker.invoke("http://item.taobao.com/item.htm?id=38614922233");

		System.out.println(result.getTmallUrl());
		Assert.assertEquals(result.getStatusCode(), 200);

		System.out.println(result.getContentString());

		EasyMockUnitils.verify();
	}

	@Test
	public void testProvide_useProxy() throws Exception {
		final String ip = "182.254.130.85:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();
		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		HttpClientInvoker invoker;
		HttpInvokeResult result;
		
		invoker = this.testObject.provide(ShopType.TMALL, null);
		result = invoker.invoke("http://detail.tmall.com/item.htm?id=42842072569");

		if (result.getException() != null) {
			result.getException().printStackTrace();
		}

		Assert.assertEquals(result.getStatusCode(), 200);
		
		Thread.sleep(3*1000);

		invoker = this.testObject.provide(ShopType.TMALL, null);
		result = invoker.invoke("http://item.taobao.com/item.htm?id=37842608697");

		if (result.getException() != null) {
			result.getException().printStackTrace();
		}

		Assert.assertEquals(result.getStatusCode(), 200);
		
		Thread.sleep(3*1000);

		invoker = this.testObject.provide(ShopType.TMALL, null);
		result = invoker.invoke("http://detail.tmall.com/item.htm?id=42817525975");

		if (result.getException() != null) {
			result.getException().printStackTrace();
		}

		Assert.assertEquals(result.getStatusCode(), 200);
		
		EasyMockUnitils.verify();
	}
}

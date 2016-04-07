/**
 * 
 */
package com.vdlm.spider.parser.shop.info;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseShopInfoBean;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.service.ShopService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:29:13 PM Jul 22, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoShopInfoParserTests {
	@TestedObject
	TaobaoShopInfoParser testObject;

	@Mock
	IpPools ipPools;

	ParseShopInfoBean bean;

	@Before
	public void before() {
		this.bean = new ParseShopInfoBean();
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setRequestUrl("http://shop113449814.taobao.com");
		this.bean.setRnd("");

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TaobaoShopInfoParser(provider, bean, new ShopService() {
			@Override
			public void save(Shop shop) {
			}
		});
	}

	@Test
	public void testParse_local() throws Exception {
		this.testObject.result = new HttpInvokeResult();

		final String file = this.getClass().getResource("/data/taobao.item.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse() throws Exception {
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		System.out.println(this.bean.getShopUrl());

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		this.bean.setRequestUrl("http://jialan167.taobao.com");
		
		final String ip = "42.121.58.26:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

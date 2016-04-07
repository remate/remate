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
public class TmallShopInfoParserTests {
	@TestedObject
	TmallShopInfoParser testObject;

	@Mock
	IpPools ipPools;

	ParseShopInfoBean bean;

	@Before
	public void before() {
		this.bean = new ParseShopInfoBean();
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TMALL);
		this.bean.setRequestUrl("http://nalanvb.taobao.com/");
		this.bean.setRnd("");

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TmallShopInfoParser(provider, bean, new ShopService() {
			@Override
			public void save(Shop shop) {
			}
		});
	}

	@Test
	public void testParse_local() throws Exception {
		this.testObject.result = new HttpInvokeResult();

		final String file = this.getClass().getResource("/data/tmall.item.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}
	
	@Test
	public void testParse_local2() throws Exception {
		this.testObject.result = new HttpInvokeResult();

		final String file = this.getClass().getResource("/data/tmall.shop.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse() throws Exception {
		final String ip = "182.254.130.85:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();
		EasyMock.expect(this.ipPools.pollingSafeAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		//222.73.215.30:8081,180.153.42.3:8081,121.199.23.237:8081,121.199.57.132:8081,121.199.57.112:8081
		final String ip = "121.199.57.132:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

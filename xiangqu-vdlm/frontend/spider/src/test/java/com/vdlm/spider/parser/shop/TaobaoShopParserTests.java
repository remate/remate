/**
 * 
 */
package com.vdlm.spider.parser.shop;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

import com.vdlm.spider.MockMemcachedClient;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.queue.TaskQueue;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:20:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoShopParserTests {

	@TestedObject
	TaobaoShopParser testObject;

	@Mock
	IpPools ipPools;

	ParseShopTaskBean bean;

	@Before
	public void before() throws Exception {
		new UrlCounters().setMemcachedSpiderClient(new MockMemcachedClient());
		
		this.bean = new ParseShopTaskBean();
		this.bean.setTaskId("000" + UUID.randomUUID().toString());
		this.bean.setOuerUserId("1s4ndo01");
		this.bean.setOuerShopId("aivp9tb2");
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setParserType(ParserType.TAOBAO_SEARCH);
		this.bean.setShopUrl("http://qclj.taobao.com");
		this.bean.setRequestUrl("http://qclj.taobao.com/search.htm");
		this.bean.setRefererUrl(null);

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TaobaoShopParser(provider, bean);

		final TaskQueues taskQueues = new TaskQueues();
		taskQueues.setParseItemTaskQueue(new TaskQueue() {
			@Override
			public boolean add(byte[] bytes) {
				return true;
			}
		});
		taskQueues.setParseShopTaskQueue(new TaskQueue() {
			@Override
			public boolean add(byte[] bytes) {
				return true;
			}
		});

	}

	@Test
	public void testParse_auth() throws Exception {
		final String file = this.getClass().getResource("/data/taobao.auth.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		Assert.assertTrue(this.testObject.isAuthHtml(htmlContent));

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_local() throws Exception {
		final String file = this.getClass().getResource("/data/taobao.shop.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse() throws Exception {
		this.bean.setOuerShopId("1qq6io");
		this.bean.setOuerUserId("5af509nf");
		this.bean.setShopUrl("http://goodonenight.taobao.com");
		this.bean.setRequestUrl("https://img.alicdn.com/imgextra/i1/708606639/TB2NySEdXXXXXXrXXXXXXXXXXXX_!!708606639.jpg");

		 final String ip = "120.26.49.122:8081";
//		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		// final String ip = "10.8.100.2:8622";
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

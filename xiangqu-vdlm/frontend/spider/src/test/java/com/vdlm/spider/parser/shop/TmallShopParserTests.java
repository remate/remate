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

import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.queue.TaskQueue;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:20:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TmallShopParserTests {

	@TestedObject
	TaobaoShopParser testObject;

	@Mock
	IpPools ipPools;

	ParseShopTaskBean bean;

	@Before
	public void before() {
		this.bean = new ParseShopTaskBean();
		this.bean.setTaskId("000" + UUID.randomUUID().toString());
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TMALL);
		this.bean.setParserType(ParserType.TMALL_SEARCH);
		this.bean.setShopUrl("http://thehistoryofwhoo.tmall.com");
		this.bean.setRequestUrl("http://thehistoryofwhoo.tmall.com/search.htm");
		this.bean.setRefererUrl(null);

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TmallShopParser(provider, bean);

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
		final String file = this.getClass().getResource("/data/tmall.auth.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		Assert.assertTrue(this.testObject.isAuthHtml(htmlContent));

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_local() throws Exception {
		final String file = this.getClass().getResource("/data/tmall.shop.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse() throws Exception {
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		final String ip = "106.186.112.112:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

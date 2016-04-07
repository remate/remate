/**
 * 
 */
package com.vdlm.spider.parser.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.MockMemcachedClient;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.cache.UrlCounters;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.queue.TaskQueue;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.utils.http.HttpInvokeResult;
import com.vdlm.utils.http.PoolingHttpClients;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:20:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoShopParserTests2 {

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
		this.bean.setOuerUserId("5af509nf");
		this.bean.setOuerShopId("1qq6io");
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setParserType(ParserType.TAOBAO_SEARCH);
		this.bean.setShopUrl("http://goodonenight.taobao.com");
		this.bean.setRequestUrl("http://goodonenight.taobao.com/search.htm?mid=w-2402525238-0&search=y&pageNo=7");
		this.bean.setRefererUrl(null);

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TaobaoShopParser(provider, bean);

		final TaskQueues taskQueues = new TaskQueues();
		taskQueues.setParseItemTaskQueue(new TaskQueue() {
			@Override
			public boolean add(byte[] bytes) {
				final ParseItemTaskBean bean = JSON.parseObject(bytes, ParseItemTaskBean.class);
				
				final Map<String, String> params = new HashMap<String, String>();
				params.put("ouerUserId", bean.getOuerUserId());
				params.put("ouerShopId", bean.getOuerShopId());
				params.put("url", bean.getItemUrl());

				final HttpInvokeResult result = PoolingHttpClients.post(
						"http://122.225.68.112:12080/moveItem/url/async", params);
				
				if (!result.isOK()) {
					System.err.println(result.toString());
					System.err.println(bean);
				}

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
	public void testParse_useProxy() throws Exception {
		// final String ip = "10.8.100.2:8622";
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

/**
 * 
 */
package com.vdlm.spider.parser.itemlist;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;

import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.bean.ParseShopTaskBean;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.parser.config.ItemListConfigs;
import com.vdlm.spider.queue.TaskQueue;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:01:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoItemListParserTests {

	private static final Logger LOG = LoggerFactory.getLogger(TaobaoItemListParserTests.class);

	@TestedObject
	TaobaoItemListParser testObject;

	@Mock
	IpPools ipPools;

	@Mock
	ItemService itemService;

	ParseShopTaskBean bean;

	HttpClientProvider provider;
	
	int index;

	@Before
	public void before() {
		this.bean = new ParseShopTaskBean();
		this.bean.setTaskId("000" + UUID.randomUUID().toString());
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setParserType(ParserType.TAOBAO_ITEMLIST);
		this.bean.setShopUrl("http://mixdream.taobao.com/");
		this.bean.setNickname("xmlapxmlap");
		this.bean.setRequestUrl(ItemListConfigs.getOrCreateTaobaoItemListConfig().getUrl0(this.bean.getNickname()));

		this.provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TaobaoItemListParser(provider, bean);

		final TaskQueues taskQueues = new TaskQueues();
		taskQueues.setParseItemTaskQueue(new TaskQueue() {
			@Override
			public boolean add(byte[] bytes) {
				LOG.debug(ParseItemTaskBean.parse(bytes).toString());
				System.out.println(++index);
				return true;
			}
		});
		taskQueues.setParseShopTaskQueue(new TaskQueue() {
			@Override
			public boolean add(byte[] bytes) {
				LOG.debug(ParseShopTaskBean.parse(bytes).toString());
				return true;
			}
		});
	}

	@Test
	public void testParse_JSON() throws Exception {
		final String file = this.getClass().getResource("/data/taobao.itemlist0.txt").getFile();

		final String jsonString = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(jsonString);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_EmptyItemList() throws Exception {
		final String file = this.getClass().getResource("/data/taobao.itemlist2.txt").getFile();

		final String jsonString = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(jsonString);

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse() throws Exception {
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		this.bean.setShopUrl("http://fever-hand.taobao.com");
		
		final String ip = "121.199.23.237:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

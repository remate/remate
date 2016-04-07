/**
 * 
 */
package com.vdlm.spider.parser.item;

import java.util.List;
import java.util.UUID;

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

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.ParserType;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.bean.ParseItemTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.IpPools;
import com.vdlm.spider.queue.TaskQueue;
import com.vdlm.spider.queue.TaskQueues;
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:01:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TmallItemParserTests2 {

	private static final Logger LOG = LoggerFactory.getLogger(TmallItemParserTests2.class);

	@TestedObject
	TmallItemParser testObject;

	@Mock
	IpPools ipPools;

	@Mock
	ItemService itemService;

	ParseItemTaskBean bean;

	@Before
	public void before() {
		this.bean = new ParseItemTaskBean();
		this.bean.setTaskId("001" + UUID.randomUUID().toString());
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TMALL);
		this.bean.setParserType(ParserType.TMALL_ITEM);
		this.bean.setItemUrl("http://detail.tmall.com/item.htm?id=36829985917");

		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TmallItemParser(provider, bean, itemService) {

			@Override
			protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("item >> {}", JSON.toJSONString(item));
					for (Sku sku : skus) {
						LOG.debug("sku >> {}", JSON.toJSONString(sku));
					}
					for (Img img : imgs) {
						LOG.debug("img >> {}", JSON.toJSONString(img));
					}
				}
			}
		};

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
	public void testParse() throws Exception {
		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
//		final String ip = "121.199.23.237:8081";
		final String ip = "211.144.72.153:80";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TMALL)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

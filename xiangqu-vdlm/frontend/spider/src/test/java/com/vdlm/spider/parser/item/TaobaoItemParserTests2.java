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
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:01:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoItemParserTests2 {

	private static final Logger LOG = LoggerFactory.getLogger(TaobaoItemParserTests2.class);

	@TestedObject
	TaobaoItemParser testObject;

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
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setParserType(ParserType.TAOBAO_ITEM);
		this.bean.setItemUrl("http://item.taobao.com/item.htm?id=36488294533");
		final HttpClientProvider provider = new HttpClientProvider();
		provider.setIpPools(this.ipPools);
		provider.init();

		this.testObject = new TaobaoItemParser(provider, bean, itemService) {
			@Override
			protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("item >> {}", JSON.toJSONString(item));

					if (skus == null) {
						LOG.debug("skus >> []");
					} else {
						for (Sku sku : skus) {
							LOG.debug("sku >> {}", JSON.toJSONString(sku));
						}
					}

					if (imgs == null) {
						LOG.debug("imgs >> []");
					} else {
						for (Img img : imgs) {
							LOG.debug("img >> {}", JSON.toJSONString(img));
						}
					}
				}
			}
		};
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
	public void testParse_soldOut() throws Exception {
		this.bean.setItemUrl("http://item.taobao.com/item.htm?id=12345667676");

		final String ip = "127.0.0.1";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}

	@Test
	public void testParse_useProxy() throws Exception {
		this.bean.setItemUrl("http://item.taobao.com/item.htm?id=43150048922");

		String[] ips = {"115.29.48.8",
				"120.25.202.110",
				"120.25.231.166",
				"120.25.233.162",
				"121.40.202.182",
				"121.41.86.163",
				"121.42.12.34",
				"123.57.69.88",
				"182.92.128.105",
				"182.92.172.142"};		// final String ip = "222.73.215.30:8081";
		// final String ip = "180.153.42.3:8081";
		 final String ip = "182.92.172.142:8081";

		EasyMock.expect(this.ipPools.getAvaliableIp(ShopType.TAOBAO)).andReturn(ip).anyTimes();

		EasyMockUnitils.replay();

		this.testObject.parse();

		EasyMockUnitils.verify();
	}
}

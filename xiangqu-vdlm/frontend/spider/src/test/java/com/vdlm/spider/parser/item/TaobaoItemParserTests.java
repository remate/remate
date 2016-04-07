/**
 * 
 */
package com.vdlm.spider.parser.item;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
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
import com.vdlm.spider.service.ItemService;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:01:49 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class TaobaoItemParserTests {

	private static final Logger LOG = LoggerFactory.getLogger(TaobaoItemParserTests.class);

	@TestedObject
	TaobaoItemParser testObject;

	@Mock
	HttpClientProvider provider;

	@Mock
	ItemService itemService;

	ParseItemTaskBean bean;

	@Before
	public void before() {
		this.bean = new ParseItemTaskBean();
		this.bean.setTaskId("000" + UUID.randomUUID().toString());
		this.bean.setOuerUserId("8rrn7z99");
		this.bean.setOuerShopId("1r5bid0x");
		this.bean.setShopType(ShopType.TAOBAO);
		this.bean.setParserType(ParserType.TAOBAO_ITEM);
		this.bean.setItemUrl("http://detail.tmall.com/item.htm?id=19907038456");

		this.testObject = new TaobaoItemParser(provider, bean, itemService) {

			@Override
			String getRemoteSkuInfo(String ajaxUrl) {
				try {
					final String file = this.getClass().getResource("/data/taobao.ajax.txt").getFile();
					return FileUtils.readFileToString(new File(file));
				}
				catch (IOException e) {
					return null;
				}
			}

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
	}

	@Test
	public void testParse() throws Exception {

		final String file = this.getClass().getResource("/data/taobao.item.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}
	
	@Test
	public void testParse2() throws Exception {

		final String file = this.getClass().getResource("/data/taobao.item2.html").getFile();

		final String htmlContent = FileUtils.readFileToString(new File(file));

		EasyMockUnitils.replay();

		this.testObject.parse(htmlContent);

		EasyMockUnitils.verify();
	}
}

/**
 * 
 */
package com.vdlm.spider.dao;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.entity.Item;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:52:12 PM Jul 21, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({ "classpath:spring-dao.xml" })
@Transactional(TransactionMode.ROLLBACK)
public class ItemDaoTests {

	@TestedObject
	ItemDao dao;

	@SpringBeanByType
	DataSource dataSource;

	@Before
	public void before() {
		this.dao = new ItemDao();
		this.dao.setDataSource(dataSource);
	}

	@Test
	public void testInsert() {
		final Item entity = new Item();
		entity.setOuerUserId("1");
		entity.setOuerShopId("2");
		entity.setName("海青蓝2014夏装新款欧美大牌修身中长裙百褶裙大摆裙蕾丝连衣裙女");
		entity.setPrice(1.0);
		entity.setAmount(1);
		entity.setItemUrl("http://detail.tmall.com/item.htm?id=19907038456");
		entity.setShopType(ShopType.TAOBAO);
		entity.setReqFrom(ReqFrom.KKKD);

		EasyMockUnitils.replay();

		//entity.setId(this.dao.insert(entity));

		Assert.assertNotNull(entity.getId());

		EasyMockUnitils.verify();
	}

	@Test
	public void testUpdate() {
		final Item entity = new Item();
		entity.setOuerUserId("1");
		entity.setOuerShopId("2");
		entity.setName("海青蓝2014夏装新款欧美大牌修身中长裙百褶裙大摆裙蕾丝连衣裙女");
		entity.setPrice(1.0);
		entity.setAmount(1);
		entity.setItemUrl("http://detail.tmall.com/item.htm?id=19907038456");
		entity.setShopType(ShopType.TAOBAO);
		entity.setReqFrom(ReqFrom.KKKD);

		EasyMockUnitils.replay();

		//entity.setId(this.dao.insert(entity));

		Assert.assertNotNull(entity.getId());

		//Assert.assertTrue(this.dao.update(entity) > 0);

		EasyMockUnitils.verify();
	}
}

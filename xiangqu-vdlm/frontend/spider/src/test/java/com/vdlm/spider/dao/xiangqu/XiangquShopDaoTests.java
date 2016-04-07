/**
 * 
 */
package com.vdlm.spider.dao.xiangqu;

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
import com.vdlm.spider.entity.Shop;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:40:44 AM Aug 8, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({ "classpath:spring-dao-xiangqu.xml" })
@Transactional(TransactionMode.ROLLBACK)
public class XiangquShopDaoTests {

	@TestedObject
	XiangquShopDao dao;

	@SpringBeanByType
	DataSource dataSource;

	@Before
	public void before() {
		this.dao = new XiangquShopDao();
		this.dao.setDataSource(dataSource);
	}

	@Test
	public void testInsert() {
		final Shop entity = new Shop();
		entity.setReqFrom(ReqFrom.XIANGQU);
		entity.setName("Catworld台湾馆女装");
		entity.setNickname("obuy_catwalk");
		entity.setShopType(ShopType.TAOBAO);
		entity.setUserId("1");
		entity.setShopId("2");

		EasyMockUnitils.replay();

		Assert.assertTrue(this.dao.insert(entity) > 0);

		EasyMockUnitils.verify();
	}

	@Test
	public void testUpdate() {
		final Shop entity = new Shop();
		entity.setReqFrom(ReqFrom.XIANGQU);
		entity.setName("Catworld台湾馆女装");
		entity.setNickname("obuy_catwalk");
		entity.setShopType(ShopType.TAOBAO);
		entity.setUserId("1");
		entity.setShopId("2");

		EasyMockUnitils.replay();

		Assert.assertTrue(this.dao.insert(entity) > 0);

		Assert.assertTrue(this.dao.update(entity) > 0);

		EasyMockUnitils.verify();
	}
}

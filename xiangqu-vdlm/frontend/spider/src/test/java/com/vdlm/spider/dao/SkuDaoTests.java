/**
 * 
 */
package com.vdlm.spider.dao;

import java.util.ArrayList;
import java.util.List;

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

import com.vdlm.spider.entity.Sku;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:52:12 PM Jul 21, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({ "classpath:spring-dao.xml" })
@Transactional(TransactionMode.ROLLBACK)
public class SkuDaoTests {

	@TestedObject
	SkuDao dao;

	@SpringBeanByType
	DataSource dataSource;

	@Before
	public void before() {
		this.dao = new SkuDao();
		this.dao.setDataSource(dataSource);
	}

	@Test
	public void testInsert() {
		final Sku entity = new Sku();
		entity.setSpec("123");
		entity.setOrigSpec("123");
		entity.setPrice(1.0);
		entity.setAmount(1);
		entity.setItemId(1L);

		EasyMockUnitils.replay();

		Assert.assertTrue(this.dao.insert(entity) > 0);

		EasyMockUnitils.verify();
	}

	@Test
	public void testDeleteList() {
		final List<Sku> entities = new ArrayList<Sku>(10);
		for (int i = 0; i < 10; i++) {
			final Sku entity = new Sku();
			entity.setSpec("123");
			entity.setOrigSpec("123");
			entity.setPrice(1.0);
			entity.setAmount(1);
			entity.setItemId(1L);

			entities.add(entity);
		}

		EasyMockUnitils.replay();

		this.dao.insert(entities);

		Assert.assertTrue(this.dao.deleteList(1L) > 0);

		EasyMockUnitils.verify();
	}
}

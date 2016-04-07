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

import com.vdlm.spider.entity.Img;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:52:12 PM Jul 21, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({ "classpath:spring-dao.xml" })
@Transactional(TransactionMode.ROLLBACK)
public class ImgDaoTests {

	@TestedObject
	ImgDao dao;

	@SpringBeanByType
	DataSource dataSource;

	@Before
	public void before() {
		this.dao = new ImgDao();
		this.dao.setDataSource(dataSource);
	}

	@Test
	public void testInsert() {
		final Img entity = new Img();
		entity.setImg("qn|xaya|Ftbkh2DxiGlDLQ68iCjh9nywZNdv");
		entity.setItemId(1L);
		entity.setType(1);
		entity.setOrderNum(1);

		EasyMockUnitils.replay();

		Assert.assertTrue(this.dao.insert(entity) > 0);

		EasyMockUnitils.verify();
	}

	@Test
	public void testDeleteList() {
		final List<Img> entities = new ArrayList<Img>(10);
		for (int i = 0; i < 10; i++) {
			final Img entity = new Img();
			entity.setImg("qn|xaya|Ftbkh2DxiGlDLQ68iCjh9nywZNdv");
			entity.setItemId(1L);
			entity.setType(1);
			entity.setOrderNum(i + 1);

			entities.add(entity);
		}

		EasyMockUnitils.replay();

		this.dao.insert(entities);

		Assert.assertTrue(this.dao.deleteList(1L) > 0);

		EasyMockUnitils.verify();
	}
}

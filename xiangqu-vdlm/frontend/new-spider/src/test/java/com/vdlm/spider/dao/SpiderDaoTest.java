package com.vdlm.spider.dao;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.vdlm.config.ApplicationConfig;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.entity.HttpRequestError;
import com.vdlm.spider.entity.ItemListProcess;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.proxy.Proxy;
import com.vdlm.spider.proxy.ProxyStatus;

/**
 *
 * @author: chenxi
 */

@ActiveProfiles("dev")
@ContextConfiguration(classes = { SpiderDaoConfig.class, ApplicationConfig.class })
public class SpiderDaoTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	@Autowired
	private DescDao descDao;
	@Autowired
	private ImgDao imgDao;
	@Autowired
	private ItemListProcessDao itemListProcessDao;
	@Autowired
	private HttpRequestErrorDao httpRequestErrorDao;
	@Autowired
	private ProxyDao proxyDao;
	
	@Test
	public void testItemDao() {
//		System.out.println(SpringContextUtil.getBean("itemTaskStrategy").toString());
		System.out.println(itemDao.exist(1L));
		System.out.println(itemDao.exist("59f7tkv7", "36799263376"));
		System.out.println(itemDao.exist("ajuxo9zy", "36799263376"));
	}
	
	@Test
	public void testItemProcessDao() {
//		itemProcessDao.setDescParsed(10L);
//		final Long shopId = itemProcessDao.queryShopId("xxx","36822932376");
//		System.out.println(shopId);
		ItemProcess ip = itemProcessDao.queryOneItemProcess(2096L);
		System.out.println(ip);
		ip = itemProcessDao.queryOneItemProcess(999999L);
		System.out.println(ip);
	}
	
	@Test
	public void testDescDao() {
		final Desc desc = descDao.queryOneDesc(118L);
		System.out.println(desc);
	}
	
	@Test
	public void testImgDao() {
		System.out.println(imgDao.queryImgCount(11L, 3));
	}
	
	@Test
	public void testItemListProcessDao() {
		ItemListProcess ilp = itemListProcessDao.queryOneItemProcess(32L);
		System.out.println(ilp);
		ilp = itemListProcessDao.queryOneItemProcess(33L);
		System.out.println(ilp);
//		itemListProcessDao.decItemCount(2093L, SpideItemType.GROUP_IMG);
	}
	
	@Test
	public void testHttpRequestErrorDao() {
		final HttpRequestError error = new HttpRequestError();
		error.setStatusCode(404);
		error.setUrl("http://mock.test.com/tttt.jpg");
		httpRequestErrorDao.insert(error);
	}
	
	@Test
	public void testProxyDao() throws Exception {
		final Proxy proxy = new Proxy();
		proxy.setIp("test");
		proxy.setPort(7777);
		proxy.setSource("test");
		proxy.setCheckTime(new Date());
		proxy.setType("test");
		proxy.setCurrentPage(0);
		proxy.setStatus(ProxyStatus.NEW);
		if (proxyDao.exist(proxy) == null) {
			proxyDao.insert(proxy);
		}
		else {
			proxyDao.update(proxy);
		}
	}
}

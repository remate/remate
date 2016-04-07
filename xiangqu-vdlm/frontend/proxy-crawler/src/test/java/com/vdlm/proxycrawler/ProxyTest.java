package com.vdlm.proxycrawler;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.vdlm.config.ApplicationConfig;
import com.vdlm.proxycrawler.third.XiciHomeProxyCrawler;

/**
 *
 * @author: chenxi
 */

@ActiveProfiles("dev")
@ContextConfiguration(classes = { ProxyConfig.class, ApplicationConfig.class })
public class ProxyTest extends AbstractJUnit4SpringContextTests {

//	@Autowired
//	XiciNnProxyCrawler xici;
//	
//	@Autowired
//	Daili5566ProxyCrawler daili5566;
	
	@Autowired
	XiciHomeProxyCrawler xiciHome;
	
	@Test
	public void testXiciHomeCrawlProxy() throws Exception {
		xiciHome.crawl(1);
	}
	
//	@Test
//	public void testXiciCrawlProxy() throws Exception {
//		xici.crawl(1);
//	}
	
//	@Test
//	public void testDaili5566CrawlProxy() throws Exception {
//		daili5566.crawl(1);
//	}
}

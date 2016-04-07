/**
 * 
 */
package com.vdlm.spider.cache;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vdlm.spider.ReqFrom;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:18:16 AM Sep 4, 2014
 */
public class UrlCountersTest {

	MemcachedClient client;

	@Before
	public void before() throws Exception {
		//		final String address = "42.121.18.234:11211"; //xiangqu online
		final String address = "10.8.100.2:11211"; //kkkd dev
		//		final String address = "122.225.68.112:11211"; //kkkd online
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address));
		client = builder.build();

		new UrlCounters().setMemcachedSpiderClient(client);
	}

	@After
	public void after() throws IOException {
		client.shutdown();
	}

	@Test
	public void testGetKey() throws Exception {
		final String key = UrlCounters.getKey(ReqFrom.XIANGQU, "0", "0", "http://jialan167.taobao.com");

		System.out.println(key);

		final String md5 = DigestUtils.md5Hex(key);

		System.out.println(md5);

		System.out.println(client.get(md5));
	}

	@Test
	public void testAdd() throws Exception {
		System.out.println(UrlCounters.add(ReqFrom.XIANGQU, "0", "0", "http://jialan167.taobao.com"));
	}
}

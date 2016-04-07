/**
 * 
 */
package com.vdlm.spider.cache;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:48:33 PM Jul 28, 2014
 */
public class CacheTests {

	MemcachedClient client;

	@Before
	public void before() throws IOException {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("10.8.100.2:11211"));
		client = builder.build();
	}

	@After
	public void after() throws IOException {
		client.shutdown();
	}

	@Test
	public void testDelete() throws TimeoutException, InterruptedException, MemcachedException {
		System.out.println(client.delete("07b6b33b360e47e1a034a5a72607318a"));
	}

	@Test
	public void testIncr() throws TimeoutException, InterruptedException, MemcachedException {
		final int timeout = 24 * 60 * 60;

		System.out.println(client.incr("abc", 1, 0, 5000, timeout));
	}
}

/**
 * 
 */
package com.vdlm.spider.http;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:45:21 PM Jul 19, 2014
 */
//@Ignore
public class IpPoolsTests {

	MemcachedClient memcachedClient;
	IpPools ipPools;

	@Before
	public void before() throws Exception {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("10.8.100.2:11211"));
		this.memcachedClient = builder.build();

		this.ipPools = new IpPools() {
			@Override
			int getDisableIpExp() {
				return 60;
			}
		};
		this.ipPools.setMemcachedSpiderClient(memcachedClient);
		this.ipPools.init();
	}
	
	@Test
	public void testInit() {
		
	}

	@After
	public void after() throws Exception {
		this.memcachedClient.shutdown();
		this.ipPools.destroy();
	}
	
	@Test
	public void testMergeList() {
		
	}

	@Test
	public void testDisableIp() throws Exception {
		final ShopType shopType = ShopType.TAOBAO;
		final String ip = "121.199.23.237:8081";

		System.out.println(this.memcachedClient.delete(this.ipPools.getDisableIpKey(ip)));

		this.ipPools.disableIp(shopType, ip);

		Assert.assertTrue(this.ipPools.isDisableIp(ip));

		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				ipPools.disableIp(ShopType.TAOBAO, ipPools.pollingSafeAvaliableIp(ShopType.TAOBAO));
			}
		}, 3, 50, TimeUnit.SECONDS);

		TimeUnit.SECONDS.sleep(3000);
	}

}

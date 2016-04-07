/**
 * 
 */
package com.vdlm.spider.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:48:33 PM Jul 28, 2014
 */
public class CacheTests2 {

	MemcachedClient client;

	@Before
	public void before() throws Exception {
		final String address = "10.18.10.7:11311";
//		final String address = "10.8.100.2:11211";
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address));
		client = builder.build();
	}

	@After
	public void after() throws IOException {
		client.shutdown();
	}

	@Test
	public void testGet() throws Exception {
		Object obj = client.get("UserDAOImpl_137070");
		if (obj == null) {
			System.out.println("No Data");
		}
		else {
			System.out.println(JSON.toJSONString(obj));
		}

		System.out.println(client.delete("UserDAOImpl_137070"));
	}

	Set<String> getIds(String path) throws Exception {
		Set<String> results = new HashSet<String>();

		File file = new File(path);

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));

			while (true) {
				final String line = br.readLine();
				if (line == null) {
					break;
				}
				results.add(line.trim());
			}
		}
		finally {
			IOUtils.closeQuietly(br);
		}

		return results;
	}
	
	@Test
	public void testDeleteIps() throws Exception {
		Set<String> ids = this.getIds("C:\\Users\\Think\\Desktop\\ip.txt");

		for (String id : ids) {
			System.out.println(client.delete("DisableIp_" + id));
		}
		
		//update user set user_type=8 where id IN ();

		System.out.println(JSON.toJSONString(ids));
	}

	@Test
	public void testDeleteUser() throws Exception {
		Set<String> ids = this.getIds("C:\\Users\\Think\\Desktop\\userId.txt");

		for (String id : ids) {
			System.out.println(client.delete("UserDAOImpl_" + id));
		}
		
		//update user set user_type=8 where id IN ();

		System.out.println(JSON.toJSONString(ids));
	}

	@Test
	public void testDeleteProduct() throws Exception {
		//		select * from user where id=96753;
		//
		//		select * from product where user_id=96753 and status <> 3;
		//
		//		update product set status=3 where user_id=96753;
		
		// update product set status=3 WHERE `status`=0 and title like '%美女上门%';

		Set<String> ids = this.getIds("C:\\Users\\Think\\Desktop\\productId.txt");

		for (String id : ids) {
			System.out.println(client.delete("ProductDAOImpl_" + id));
		}
	}
}

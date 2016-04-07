package com.vdlm.spider.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CookieConfTests {

	IpPools ipPools;
	CookieConf cookieConf;

	@Before
	public void before() {
		this.cookieConf = new CookieConf();
		try {
			this.cookieConf.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAvailable() {
		Assert.assertTrue(cookieConf.available());
	}
	
	@Test
	public void testLoad() throws InterruptedException, IOException {
		Assert.assertTrue(cookieConf.reload());
	}

	@Test
	public void testSave() throws InterruptedException, IOException {
		
		List<Cookie> oths1 = cookieConf.getCookie("192.168.1.1");
		System.out.println(oths1.get(0).getK() + ":" + oths1.get(0).getV());
		
		List<Cookie> dd = cookieConf.getCookie(null);
		System.out.println(oths1.get(0).getK() + ":" + oths1.get(0).getV());
		
		List<Cookie> results = new ArrayList<Cookie>();
		results.add(new Cookie("_tb_token_", "ccccc"));
		results.add(new Cookie("cna", "y52vDNkJbnwCAXPsC2K3ZC0B"));
		results.add(new Cookie("cookie2", "1c9fccf7b0b0b9a4860500d737eec1ed"));
		results.add(new Cookie("isg", "61E9512065B93E2EA9DE4C1F6D43AB88"));
		results.add(new Cookie("mt", "ci%3D-1_0"));
		results.add(new Cookie("t", "0489aef306d16d298a6e57717cfe5f2a"));
		results.add(new Cookie("v", "0"));
		Assert.assertTrue(cookieConf.store(results, "192.168.1.1"));
		Assert.assertTrue(cookieConf.store(results, null));
		
		List<Cookie> def = cookieConf.getCookie(null);
		System.out.println(def.get(0).getK() + ":" + def.get(0).getV());
		
		List<Cookie> conf = cookieConf.getCookie("192.168.1.1");
		System.out.println(conf.get(0).getK() + ":" + conf.get(0).getV());
		
	}
	
	@Test
	public void testGet() throws InterruptedException {
		List<Cookie> defs = cookieConf.getCookie(null);
		Assert.assertNotNull(defs);
		
		System.out.println("############");
		List<Cookie> oths = cookieConf.getCookie("127.0.0.1");
		System.out.println(oths.get(0).getK() + ":" + oths.get(0).getV());
		
		List<Cookie> oths1 = cookieConf.getCookie("192.168.1.1");
		System.out.println(oths1.get(0).getK() + ":" + oths1.get(0).getV());
		
		List<Cookie> oths2 = cookieConf.getCookie("10.8.100.116");
		System.out.println(oths2.get(0).getK() + ":" + oths2.get(0).getV());
		System.out.println("############");
		
		//System.out.println(JSON.toJSONString(oths));
		System.out.println("-----------");
	}
	
}

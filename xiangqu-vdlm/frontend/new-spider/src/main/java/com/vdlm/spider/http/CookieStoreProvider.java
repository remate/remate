/**
 * 
 */
package com.vdlm.spider.http;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * <pre>
 *
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:06:47 PM May 5, 2015
 */
public class CookieStoreProvider {

	ConcurrentHashMap<String, CookieStore> cookieStores = new ConcurrentHashMap<String, CookieStore>();

	/**
	 * 每个ip分配一个
	 * @param ip
	 * @return
	 */
	public CookieStore provide(String ip) {
		CookieStore result = new BasicCookieStore();

		CookieStore old = cookieStores.putIfAbsent(ip, result);
		if (old != null) {
			return old;
		}

		return result;
	}
}

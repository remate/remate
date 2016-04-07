/**
 * 
 */
package com.vdlm.spider.cache;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.spider.Config;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 6:39:30 PM Jul 20, 2014
 */
public class UrlCounters {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private static MemcachedClient memcachedSpiderClient;

	@Autowired
	public void setMemcachedSpiderClient(MemcachedClient memcachedSpiderClient) {
		UrlCounters.memcachedSpiderClient = memcachedSpiderClient;
		log.warn("memcaced address:{}",memcachedSpiderClient.getServersDescription());
	}

	static String KEY_FORMAT = "rf_%s_uid_%s_sid_%s_url_%s";

	static String getKey(ReqFrom reqFrom, String userId, String shopId, String url) {
		return String.format(KEY_FORMAT, reqFrom, userId, shopId, url);
	}

	public static boolean add(ReqFrom reqFrom, String userId, String shopId, String url) {
		return add(reqFrom, userId, shopId, url, Config.instance().getCacheTimeout());
	}

	public static boolean add(ReqFrom reqFrom, String userId, String shopId, String url, long timeout) {
		final String key = getKey(reqFrom, userId, shopId, url);
		// md5节省空间
		final String md5 = DigestUtils.md5Hex(key);
		try {
			return memcachedSpiderClient.incr(md5, 1, 0, Config.instance().getCacheOptTimeout(), (int) (timeout / 1000)) == 0;
		}
		catch (Exception e) {
			//TODO 异常情况放弃?
			Logs.unpredictableLogger.error("Error to find by [" + key + "] from Memcached", e);
			return false;
		}
	}
	
	public static boolean exists(ReqFrom reqFrom, String userId, String shopId, String url) {
		final String key = getKey(reqFrom, userId, shopId, url);
		// md5节省空间
		final String md5 = DigestUtils.md5Hex(key);
		try {
			return memcachedSpiderClient.get(md5) != null;
		}
		catch (Exception e) {
			//TODO 异常情况放弃?
			Logs.unpredictableLogger.error("Error to find by [" + key + "] from Memcached", e);
			return false;
		}
	}
	
	public static boolean delete(ReqFrom reqFrom, String userId, String shopId, String url) {
		return delete(reqFrom, userId, shopId, url, Config.instance().getCacheTimeout());
	}

	public static boolean delete(ReqFrom reqFrom, String userId, String shopId, String url, long timeout) {
		final String key = getKey(reqFrom, userId, shopId, url);
		// md5节省空间
		final String md5 = DigestUtils.md5Hex(key);
		try {
			return memcachedSpiderClient.delete(md5);
		}
		catch (Exception e) {
			//TODO 异常情况放弃?
			Logs.unpredictableLogger.error("Error to find by [" + key + "] from Memcached", e);
			return false;
		}
	}
}

/**
 * 
 */
package com.vdlm.spider.cache;

import java.util.concurrent.ConcurrentHashMap;

import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:09:55 PM Jul 30, 2014
 */
public class TaskCounter {

	public final ConcurrentHashMap<String, Entry> increments = new ConcurrentHashMap<String, Entry>();
	public final ConcurrentHashMap<String, Entry> decrements = new ConcurrentHashMap<String, Entry>();
	public final ConcurrentHashMap<String/* taskId */, PubInfo> pubs = new ConcurrentHashMap<String, PubInfo>();

	public static class Entry {
		long createTimeMillis = System.currentTimeMillis();
		long lastModifiedTimeMillis = System.currentTimeMillis();
		int index = 0;

		int incrementAndGet() {
			this.lastModifiedTimeMillis = System.currentTimeMillis();
			return ++index;
		}

		int decrementAndGet() {
			this.lastModifiedTimeMillis = System.currentTimeMillis();
			return --index;
		}

		public long getCreateTimeMillis() {
			return createTimeMillis;
		}

		public long getLastModifiedTimeMillis() {
			return lastModifiedTimeMillis;
		}

		public int get() {
			return index;
		}

	}

	public static class PubInfo {
		String phone;
		String userId;
		ShopType shopType;
		DeviceType deviceType;

		public String getPhone() {
			return phone;
		}

		public String getUserId() {
			return userId;
		}

		public ShopType getShopType() {
			return shopType;
		}

		public DeviceType getDeviceType() {
			return deviceType;
		}
	}

	public void init(String taskId, String phone, String userId, ShopType shopType, DeviceType deviceType) {
		increments.put(taskId, new Entry());
		decrements.put(taskId, new Entry());

		final PubInfo info = new PubInfo();
		info.phone = phone;
		info.userId = userId;
		info.shopType = shopType;
		info.deviceType = deviceType;
		pubs.put(taskId, info);
	}

	public int incrementAndGet(String taskId) {
		Entry entry = new Entry();
		Entry old = increments.putIfAbsent(taskId, entry);
		if (old != null) {
			entry = old;
		}
		return entry.incrementAndGet();
	}

	public int decrementAndGet(String taskId) {
		Entry entry = new Entry();
		Entry old = decrements.putIfAbsent(taskId, entry);
		if (old != null) {
			entry = old;
		}
		return entry.decrementAndGet();
	}

	public void remove(String taskId) {
		increments.remove(taskId);
		decrements.remove(taskId);
		pubs.remove(taskId);
	}
}

/**
 * 
 */
package com.vdlm.spider.cache;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.vdlm.spider.DeviceType;
import com.vdlm.spider.ShopType;
import com.vdlm.utils.ThreadLocalRandom;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:00:58 PM Jul 19, 2014
 */
public class TaskCounters {

	static final int index_len = 3;
	static final int size = 10;
	static final ArrayList<TaskCounter> counters = new ArrayList<TaskCounter>(size);
	static {
		for (int i = 0; i < size; i++) {
			counters.add(new TaskCounter());
		}
	}

	public static int size() {
		return size;
	}

	public static ArrayList<TaskCounter> getCounters() {
		return counters;
	}

	public static String init(String phone, String userId, ShopType shopType, DeviceType deviceType) {
		final int index = ThreadLocalRandom.current().nextInt(size);

		final String taskId = StringUtils.leftPad(String.valueOf(index), index_len, '0') + UUID.randomUUID().toString();

		counters.get(index).init(taskId, phone, userId, shopType, deviceType);

		return taskId;
	}

	public static int incrementAndGet(String taskId) {
		if (StringUtils.isBlank(taskId)) {
			return -1;
		}

		final int index = Integer.parseInt(taskId.substring(0, index_len));

		return counters.get(index).incrementAndGet(taskId);
	}

	public static int decrementAndGet(String taskId) {
		if (StringUtils.isBlank(taskId)) {
			return -1;
		}

		final int index = Integer.parseInt(taskId.substring(0, index_len));

		return counters.get(index).decrementAndGet(taskId);
	}
}

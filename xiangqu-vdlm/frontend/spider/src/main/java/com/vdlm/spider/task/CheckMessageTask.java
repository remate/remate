/**
 * 
 */
package com.vdlm.spider.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.Config;
import com.vdlm.spider.bean.PushMessageTaskBean;
import com.vdlm.spider.bean.SmsMessageTaskBean;
import com.vdlm.spider.cache.TaskCounter;
import com.vdlm.spider.cache.TaskCounters;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.parser.config.ShopConfig;
import com.vdlm.spider.parser.config.ShopConfigs;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:07:49 PM Jul 24, 2014
 */
public class CheckMessageTask extends AbstractTask {

	@Override
	public int getCorePoolSize() {
		return TaskCounters.size();
	}

	@Override
	ThreadFactory getThreadFactory() {
		return new NamedThreadFactory("CheckMessageTask");
	}

	@Override
	void start() {
		for (int i = 0; i < this.getCorePoolSize(); i++) {
			final int index = i;

			this.getService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					try {
						CheckMessageTask.this.start0(index);
					}
					catch (Exception e) {
						log.error("unpredictable exception", e);
					}
				}
			}, this.getFixedDelay() + i * 2000, this.getFixedDelay(), TimeUnit.MILLISECONDS);
		}
	}

	void start0(int index) {
		final TaskCounter taskCounter = TaskCounters.getCounters().get(index);

		final long timeout = Config.instance().getNotifyThreshold();
		final long currentTimeMillis = System.currentTimeMillis();
		final List<String> removes = new LinkedList<String>();
		final List<TaskCounter.PubInfo> pubs = new LinkedList<TaskCounter.PubInfo>();
		for (Map.Entry<String, TaskCounter.Entry> entry : taskCounter.increments.entrySet()) {
			if ((currentTimeMillis - entry.getValue().getLastModifiedTimeMillis()) > timeout
					&& entry.getValue().get() != 0 && taskCounter.decrements.get(entry.getKey()).get() != 0
					&& (entry.getValue().get() + taskCounter.decrements.get(entry.getKey()).get()) <= 0) {

				final TaskCounter.PubInfo pub = taskCounter.pubs.get(entry.getKey());

				if (log.isDebugEnabled()) {
					log.debug("notify {}, increment: {}, decrement: {}", JSON.toJSONString(pub),
							JSON.toJSONString(entry.getValue()),
							JSON.toJSONString(taskCounter.decrements.get(entry.getKey())));
				}

				pubs.add(pub);
				removes.add(entry.getKey());
			}
			else if ((currentTimeMillis - entry.getValue().getCreateTimeMillis()) > Config.instance().getCacheTimeout()) {
				removes.add(entry.getKey());
			}
		}

		for (String key : removes) {
			taskCounter.remove(key);
		}

		for (TaskCounter.PubInfo pub : pubs) {
			if (StringUtils.isBlank(pub.getPhone())) {
				continue;
			}
			final ShopConfig config = ShopConfigs.getOrCreateShopConfig(pub.getShopType());
			if (config == null) {
				continue;
			}

			if (StringUtils.isNotBlank(config.getSmsTpl())) {
				try {
					final SmsMessageTaskBean msg = new SmsMessageTaskBean();
					msg.setPhone(pub.getPhone());
					msg.setContent(config.getSmsTpl());
					TaskQueues.getSmsMessageTaskQueue().add(msg.toJSONBytes());
				}
				catch (Exception ignore) {
				}
			}

			if (StringUtils.isNotBlank(config.getPushTplTitle()) || StringUtils.isNotBlank(config.getPushTplContent())) {
				try {
					final PushMessageTaskBean msg = new PushMessageTaskBean();
					msg.setTitle(config.getPushTplTitle());
					msg.setImg(config.getPushTplImg());
					msg.setContent(config.getPushTplContent());
					msg.setDeviceType(pub.getDeviceType());
					msg.setUserId(pub.getUserId());
					TaskQueues.getPushMessageTaskQueue().add(msg.toJSONBytes());
				}
				catch (Exception ignore) {
				}
			}
		}
	}
}

/**
 * 
 */
package com.vdlm.spider.task;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.dal.status.PushMessageStatus;
import com.vdlm.dal.type.PushMessageDeviceType;
import com.vdlm.dal.type.PushMessageType;
import com.vdlm.service.push.BaiduPushMessage;
import com.vdlm.service.push.PushService;
import com.vdlm.spider.bean.PushMessageTaskBean;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:07:49 PM Jul 24, 2014
 */
public class NotifyPushMessageTask extends AbstractTask {

	private PushService pushService;

	@Autowired
	public void setPushService(PushService pushService) {
		this.pushService = pushService;
	}

	void notifyPushMessage(PushMessageTaskBean bean) {
		final BaiduPushMessage message = new BaiduPushMessage();
		message.setType(PushMessageType.MESSAGE);
		message.setBaiduTagName(bean.getUserId());
		message.setUserId(bean.getUserId());
		message.setTitle(bean.getTitle());
		message.setDesc(bean.getContent());
		message.setCount(0);
		message.setStatus(PushMessageStatus.FAIL);
		message.setImageUrl(bean.getImg());

		switch (bean.getDeviceType()) {
		case IOS:
			message.setDeviceType(PushMessageDeviceType.IOS);
			break;
		case ANDROID:
			message.setDeviceType(PushMessageDeviceType.ANDROID);
			break;
		default:
			log.warn("ignore PushMessahe:" + bean);
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("send PushMessage:" + bean);
		}

		this.pushService.sendTagMessageByBaidu(message);
	}

	@Override
	void start() {
		for (int i = 0; i < this.getCorePoolSize(); i++) {
			this.getService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					try {
						NotifyPushMessageTask.this.start0();
					}
					catch (Exception e) {
						log.error("unpredictable exception", e);
					}
				}
			}, this.getFixedDelay() + i * 2000, this.getFixedDelay(), TimeUnit.MILLISECONDS);
		}
	}

	void start0() {
		byte[] bytes = null;
		PushMessageTaskBean bean = null;
		while (true) {
			try {
				bytes = TaskQueues.getPushMessageTaskQueue().take();
			}
			catch (Exception e) {
				break;
			}

			if (bytes == null) {
				break;
			}

			try {
				bean = PushMessageTaskBean.parse(bytes);
			}
			catch (Exception e) {
				log.error("Error to parse JSONString to PushMessageTaskBean: " + new String(bytes), e);
				break;
			}

			try {
				this.notifyPushMessage(bean);
			}
			catch (Exception e) {
				log.error("Error to notify PushMessage:" + bean.toString(), e);
				break;
			}
		}
	}
}

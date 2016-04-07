/**
 * 
 */
package com.vdlm.spider.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.dal.model.SmsMessage;
import com.vdlm.dal.status.SmsMessageStatus;
import com.vdlm.dal.type.SmsMessageType;
import com.vdlm.service.msg.SmsMessageService;
import com.vdlm.spider.bean.SmsMessageTaskBean;
import com.vdlm.spider.core.NamedThreadFactory;
import com.vdlm.spider.queue.TaskQueues;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:07:49 PM Jul 24, 2014
 */
public class NotifySmsMessageTask extends AbstractTask {

	private SmsMessageService smsMessageService;

	@Autowired
	public void setSmsMessageService(SmsMessageService smsMessageService) {
		this.smsMessageService = smsMessageService;
	}

	@Override
	ThreadFactory getThreadFactory() {
		return new NamedThreadFactory("NotifySmsMessageTask");
	}

	void notifySmsMessage(SmsMessageTaskBean bean) {
		final SmsMessage message = new SmsMessage();
		message.setPhone(bean.getPhone());
		message.setContent(bean.getContent());
		message.setType(SmsMessageType.MOVE_PRODUCT);
		message.setStatus(SmsMessageStatus.FAIL);
		message.setCount(0);

		if (log.isDebugEnabled()) {
			log.debug("send SmsMessage:" + bean);
		}

		this.smsMessageService.insert(message);
	}

	@Override
	void start() {
		for (int i = 0; i < this.getCorePoolSize(); i++) {
			this.getService().scheduleWithFixedDelay(new Runnable() {

				@Override
				public void run() {
					try {
						NotifySmsMessageTask.this.start0();
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
		SmsMessageTaskBean bean = null;
		while (true) {
			try {
				bytes = TaskQueues.getSmsMessageTaskQueue().take();
			}
			catch (Exception e) {
				break;
			}

			if (bytes == null) {
				break;
			}

			try {
				bean = SmsMessageTaskBean.parse(bytes);
			}
			catch (Exception e) {
				log.error("Error to parse JSONString to SmsMessageTaskBean: " + new String(bytes), e);
				break;
			}

			try {
				this.notifySmsMessage(bean);
			}
			catch (Exception e) {
				log.error("Error to notify SmsMessage:" + bean.toString(), e);
				break;
			}
		}
	}
}

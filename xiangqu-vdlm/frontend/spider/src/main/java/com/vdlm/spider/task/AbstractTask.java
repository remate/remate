/**
 * 
 */
package com.vdlm.spider.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.spider.Config;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:37:24 PM Jul 20, 2014
 */
public abstract class AbstractTask {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private int corePoolSize = Runtime.getRuntime().availableProcessors();

	private int fixedDelay = (int)Config.instance().getTaskDelayTime();

	private ScheduledExecutorService service;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		if (this.corePoolSize > 0) {
			this.corePoolSize = corePoolSize;
		}
	}

	public int getFixedDelay() {
		return fixedDelay;
	}

	public void setFixedDelay(int fixedDelay) {
		if (this.fixedDelay > 0) {
			this.fixedDelay = fixedDelay;
		}
	}

	ThreadFactory getThreadFactory() {
		return Executors.defaultThreadFactory();
	}

	ScheduledExecutorService getService() {
		return service;
	}

	public void init() {
		if (log.isInfoEnabled()) {
			log.info("start to init " + this.getClass().getSimpleName());
		}

		this.service = Executors.newScheduledThreadPool(this.getCorePoolSize(), this.getThreadFactory());

		this.start();

		if (log.isInfoEnabled()) {
			log.info("success to init " + this.getClass().getSimpleName());
		}
	}

	abstract void start();

	public void destroy() {
		if (log.isInfoEnabled()) {
			log.info("start to destroy " + this.getClass().getSimpleName());
		}

		this.service.shutdown();

		if (log.isInfoEnabled()) {
			log.info("success to destroy " + this.getClass().getSimpleName());
		}
	}
}

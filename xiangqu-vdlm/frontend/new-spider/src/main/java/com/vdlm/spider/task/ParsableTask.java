package com.vdlm.spider.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.TaskBean;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public abstract class ParsableTask<T> implements Runnable {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected final BusSignalManager bsm;
	protected final HttpClientInvoker invoker;
	protected final HttpInvokeResult result;
	protected final CrawlableTask<? extends ItemListTaskBean> retriable;
	
	public ParsableTask(BusSignalManager bsm,
						 HttpClientInvoker invoker,
						 HttpInvokeResult result,
						 CrawlableTask<? extends ItemListTaskBean> retriable) {
		this.bsm = bsm;
		this.invoker = invoker;
		this.result = result;
		this.retriable = retriable;
	}
	
	@Override
	public void run() {
		final long start = System.currentTimeMillis();
		Logs.TASK.info("start to parse {} task...", getBean().getTaskName());
		final T entity = parse();
		if (entity == null) {
			Logs.TASK.warn("parse task {} failed", getBean());
			return;
		}
		persistent(entity);
		furtherCrawl(entity);
		Logs.TASK.info("end parse {} task, cost {} ms", 
				getBean().getTaskName(), System.currentTimeMillis() - start);
	}
	
	protected void submitCrawlableTask(CrawlableTask<? extends TaskBean> task) {
		bsm.signal(task);
	}
	
	protected void logStatistics(Logs.StatsResult ret, ItemListTaskBean bean) {
		Logs.statistics
		.warn("parser_shopinfo_sync. shopId:{}, shopUrl:{}, code:{}, reason:{}",
				bean.getOuerShopId(), bean.getShopUrl(),
				this.result.getStatusCode(), ret);
	}

	protected abstract T parse(); 
	protected abstract void persistent(T entity);
	protected abstract void furtherCrawl(T entity);
	
	protected ItemListTaskBean getBean() {
		return retriable.bean;
	}

}

/**
 * 
 */
package com.vdlm.proxycrawler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:52:32 PM Jul 20, 2014
 */
public abstract class Logs {

	public static final Logger unpredictableLogger = LoggerFactory
			.getLogger("unpredictable");

	public static final Logger maybeChangedRuleLogger = LoggerFactory
			.getLogger("maybeChangedRule");

	public static final Logger monitorLogger = LoggerFactory
			.getLogger("monitor");

	public static final Logger statistics = LoggerFactory
			.getLogger("statistics");
	
	public static final Logger TASK = LoggerFactory
			.getLogger("task");
	
	public static final Logger RETRY = LoggerFactory
			.getLogger("retry");
	
	public static final Logger RESUBMIT_DEFEATER = LoggerFactory
			.getLogger("resubmit_defeater");
	
	public static final Logger ITEMLIST_PROCESS = LoggerFactory
			.getLogger("itemlist_process");
	
	public static final Logger ITEM_PROCESS = LoggerFactory
			.getLogger("item_process");
	
	public static final Logger QUEUE = LoggerFactory
			.getLogger("queue");
	
	public static final Logger PROXY = LoggerFactory
			.getLogger("proxy");

	/** 用于统计任务执行结果，加日志 */
	public enum StatsResult {
		/** 任务成功 */
		SUCESS, /** 任务失败 */
		FAIL, /** 任务部分成功 */
		PARTIAL, /** 任务已提交异步处理 */
		ASYNC
	}
}

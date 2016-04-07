/**
 * 
 */
package com.vdlm.spider.utils;

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
	
	public static final Logger itemParsedLogger = LoggerFactory
			.getLogger("item_parsed");

	/** 用于统计任务执行结果，加日志 */
	public enum StatsResult {
		
		SUCESS, /** 任务成功 */ 
		FAIL, /** 任务失败 */ 
		PARTIAL, /** 任务部分成功 */ 
		ASYNC /** 任务已提交异步处理 */
	}
}

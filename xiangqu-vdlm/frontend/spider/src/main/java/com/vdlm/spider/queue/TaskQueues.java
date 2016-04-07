/**
 * 
 */
package com.vdlm.spider.queue;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:32:54 PM Jul 20, 2014
 */
public class TaskQueues {

	private static TaskQueue parseItemTaskQueue;
	private static TaskQueue parseShopTaskQueue;
	private static TaskQueue smsMessageTaskQueue;
	private static TaskQueue pushMessageTaskQueue;

	public static TaskQueue getParseItemTaskQueue() {
		return parseItemTaskQueue;
	}

	public static TaskQueue getParseShopTaskQueue() {
		return parseShopTaskQueue;
	}

	public static TaskQueue getSmsMessageTaskQueue() {
		return smsMessageTaskQueue;
	}

	public static TaskQueue getPushMessageTaskQueue() {
		return pushMessageTaskQueue;
	}

	public void setParseItemTaskQueue(TaskQueue parseItemTaskQueue) {
		TaskQueues.parseItemTaskQueue = parseItemTaskQueue;
	}

	public void setParseShopTaskQueue(TaskQueue parseShopTaskQueue) {
		TaskQueues.parseShopTaskQueue = parseShopTaskQueue;
	}

	public void setSmsMessageTaskQueue(TaskQueue smsMessageTaskQueue) {
		TaskQueues.smsMessageTaskQueue = smsMessageTaskQueue;
	}

	public void setPushMessageTaskQueue(TaskQueue pushMessageTaskQueue) {
		TaskQueues.pushMessageTaskQueue = pushMessageTaskQueue;
	}

}

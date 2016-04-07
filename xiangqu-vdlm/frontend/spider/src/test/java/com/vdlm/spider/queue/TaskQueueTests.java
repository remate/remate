/**
 * 
 */
package com.vdlm.spider.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:15:59 PM Jul 23, 2014
 */
public class TaskQueueTests {

	TaskQueue queue1 = new TaskQueue().setPath("/ouer/data/fqueue/ParseItemTask").init();
	TaskQueue queue2 = new TaskQueue().setPath("/ouer/data/fqueue/ParseShopTask").init();

	@Test
	public void testTake() throws InterruptedException {
		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);

		exec.submit(new Runnable() {
			@Override
			public void run() {
				task(queue1);
			}
		});

		exec.submit(new Runnable() {
			@Override
			public void run() {
				task(queue2);
			}
		});
		
		exec.awaitTermination(5, TimeUnit.MINUTES);
	}

	void task(TaskQueue queue) {
		byte[] bytes;
		while (true) {
			try {
				bytes = queue.take();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}

			System.out.println(new String(bytes));
		}
	}
}

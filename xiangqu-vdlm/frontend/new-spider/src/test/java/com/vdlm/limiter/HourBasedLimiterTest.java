package com.vdlm.limiter;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.vdlm.limiter.ConcurrentLimiter;

/**
 *
 * @author: chenxi
 */

@ContextConfiguration(locations = { "classpath:META-INF/applicationContext-limiter.xml" })
public class HourBasedLimiterTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ConcurrentLimiter concurrentLimiter;
	
	@SuppressWarnings("static-access")
	@Test
	public void testHourThreshold() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		concurrentLimiter.start();
		for (int i = 0; i < 20; i++) {
			final Thread t = new TestThread(concurrentLimiter, latch, i);
			t.start();
		}
		latch.countDown();
		
		Thread.currentThread().sleep(10000);
	}
	
	class TestThread extends Thread {
		
		private final ConcurrentLimiter concurrentLimiter;
		private final CountDownLatch latch;
		private final int number;
		
		public TestThread(ConcurrentLimiter concurrentLimiter, CountDownLatch latch, int number) {
			this.concurrentLimiter = concurrentLimiter;
			this.latch = latch;
			this.number = number;
		}
		
		@Override
		public void run() {
			try {
				latch.await();
				concurrentLimiter.acquire();
				System.out.println(number + ":i'm sleeping");
				Thread.sleep(2000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			concurrentLimiter.release();
			System.out.println(number + ":i'm release");
		}
	}
}

package com.vdlm.common.concurrent;

import org.junit.Test;

import com.vdlm.concurrent.DynamicSemaphore;

/**
 *
 * @author: chenxi
 */

public class DynamicSemaphoreTest {

	@Test
	public void testDynamic() {
		final DynamicSemaphore semaphore = new DynamicSemaphore();
		System.out.println("permits:" + semaphore.availablePermits());
		semaphore.register();
		semaphore.register();
		System.out.println("permits:" + semaphore.availablePermits());
		semaphore.drainPermits();
		new TestThread(semaphore).start();

		semaphore.acquire(semaphore.getMaxPermits() + 2);
		System.out.println("i'm here");
		System.out.println("availablePermits:" + semaphore.availablePermits());
	}

	class TestThread extends Thread {

		private final DynamicSemaphore semaphore;

		TestThread(DynamicSemaphore semaphore) {
			this.semaphore = semaphore;
		}

		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					// System.out.println(i + ":i'm sleeping");
					Thread.sleep(2000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				semaphore.release();
				System.out.println(i + ":i'm release");
			}
		}

	}

}

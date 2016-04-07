/**
 * 
 */
package com.vdlm.spider.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.google.code.fqueue.FQueue;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:08:41 PM Jul 16, 2014
 */
public class BlockingFQueue {

	private final FQueue fqueue;
	private final ReentrantLock lock;
	private final Condition notEmpty;
	private final Condition notFull;
	private int size;
	private volatile boolean isClosed = false;

	public BlockingFQueue(String path) {
		this(path, 1024 * 1024 * 1024);
	}

	public BlockingFQueue(String path, int capacity) {
		try {
			this.fqueue = new FQueue(path, capacity);
		}
		catch (Exception e) {
			throw new RuntimeException("Error to new FQueue(" + path + ", " + capacity + ")", e);
		}
		this.size = fqueue.size();
		this.lock = new ReentrantLock();
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
	}

	private void insert(byte[] x) {
		fqueue.add(x);
		++size;
		notEmpty.signal();
	}

	// public void put(byte[] e) throws InterruptedException {
	// if (e == null) throw new NullPointerException();
	// final ReentrantLock lock = this.lock;
	// lock.lockInterruptibly();
	// try {
	// try {
	// while (curCapacity + e.length >= capacity)
	// notFull.await();
	// }
	// catch (InterruptedException ie) {
	// notFull.signal(); // propagate to non-interrupted thread
	// throw ie;
	// }
	// this.insert(e);
	// }
	// finally {
	// lock.unlock();
	// }
	// }

	private byte[] extract() {
		byte[] x = fqueue.poll();
		//		if (x == null) {
		//			throw new IllegalStateException("Error to poll from fqueue");
		//		}
		--size;
		notFull.signal();
		return x;
	}

	public byte[] take() throws InterruptedException {
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			try {
				while (!isClosed && size == 0)
					notEmpty.await();

				if (isClosed) {
					throw new InterruptedException("the Queue is closed");
				}
			}
			catch (InterruptedException ie) {
				notEmpty.signal(); // propagate to non-interrupted thread
				throw ie;
			}
			return this.extract();
		}
		finally {
			lock.unlock();
		}
	}

	public int size() {
		return this.size;
	}

	public boolean add(byte[] e) {
		if (e == null) throw new NullPointerException();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			insert(e);
			return true;
		}
		catch (Exception error) {
			return false;
		}
		finally {
			lock.unlock();
		}
	}

	public byte[] poll() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (size == 0) return null;
			byte[] x = extract();
			return x;
		}
		finally {
			lock.unlock();
		}
	}

	public void close() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			isClosed = true;

			try {
				notEmpty.signalAll();
			}
			finally {
				this.fqueue.close();
			}
		}
		finally {
			lock.unlock();
		}
	}
}

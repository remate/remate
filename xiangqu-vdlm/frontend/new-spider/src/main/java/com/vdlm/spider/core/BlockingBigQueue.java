///**
// * 
// */
//package com.vdlm.spider.core;
//
//import java.io.IOException;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
//
//import com.leansoft.bigqueue.BigQueueImpl;
//import com.leansoft.bigqueue.IBigQueue;
//
///**
// * @author Wayne.Wang<5waynewang@gmail.com>
// * @since 5:08:41 PM Jul 16, 2014
// */
//public class BlockingBigQueue {
//
//	private final IBigQueue bigqueue;
//	private final ReentrantLock lock;
//	private final Condition notEmpty;
//	private final Condition notFull;
//	private long size;
//	private volatile boolean isClosed = false;
//
//	public BlockingBigQueue(String path) {
//		this(path, "", 1024 * 1024 * 1024);
//	}
//	
//	public BlockingBigQueue(String path, String name) {
//		this(path, name, 1024 * 1024 * 1024);
//	}
//
//	public BlockingBigQueue(String path, String name, int capacity) {
//		try {
//			this.bigqueue = new BigQueueImpl(path, name, capacity);
//		}
//		catch (final Exception e) {
//			throw new RuntimeException("Error to new FQueue(" + path + ", " + capacity + ")", e);
//		}
//		this.size = bigqueue.size();
//		this.lock = new ReentrantLock();
//		this.notEmpty = lock.newCondition();
//		this.notFull = lock.newCondition();
//	}
//
//	private void insert(byte[] x) throws IOException {
//		bigqueue.enqueue(x);
//		++size;
//		notEmpty.signal();
//	}
//
//	// public void put(byte[] e) throws InterruptedException {
//	// if (e == null) throw new NullPointerException();
//	// final ReentrantLock lock = this.lock;
//	// lock.lockInterruptibly();
//	// try {
//	// try {
//	// while (curCapacity + e.length >= capacity)
//	// notFull.await();
//	// }
//	// catch (InterruptedException ie) {
//	// notFull.signal(); // propagate to non-interrupted thread
//	// throw ie;
//	// }
//	// this.insert(e);
//	// }
//	// finally {
//	// lock.unlock();
//	// }
//	// }
//
//	private byte[] extract() throws IOException {
//		final byte[] x = bigqueue.dequeue();
//		//		if (x == null) {
//		//			throw new IllegalStateException("Error to poll from fqueue");
//		//		}
//		--size;
//		notFull.signal();
//		return x;
//	}
//
//	public byte[] take() throws InterruptedException, IOException {
//		final ReentrantLock lock = this.lock;
//		lock.lockInterruptibly();
//		try {
//			try {
//				while (!isClosed && size == 0) {
//					notEmpty.await();
//				}
//
//				if (isClosed) {
//					throw new InterruptedException("the Queue is closed");
//				}
//			}
//			catch (final InterruptedException ie) {
//				notEmpty.signal(); // propagate to non-interrupted thread
//				throw ie;
//			}
//			return this.extract();
//		}
//		finally {
//			lock.unlock();
//		}
//	}
//
//	public long size() {
//		return this.size;
//	}
//
//	public boolean add(byte[] e) {
//		if (e == null) {
//			throw new NullPointerException();
//		}
//		final ReentrantLock lock = this.lock;
//		lock.lock();
//		try {
//			insert(e);
//			return true;
//		}
//		catch (final Exception error) {
//			return false;
//		}
//		finally {
//			lock.unlock();
//		}
//	}
//
//	public byte[] poll() throws IOException {
//		final ReentrantLock lock = this.lock;
//		lock.lock();
//		try {
//			if (size == 0) {
//				return null;
//			}
//			final byte[] x = extract();
//			return x;
//		}
//		finally {
//			lock.unlock();
//		}
//	}
//
//	public void close() throws IOException {
//		final ReentrantLock lock = this.lock;
//		lock.lock();
//		try {
//			isClosed = true;
//
//			try {
//				notEmpty.signalAll();
//			}
//			finally {
//				this.bigqueue.close();
//			}
//		}
//		finally {
//			lock.unlock();
//		}
//	}
//}

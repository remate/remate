package com.vdlm.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author:  chenxi
 */

public class DynamicSemaphore {
	
	private final static Logger LOG = LoggerFactory.getLogger(DynamicSemaphore.class);

    private final ResizeableSemaphore semaphore;

    private int maxPermits;

    private final AtomicInteger prepareReleased = new AtomicInteger(0);

    private final AtomicInteger prepareAcquired = new AtomicInteger(0);

    public DynamicSemaphore() {
        this(0);
    }

    public DynamicSemaphore(int permits) {
        semaphore = new ResizeableSemaphore(permits);
    }

    public void register() {
        setMaxPermits(getMaxPermits() + 1);
    }

    public int drainPermits() {
        return semaphore.drainPermits();
    }

    public int availablePermits() {
        return semaphore.availablePermits();
    }

    public synchronized void setMaxPermits(int newMax) {
        if (newMax < 1) {
            throw new IllegalArgumentException(
                    "Semaphore size must be at least 1," + " was " + newMax);
        }

        int delta = newMax - this.maxPermits;

        if (delta == 0) {
            return;
        } else if (delta > 0) {
            this.semaphore.release(delta);
        } else {
            delta *= -1;
            this.semaphore.reducePermits(delta);
        }

        this.maxPermits = newMax;
    }

    public void prepareAcquired() {
        prepareAcquired.incrementAndGet();
    }

    public void prepareReleased() {
        prepareReleased.incrementAndGet();
    }

    public int getPrepareAcquired() {
        return prepareAcquired.get();
    }

    public int getPrepareReleased() {
        return prepareReleased.get();
    }

    public synchronized void flushPrepareAcquired() {
        final int num = prepareAcquired.get();
        if (num <= 0) {
        	LOG.warn("no prepared acquire");
            return;
        }
        try {
            this.semaphore.acquire(num);
        } catch (final InterruptedException e) {
        	LOG.error("flushPrepareAcquired error ", e);
        }
    }

    public synchronized void flushPrepareReleased() {
        final int num = prepareReleased.get();
        if (num <= 0) {
        	LOG.warn("no prepared release");
            return;
        }
        this.semaphore.release(prepareReleased.get());
    }

    public void release() {
        this.semaphore.release();
    }

    public void release(int permits) {
        this.semaphore.release(permits);
    }
    
    public boolean tryAcquire() {
    	return semaphore.tryAcquire();
    }

    public void acquire() {
        try {
            this.semaphore.acquire();
        } catch (final InterruptedException e) {
        	LOG.error("acquire error ", e);
        }
    }

    public void acquire(int permits) {
        try {
            this.semaphore.acquire(permits);
        } catch (final InterruptedException e) {
        	LOG.error("acquire error ", e);
        }
    }

    public synchronized int getMaxPermits() {
        return maxPermits;
    }

    private static final class ResizeableSemaphore extends Semaphore {

        /**
         *
         */
        private static final long serialVersionUID = 8417422198201251272L;

        ResizeableSemaphore(int size) {
            super(size);
        }

        @Override
        protected void reducePermits(int reduction) {
            super.reducePermits(reduction);
        }
    }

}

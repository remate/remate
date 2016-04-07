package com.vdlm.limiter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.base.PeriodicalUpdater;
import com.vdlm.common.core.statistic.TransactionStatisticer;
import com.vdlm.concurrent.DynamicSemaphore;

/**
 *
 * @author: chenxi
 */

public class HourBasedConcurrentLimiter implements ConcurrentLimiter, PeriodicalUpdater {
	
	private DynamicSemaphore semaphore;
	
	@Autowired
	private TransactionStatisticer transactionStatisticer;
	
	@Override
	public void start() throws IOException {
		final Properties properties = loadThresholdsProps();
        final int currentHour = getCurrentHour();
        int limit = -1;
        String value;
        boolean markAgain = false;
        int hour = currentHour;
        while (limit < 0) {
        	value = properties.getProperty(String.valueOf(hour));
        	if (value != null) {
        		limit = Integer.valueOf(value);
        		if (limit < 0) {
					throw new IllegalArgumentException("invalid value: " + limit + " for key: " 
							+ currentHour + " in " + LimiterConstants.HOUR_CONCURRENT_LIMIT_FILE);
				}
        		break;
        	} else {
        		if (hour == currentHour) {
        			if (markAgain) {
        				// the second time visit the current hour, must scan all values in properties file
						break;
					}
        			markAgain = true;
        			hour = LimiterConstants.MAX_HOUR;
        		} else {
        			hour--;
				}
        	}
        }
        
        if (limit < 0) {
        	limit = LimiterConstants.DEFAULT_CONCURRENT;
		}
        semaphore = new DynamicSemaphore(limit);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void acquire() throws InterruptedException {
		while (!semaphore.tryAcquire()) {
			Thread.currentThread().sleep(1000);
		}
		transactionStatisticer.incHandledTransactionStart();
	}

	@Override
	public void release() {
		semaphore.release();
		transactionStatisticer.incHandledTransactionEnd();
	}
	
	@Override
	public void updatePeriodically() throws IOException {
		final Properties properties = loadThresholdsProps();
        final int currentHour = getCurrentHour();
        final String value = properties.getProperty(String.valueOf(currentHour));
        if (value != null) {
        	// no-op
        	return;
        }
        final int limit = Integer.valueOf(value);
		if (limit < 0) {
			throw new IllegalArgumentException("invalid value: " + limit + " for key: " 
					+ currentHour + " in " + LimiterConstants.HOUR_CONCURRENT_LIMIT_FILE);
		}
		semaphore.setMaxPermits(limit);
	}
	
	private Properties loadThresholdsProps() throws IOException {
		final Properties properties = new Properties();
        final InputStream resource = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream(LimiterConstants.HOUR_CONCURRENT_LIMIT_FILE);
        try {
            properties.load(resource);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + LimiterConstants.HOUR_CONCURRENT_LIMIT_FILE + " under classpath", e);
        } finally {
        	if (resource != null) {
				resource.close();
			}
        }
	}
	
	private int getCurrentHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

}

package com.vdlm.limiter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.core.statistic.TransactionStatisticer;

/**
 *
 * @author: chenxi
 */

public class ThroughputConcurrentLimiter implements ConcurrentLimiter {
	
	private int handledThroughput;
	private int finishedThroughput;
	
	@Autowired
	private TransactionStatisticer transactionStatisticer;
	
	@Override
	public void start() throws Exception {
		final Properties properties = loadThresholdsProps();
		handledThroughput = Integer.valueOf(properties.getProperty(LimiterConstants.HANDLED_THROUGHPUT));
		finishedThroughput = Integer.valueOf(properties.getProperty(LimiterConstants.FINISHED_THROUGHPUT));
	}

	@SuppressWarnings("static-access")
	@Override
	public void acquire() throws InterruptedException {
		while (transactionStatisticer.getHandledThroughput().get() > handledThroughput
				|| transactionStatisticer.getFinishedThroughput().get() > finishedThroughput) {
			Thread.currentThread().sleep(1000);
		}
		transactionStatisticer.incHandledTransactionStart();
	}

	@Override
	public void release() {
		transactionStatisticer.incHandledTransactionEnd();
	}

	private Properties loadThresholdsProps() throws IOException {
		final Properties properties = new Properties();
        final InputStream resource = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream(LimiterConstants.THROUGHPUT_CONCURRENT_LIMIT_FILE);
        try {
            properties.load(resource);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + LimiterConstants.THROUGHPUT_CONCURRENT_LIMIT_FILE + " under classpath", e);
        } finally {
        	if (resource != null) {
				resource.close();
			}
        }
	}
}

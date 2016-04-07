package com.vdlm.limiter;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.PriorityOrdered;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;

/**
 *
 * @author: chenxi
 */

public class RateLimiterProvider implements PriorityOrdered, BusSignalListener<RateLimitValue> {
	
	private final static Logger LOG = LoggerFactory.getLogger(RateLimiterProvider.class);
	
	private double currentRate;
	
	private final ConcurrentMap<String, RateLimiter> rateLimiters = Maps.newConcurrentMap();
	
	public RateLimiterProvider(BusSignalManager bsm) {
		bsm.bind(RateLimitValue.class, this);
	}
	
	public RateLimiter provide(String key) {
		final RateLimiter rl = rateLimiters.get(key);
		if (rl != null) {
			return rl;
		}
		rateLimiters.putIfAbsent(key, RateLimiter.create(currentRate));
		return rateLimiters.get(key);
	}
	
	public boolean destory(String key) {
		return rateLimiters.remove(key) != null;
	}
	
	private void updateRate(double newRate) {
		LOG.info("start to update rate value, from " + currentRate + " to " + newRate);
		currentRate = newRate;
		final Collection<RateLimiter> rls = rateLimiters.values();
		for (final RateLimiter rl : rls) {
			rl.setRate(newRate);
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void signalFired(RateLimitValue signal) {
		updateRate(signal.getValue());
	}
}

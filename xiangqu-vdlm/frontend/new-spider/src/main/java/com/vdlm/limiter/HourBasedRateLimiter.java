package com.vdlm.limiter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.base.PeriodicalUpdater;
import com.vdlm.common.bus.BusSignalManager;

/**
 *
 * @author: chenxi
 */

public class HourBasedRateLimiter implements RateLimitIniter, PeriodicalUpdater {

	private final static Logger LOG = LoggerFactory.getLogger(HourBasedRateLimiter.class);
	
	@Autowired
	private BusSignalManager bsm;
	
	private double currentRate;
	
	@Override
	@PostConstruct
	public void initRate() throws Exception {
		final Properties properties = load(LimiterConstants.HOUR_RATELIMITS_FILE);
        final int currentHour = getCurrentHour();
        currentRate = -1;
        String value;
        boolean markAgain = false;
        int hour = currentHour;
        while (currentRate < 0) {
        	value = properties.getProperty(String.valueOf(hour));
        	if (value != null) {
        		currentRate = Double.valueOf(value);
        		if (currentRate < 0) {
					throw new IllegalArgumentException("invalid value: " + currentRate + " for key: " 
							+ currentHour + " in " + LimiterConstants.HOUR_RATELIMITS_FILE);
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
        
        if (currentRate < 0) {
        	currentRate = LimiterConstants.DEFAULT_RATE;
		}
        
        LOG.info("the current hour is " + currentHour + ", the rate limit value is " + currentRate);
        bsm.signal(new RateLimitValue(currentRate));
	}

	@Override
	public void updatePeriodically() throws Exception {
		final Properties properties = load(LimiterConstants.HOUR_RATELIMITS_FILE);
        final int currentHour = getCurrentHour();
        final String value = properties.getProperty(String.valueOf(currentHour));
        if (value != null) {
        	// no-op
        	LOG.info("the hour " + currentHour + " has no rate limit value, use old one: " + currentRate);
        	return;
        }
        final double newRate = Double.valueOf(value);
        if (currentRate == newRate) {
        	LOG.info("the hour " + currentHour + " has the same rate limit value as old one: " + currentRate);
        	return;
        }
		if (newRate < 0) {
			throw new IllegalArgumentException("invalid value: " + newRate + " for key: " 
					+ currentHour + " in " + LimiterConstants.HOUR_RATELIMITS_FILE);
		}
		
		currentRate = newRate;
		LOG.info("the hour " + currentHour + " has a new rate limit value: " + currentRate + ", start to update...");
		bsm.signal(new RateLimitValue(currentRate));
	}
	
	private Properties load(String file) throws IOException {
		final Properties properties = new Properties();
        final InputStream resource = Thread.currentThread()
                                     .getContextClassLoader()
                                     .getResourceAsStream(file);
        try {
            properties.load(resource);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load " + file + " under classpath", e);
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

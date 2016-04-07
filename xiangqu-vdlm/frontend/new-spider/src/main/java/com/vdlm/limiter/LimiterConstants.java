package com.vdlm.limiter;

/**
 *
 * @author: chenxi
 */

public abstract class LimiterConstants  {

	public final static double DEFAULT_RATE = Double.MAX_VALUE;
	public final static int DEFAULT_CONCURRENT = Integer.MAX_VALUE;
	public final static int MAX_HOUR = 23;
	
	public final static String HOUR_RATELIMITS_FILE = "hour-ratelimits.properties";
	public final static String HOUR_CONCURRENT_LIMIT_FILE = "hour-conlimit.properties";
	public final static String THROUGHPUT_CONCURRENT_LIMIT_FILE = "throughput-conlimit.properties";
	
	public final static String HANDLED_THROUGHPUT = "handled.throughput";
	public final static String FINISHED_THROUGHPUT = "finished.throughput";
}

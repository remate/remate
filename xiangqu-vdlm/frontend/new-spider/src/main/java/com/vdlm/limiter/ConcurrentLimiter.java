package com.vdlm.limiter;


/**
 *
 * @author: chenxi
 */

public interface ConcurrentLimiter 
{
	public void start() throws Exception;
	
	public void acquire() throws InterruptedException;
	
	public void release();
}

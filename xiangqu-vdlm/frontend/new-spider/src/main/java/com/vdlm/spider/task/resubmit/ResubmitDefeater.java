package com.vdlm.spider.task.resubmit;

/**
 *
 * @author: chenxi
 */

public interface ResubmitDefeater<T> 
{
	public boolean canSumbit(T task);
}

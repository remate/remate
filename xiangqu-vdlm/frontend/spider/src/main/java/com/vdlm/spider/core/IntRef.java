/**
 * 
 */
package com.vdlm.spider.core;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 7:53:11 PM Jul 19, 2014
 */
public class IntRef {

	private int value;

	public IntRef() {
	}

	public IntRef(int value) {
		this.value = value;
	}

	public final int incrementAndGet() {
		return ++value;
	}

	public final int get() {
		return value;
	}

	public final void set(int newValue) {
		value = newValue;
	}
}

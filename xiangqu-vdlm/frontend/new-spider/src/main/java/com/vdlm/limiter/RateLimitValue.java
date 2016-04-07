package com.vdlm.limiter;

/**
 *
 * @author: chenxi
 */

public class RateLimitValue {

	private final double value;
	
	public RateLimitValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
}

/**
 * 
 */
package com.vdlm.spider;

/**
 * <pre>
 * 请求来源
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:10:17 PM Aug 7, 2014
 */
public enum ReqFrom {
	KKKD(1), XIANGQU(2);

	private int value;

	private ReqFrom(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public static ReqFrom valueOf(int value) {
		switch (value) {
		case 1:
			return KKKD;
		case 2:
			return XIANGQU;
		default:
			throw new IllegalArgumentException("invalid enum value:" + value);
		}
	}
}

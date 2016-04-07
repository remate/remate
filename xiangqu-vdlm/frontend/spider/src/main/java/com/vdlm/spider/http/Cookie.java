/**
 * 
 */
package com.vdlm.spider.http;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:06:53 PM Nov 20, 2014
 */
public class Cookie {
	private String k;
	private String v;

	public Cookie(String k, String v) {
		this.k = k;
		this.v = v;
	}

	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}
}

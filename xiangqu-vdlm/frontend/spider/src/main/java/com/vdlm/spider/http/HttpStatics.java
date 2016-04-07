/**
 * 
 */
package com.vdlm.spider.http;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:44:59 AM Jul 22, 2014
 */
public abstract class HttpStatics {

	public static final String LOCAL_IP = "127.0.0.1";

	/**
	 * <pre>
	 * 判断是否本机
	 * </pre>
	 * @param ip
	 * @return
	 */
	public static boolean isLocalIp(String ip) {
		return StringUtils.startsWith(ip, LOCAL_IP);
	}
}

/**
 * 
 */
package com.vdlm.proxycrawler.utils;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * <pre>
 *
 * </pre>
 * 
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 10:19:51 AM Apr 21, 2015
 */
public class TelnetUtils {

	public static boolean telnetSuccessfully(String address) {
		final String[] arr = address.split(":");
		if (arr.length == 1) {
			return telnetSuccessfully(arr[0], 80);
		} else {
			return telnetSuccessfully(arr[0], Integer.parseInt(arr[1]));
		}
	}

	/**
	 * telnet是否正常
	 * 
	 * @param hostname
	 * @param port
	 * @return
	 */
	public static boolean telnetSuccessfully(String hostname, int port) {
		TelnetClient telnet = new TelnetClient();
		try {
			telnet.setConnectTimeout(30000);
			telnet.connect(hostname, port);
			return true;
		} catch (Exception ignore) {
			return false;
		} finally {
			try {
				telnet.disconnect();
			} catch (Exception ignore) {
			}
		}
	}
}

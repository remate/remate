/**
 * 
 */
package com.vdlm.restapi;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 1:02:32 PM Jul 29, 2014
 */
public abstract class DeviceTypes {

	public static int getDeviceType(HttpServletRequest req) {
		final Enumeration<String> names = req.getHeaderNames();
		if (names == null) {
			return 3;
		}
		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			if (!StringUtils.equalsIgnoreCase(name, "user-agent")) {
				continue;
			}
			final Enumeration<String> headers = req.getHeaders(name);

			while (headers.hasMoreElements()) {
				final String userAgent = headers.nextElement();
				if (StringUtils.containsIgnoreCase(userAgent, "ios")) {
					return 1;
				}
				else if (StringUtils.containsIgnoreCase(userAgent, "android")) {
					return 2;
				}
			}
		}
		return 3;
	}
}

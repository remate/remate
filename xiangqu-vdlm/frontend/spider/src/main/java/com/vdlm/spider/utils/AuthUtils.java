/**
 * 
 */
package com.vdlm.spider.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:40:37 PM Apr 18, 2015
 */
public class AuthUtils {

	/**
	 * 判断是否是需要认证的响应
	 * 
	 * @param response
	 * @return
	 */
	public static boolean isAuthResponse(HttpResponse response) {
		if (response == null) {
			return false;
		}
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case HttpStatus.SC_MOVED_TEMPORARILY:
		case HttpStatus.SC_MOVED_PERMANENTLY:
		case HttpStatus.SC_TEMPORARY_REDIRECT:
		case HttpStatus.SC_SEE_OTHER:
			Header locationHeader = response.getFirstHeader("location");
			// TODO 可以把规则配到配置文件中
			if (StringUtils.contains(locationHeader.getValue(),
					"login.taobao.com")
					|| StringUtils.contains(locationHeader.getValue(),
							"login.tmall.com")
//					|| StringUtils.contains(locationHeader.getValue(),
//							"jump.taobao.com")
							) {
				return true;
			}
		}
		return false;
	}
}

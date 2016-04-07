/**
 * 
 */
package com.vdlm.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.lang.servlet.CookieUtils;

/**
 * <pre>
 *
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:20:12 PM Aug 31, 2015
 */
public class XiangquUtils {
	static final Logger logger = LoggerFactory.getLogger(XiangquUtils.class);

	@SuppressWarnings("deprecation")
	public static boolean isLogined(HttpServletRequest request) {
		// cookie校验，判断xiangqu有没登录
		final Cookie[] cookies = request.getCookies();
		if (ArrayUtils.isEmpty(cookies)) {
			logger.warn("no cookie when request {}", request.getRequestURL());
			return false;
		}
		// 校验cookie
		final String login = CookieUtils.getCookieValue(request, "_login_");
		final String checkTag = CookieUtils.getCookieValue(request, "_cktag_");
		final String code = CookieUtils.getCookieValue(request, "_code_");

		if (StringUtils.isBlank(login) //
				|| StringUtils.isBlank(checkTag)//
				|| StringUtils.isBlank(code)) {
			logger.warn("login={}, checkTag={}, code={}, when request {}", login, checkTag, code, request.getRequestURL());
			return false;
		}
		final int size = 2;
		String text;
		try {
			text = URLDecoder.decode(login, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			text = URLDecoder.decode(login);
		}
		final String[] tmp = text.split("\\|");
		if (tmp.length != size) {
			logger.warn("invalid style: login={}, when request {}", login, request.getRequestURL());
			return false;
		}
		final int capacity = login.length() + checkTag.length() - (size - 1);
		final StringBuffer sb = new StringBuffer(capacity);
		sb.append(tmp[0]);//userName
		sb.append(tmp[1]);//userId
		sb.append(checkTag);

		if (!Base64.isBase64(code) || !StringUtils.equalsIgnoreCase(DigestUtils.md5Hex(sb.toString()), BlowfishEncrypter.getEncrypter().decrypt(new String(Base64.decodeBase64(code))))) {
			logger.warn("invalid cookie: login={}, checkTag={}, code={}, when request {}", login, checkTag, code, request.getRequestURL());
			return false;
		}
		return true;
	}
}

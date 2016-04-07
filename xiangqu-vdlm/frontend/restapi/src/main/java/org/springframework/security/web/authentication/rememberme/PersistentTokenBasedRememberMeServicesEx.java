package org.springframework.security.web.authentication.rememberme;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.vdlm.service.user.UserService;

/**
 * 先取cookie，没有则尝试从req param里获取authToken。
 * 
 * @author Ju
 */
public class PersistentTokenBasedRememberMeServicesEx extends PersistentTokenBasedRememberMeServices {

	@SuppressWarnings("deprecation")
	public PersistentTokenBasedRememberMeServicesEx() {
		super();
	}

	public PersistentTokenBasedRememberMeServicesEx(String key, UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
			HttpServletResponse response) {
	    // 1. validate if the cookie is in success history cookie(valid time: 10s)

	    if (isDeviceToken(cookieTokens)) { // deviceId
			UserService userService = (UserService) getUserDetailsService();
			return userService.loadUserByUsername(cookieTokens[1]); // FIXME
		}
		
		return super.processAutoLoginCookie(cookieTokens, request, response);
		// check if validate true, push the cookie into redis history save for 10s
		
		// FIXME @tonghu
		// 2. validate cookie is in session history cookie
		// 3. save history cookie token
		// 2. catch if exsist CookieTheftException
		// Set<String> historyTokens = (Set<String>)request.getSession().getAttribute("history_tokens");
	}

	private boolean isDeviceToken(String[] cookieTokens) {
		return cookieTokens.length == 3 && "device".equals(cookieTokens[0]);
	}
	
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        String cookieValue = encodeCookie(tokens);
        Cookie cookie = new Cookie(this.getCookieName(), cookieValue);
        cookie.setMaxAge(maxAge);
        
        // 把authToken写到根下面
        cookie.setPath("/");

        cookie.setSecure(request.isSecure());

        Method setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class,"setHttpOnly", boolean.class);
        if(setHttpOnlyMethod != null) {
            ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Note: Cookie will not be marked as HttpOnly because you are not using Servlet 3.0 (Cookie#setHttpOnly(boolean) was not found).");
        }

        response.addCookie(cookie);
    }

	/**
	 * 先取cookie，没有则尝试从req param里获取
	 */
	@Override
	protected String extractRememberMeCookie(HttpServletRequest request) {
		String ret = super.extractRememberMeCookie(request);
		if (StringUtils.hasText(ret))
			return ret;
		return request.getParameter(getCookieName());
	}
}

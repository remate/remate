package com.vdlm.authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vdlm.biz.authentication.LoginStrategy;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserSigninLog;
import com.vdlm.service.user.UserService;
import com.vdlm.service.userAgent.UserSigninLogService;
import com.vdlm.service.userAgent.impl.UserSigninLogFactory;
import com.vdlm.utils.UniqueNoUtils;
import com.vdlm.utils.UniqueNoUtils.UniqueNoType;

/**
 *
 * @author:  chenxi
 */

public class KkkdLoginStrategy implements LoginStrategy {

//	private static final String CLIENT_ID = "vd_cid";

	private static Logger LOG = LoggerFactory.getLogger(KkkdLoginStrategy.class);

	//    private List<String> whiteList;
	//    private List<String> whitePrefixList;

	@Override
	public String buildLoginUrl(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		//        boolean inWhiteList = false;
		//        for (String wlist : whiteList) {
		//            if (requestURI.equals(wlist)) {
		//                inWhiteList = true;
		//            }
		//        }
		//        if (!inWhiteList) {
		//            for (String wlist : whitePrefixList) {
		//                if (requestURI.startsWith(wlist)) {
		//                    inWhiteList = true;
		//                }
		//            }
		//        }
		//
		//        if (!inWhiteList) {
		//            // TODO
		//            return null;
		//        }

		//        Cookie[] cookies = request.getCookies();
		        boolean existCid = false;
		//        if (cookies != null) {
		//            for (Cookie cookie : cookies) {
		//                if (cookie.getName().equals(CLIENT_ID) && cookie.getValue() != null) {
		//                    existCid = true;
		//                    // 匿名用户自动登录
		//                    LOG.warn("user auto login by cookie " + cookie.getValue());
		//                    setCurrentUser(cookie.getValue(), request.getParameter("partner"), request);
		//                    break;
		//                }
		//            }
		//        }
		        if (!existCid) {
		//            String newCid = UniqueNoUtils.next(UniqueNoType.CID);
		//            Cookie newCookie = new Cookie(CLIENT_ID, newCid);
		//            newCookie.setMaxAge(Integer.MAX_VALUE);
		//            newCookie.setPath("/");
		//            response.addCookie(newCookie);
		//            request.setAttribute(CLIENT_ID, newCid);
		//
		//            LOG.warn("create new user and login by cookie " + newCid);
		//            setCurrentUser(newCid, request.getParameter("partner"), request);
		        	return "/pc/login.html";
		        }

		String requestURI = request.getRequestURI();
		String query = request.getQueryString();
		if (query != null) return requestURI + "?" + query;

		return requestURI;
	}

	private void setCurrentUser(String cid, String partner, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof User)) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(request.getServletContext());
			UserService userService = (UserService) webApplicationContext.getBean("userService");
			User user = userService.loadByLoginname(cid);
			if (user == null) {
				user = userService.registerAnonymous(cid, partner);
			}
			Authentication auth1 = new UsernamePasswordAuthenticationToken(user, null,
					user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth1);

			//记录用户登录环境Log
			UserSigninLog log = UserSigninLogFactory.createUserSigninLog(request, user);
			UserSigninLogService userSigninLogService = (UserSigninLogService) webApplicationContext
					.getBean("userSigninLogService");
			userSigninLogService.insert(log);
		}
	}

	//    public void setWhiteList(List<String> whiteList) {
	//        this.whiteList = whiteList;
	//    }
	//
	//    public void setWhitePrefixList(List<String> whitePrefixList) {
	//        this.whitePrefixList = whitePrefixList;
	//    }

}

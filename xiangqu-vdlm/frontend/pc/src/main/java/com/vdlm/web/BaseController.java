

package com.vdlm.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vdlm.dal.model.User;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;

/**
 * web工程的基类Controller
 *
 * @author odin
 */
public class BaseController {

	protected static final String COOKIE_NAME_KDSESSID = "KDSESSID";
	protected static final String COOKIE_NAME_KDAUTHTOKEN = "kdAuthToken";
	protected static final String COOKIE_NAME_FROMPAGE = "FromPage";
	protected static final String COOKIE_NAME_FROMCHANNEL = "FromChannel";
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	@Value("${alipay.domain}")
    String alipayDomain;
	
	@Value("${profiles.active}")
	String profile;

	/**
	 * 获取当前用户信息
	 * 如果是未登录的匿名用户，系统根据匿名用户唯一码自动创建一个用户
	 * 具体逻辑查看：UniqueNoFilter
	 * @return
	 */
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			Object principal = auth.getPrincipal();
			if (principal instanceof User) {
                return (User) principal;
            }

			if (auth.getClass().getSimpleName().indexOf("Anonymous") < 0) {
                log.error("Unknown authentication encountered, ignore it. " + auth);
            }
		}

		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "need login first.");
	}

	/**
     * 支付宝要求支付宝出现的页面不能在其他第三方域名下，只能在.kkkd.com下
     * @param request
     * @param response
     * @return
     */
	protected String redirectDomain(HttpServletRequest request, HttpServletResponse response) {
        String redirect = "";
       	return redirect; //支付宝已不用跳转域名
//        if ("prod".equals(profile) // only redirect in prod env
//        		&& !request.getServerName().toLowerCase().endsWith(alipayDomain)) {
//            String requestURL = request.getRequestURI().toString();
//            if (request.getQueryString() != null) {
//                requestURL += "?" + request.getQueryString();
//            }
//			
//			try {
//                requestURL = URLEncoder.encode(requestURL, "utf-8");
//            } catch (UnsupportedEncodingException e) {
//                log.error("requestURL encode error, requestURL:[" + requestURL + "]");
//            }
//			
//            Cookie[] cookies = request.getCookies();
//            String kdSessId = "";
//            String kdAuthToken = "";
//            String fromPage = "";
//            String fromChannel = "";
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if (cookie.getName().equals(COOKIE_NAME_KDSESSID)) {
//                        kdSessId = cookie.getValue();
//                    }
//                    if (cookie.getName().equals(COOKIE_NAME_KDAUTHTOKEN)) {
//                        kdAuthToken = cookie.getValue();
//                    }
//                    // fix VD763 in jira -- set FromPage and FromChannel cookie to www.kkkd.com from xiangqu
//                    if (cookie.getName().equals(COOKIE_NAME_FROMPAGE)) {
//                    	fromPage = cookie.getValue();
//                    }
//                    if (cookie.getName().equals(COOKIE_NAME_FROMCHANNEL)) {
//                    	fromChannel = cookie.getValue();
//                    }
//                }
//            }
//			// TODO will remove the hard code
//            redirect = "redirect:http://www.kkkd.com/auth/redirect?kdSessId=" + kdSessId + "&kdAuthToken=" + kdAuthToken 
//            		+ "&fromPage=" + fromPage + "&fromChannel=" + fromChannel + "&url=" + requestURL;
//        }
//        if (StringUtils.isNotEmpty(redirect)) {
//            log.info("Url will redirect to: " + redirect);
//        } else {
//            log.info("Url will not redirect");
//        }
//        return redirect;
    }

	
}


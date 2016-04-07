

package com.vdlm.web.auth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.web.BaseController;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @RequestMapping("/redirect")
    public String redirect(String kdSessId, String kdAuthToken, String fromPage, String fromChannel, 
    		String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Cookie[] cookies = request.getCookies();

        boolean existKdSessId = false;
        boolean existKdAuthToken = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME_KDSESSID)) {
                	if (!cookie.getValue().equals(kdSessId)) {
                		Cookie c = new Cookie(COOKIE_NAME_KDSESSID, kdSessId);
                		c.setDomain(".kkkd.com");
                        c.setPath("/");
	                    response.addCookie(c);
                	}
                    existKdSessId = true;
                }
                if (cookie.getName().equals(COOKIE_NAME_KDAUTHTOKEN)) {
                	if (!cookie.getValue().equals(kdAuthToken)) {
                		Cookie c = new Cookie(COOKIE_NAME_KDAUTHTOKEN, kdAuthToken);
                		c.setDomain(".kkkd.com");
                        c.setPath("/");
                        response.addCookie(c);
                	}
                    existKdAuthToken = true;
                }
            }
        }
        
        if (!existKdSessId) {
        	stealTomcatSessionId(request, kdSessId);
            Cookie c1 = new Cookie(COOKIE_NAME_KDSESSID, kdSessId);
            c1.setDomain(".kkkd.com");
            c1.setPath("/");
            response.addCookie(c1);
        }
        if (!existKdAuthToken) {
            Cookie c2 = new Cookie(COOKIE_NAME_KDAUTHTOKEN, kdAuthToken);
            c2.setDomain(".kkkd.com");
            c2.setPath("/");
            response.addCookie(c2);
        }
        
        // fix VD763 in jira -- set FromPage and FromChannel cookie to www.kkkd.com from xiangqu
        Cookie fpCookie = new Cookie(COOKIE_NAME_FROMPAGE, fromPage);
        fpCookie.setDomain(".kkkd.com");
        fpCookie.setPath("/");
        response.addCookie(fpCookie);
        
        Cookie fcCookie = new Cookie(COOKIE_NAME_FROMCHANNEL, fromChannel);
        fcCookie.setDomain(".kkkd.com");
        fcCookie.setPath("/");
        response.addCookie(fcCookie);

        return "redirect:" + url;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void stealTomcatSessionId(HttpServletRequest request, String kdSessId) throws Exception {
    	HttpServletRequestWrapper w1 = (HttpServletRequestWrapper) ((HttpServletRequestWrapper) request).getRequest();
        HttpServletRequestWrapper w2 = (HttpServletRequestWrapper) w1.getRequest();
        ServletRequest sr = w2.getRequest(); 
        Field field = sr.getClass().getDeclaredField("request");
        field.setAccessible(true);
        Class clazz = Class.forName("org.apache.catalina.connector.Request");
        Method method = clazz.getDeclaredMethod("setRequestedSessionId", String.class);
        method.invoke(field.get(sr), kdSessId);
    }
}


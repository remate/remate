package com.vdlm.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;

import com.vdlm.biz.authentication.LoginStrategy;

/**
 *
 * @author:  chenxi
 */

public class XiangquLoginStrategy implements LoginStrategy {

    private static Logger LOG = LoggerFactory.getLogger(XiangquLoginStrategy.class);

    @Value("${xiangqu.web.site}")
    private String xiangquWebSite;

    @Override
    public String buildLoginUrl(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for(Cookie cookie : cookies) {
//                if (cookie.getName().equals("KDSESSID") && request.getSession().getId().equals(cookie.getValue())) { //
//                    LOG.info("KDSESSID:" + cookie.getValue());
//                    // 删除子域的domain
//                    cookie.setPath("/");
//                    cookie.setMaxAge(0);
//                    response.addCookie(cookie);
//                }
//            }
//        }

        String requestURL = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            requestURL += "?" + request.getQueryString();
        }
        String xiangquGetTokenUrl = "/";
        try {
            xiangquGetTokenUrl = xiangquWebSite + "/ouer/auth?backUrl=" + URLEncoder.encode(requestURL, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return xiangquGetTokenUrl;
    }

}

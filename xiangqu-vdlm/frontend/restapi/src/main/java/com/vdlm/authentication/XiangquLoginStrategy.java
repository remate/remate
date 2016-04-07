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

    private static Logger LOGGER = LoggerFactory.getLogger(XiangquLoginStrategy.class);

    @Value("${xiangqu.api.site}")
    private String xiangquApiSite;

    @Override
    public String buildLoginUrl(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {

        String requestURL = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            requestURL += "?" + request.getQueryString();
        }
        String xiangquGetTokenUrl = "/";
        try {
            xiangquGetTokenUrl = xiangquApiSite + "/ouer/auth?backUrl=" + URLEncoder.encode(requestURL, "utf-8");
        } catch (UnsupportedEncodingException e) {
        	LOGGER.error("buildLoginUrl error",e);
        }
        LOGGER.debug("Xiangqu User will be redirected to url: " + xiangquGetTokenUrl);
        return xiangquGetTokenUrl;
    }

}

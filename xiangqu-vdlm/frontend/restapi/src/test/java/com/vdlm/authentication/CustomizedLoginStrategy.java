package com.vdlm.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

import com.vdlm.biz.authentication.LoginStrategy;

/**
 *
 * @author:  chenxi
 */

public class CustomizedLoginStrategy implements LoginStrategy {

    @Override
    public String buildLoginUrl(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception) {
        String path = request.getRequestURI();
        if (path.startsWith("/admin")) {
            return "/admin";
        } else {
            return "/other";
        }
    }

}

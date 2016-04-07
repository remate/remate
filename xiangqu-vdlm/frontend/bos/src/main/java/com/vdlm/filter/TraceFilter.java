package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.vdlm.dal.model.User;

public class TraceFilter extends OncePerRequestFilter {

    private Logger log = LoggerFactory.getLogger("monitor");
    
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    String requestURI = request.getRequestURI();
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String trace = "";
        if (principal != null && principal instanceof User) {
            User user = (User)principal;
            trace = "TRACE: user[" + user.getLoginname() + "] : " + requestURI;
            if (StringUtils.isNotEmpty(request.getQueryString())) {
                trace += "?" + request.getQueryString();
            }
        } else {
            trace = "TRACE: anonymouse user access: " + requestURI;
            if (StringUtils.isNotEmpty(request.getQueryString())) {
                trace += "?" + request.getQueryString();
            }
        }
        log.info(trace + " Params: " + JSON.toJSONString(request.getParameterMap()));

        filterChain.doFilter(request, response);
	}
}

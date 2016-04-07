package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.vdlm.dal.model.User;
import com.vdlm.service.user.UserService;

public class RunAsFilter extends OncePerRequestFilter {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
	private RunAsManager runAsManager;
    
    @Autowired
	private UserService userService;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		UserService userService = (UserService) webApplicationContext.getBean("userService");
		
	    String requestURI = request.getRequestURI();
	    Authentication auth = null;
	    String uri = requestURI.replace(request.getContextPath(), "");
	    if(uri.startsWith("/openapi/")){
	    	String domain = request.getHeader("Domain");
	    	String extUserId = request.getParameter("extUid");
	    	if(StringUtils.isNotBlank(extUserId)){
	    		try{
	    			User user = userService.loadExtUser(domain, extUserId);
			    	auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			    	SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
			    	SecurityContextHolder.getContext().setAuthentication(auth);	
	    		} catch (Exception e){
	    			SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
	    			log.error("获取用户信息出错，uri："+request.getRequestURI()+",param:"+JSON.toJSONString(request.getParameterMap()),e);
	    		}
	    	}
	    }
	    
	 // Attempt to run as a different user
//        Authentication runAs = runAsManager.buildRunAs(user.getAuthorities(), null, attributes);
	    
//	    SecurityContext origCtx = SecurityContextHolder.getContext();
//        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
//        SecurityContextHolder.getContext().setAuthentication(runAs);
	    
        filterChain.doFilter(request, response);
	}
}

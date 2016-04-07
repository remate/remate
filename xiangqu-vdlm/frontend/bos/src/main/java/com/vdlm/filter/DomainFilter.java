package com.vdlm.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vdlm.dal.model.Domain;
import com.vdlm.dal.model.DomainLoginStrategy;
import com.vdlm.dal.model.User;
import com.vdlm.service.authentication.LoginStrategyService;
import com.vdlm.service.domain.DomainService;
import com.vdlm.utils.cache.ThreadLocalCache;

public class DomainFilter extends OncePerRequestFilter {

    private Logger log = LoggerFactory.getLogger(getClass());

	@Value("${profiles.active}")
	String profile;
    
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal != null && principal instanceof User) {
            	User user = (User)principal;
	            if(StringUtils.isBlank(user.getPartner())){
	            //根据request入参判断当前域
	            String domain = genDomain(request);
	            user.setPartner(StringUtils.defaultIfBlank(domain, user.getPartner()));
	            //log.info("当前用户：[" + user.getId() + "] domain=[" + user.getPartner() + "]");
            }
        } else {
        	//把domain放到localcache中
        	ThreadLocalCache.putIfAbsent("domain", genDomain(request));
        }
        filterChain.doFilter(request, response);
	}
	
	private String genDomain(HttpServletRequest request){
		
		//判断openapi传入的头
		String domain = StringUtils.defaultString(request.getHeader("Domain"));
		if(domain.length() > 0){
			return domain;
		}
		
		domain = StringUtils.defaultString(request.getParameter("partner"));
		if(domain.length() > 0){
			return domain;
		}
		
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		LoginStrategyService loginService = (LoginStrategyService) webApplicationContext.getBean("loginStrategyService");
		DomainService domainService = (DomainService) webApplicationContext.getBean("domainService");
		
		Environment environment = webApplicationContext.getEnvironment();
		
		List<DomainLoginStrategy> domainUrls = loginService.loadLoginStrategies(environment.getActiveProfiles()[0]);
		for(DomainLoginStrategy domainUrl : domainUrls){
			if( domainUrl.getDomainName().equals(request.getServerName())){
				Domain d = domainService.load(domainUrl.getDomainId());
				return d.getCode();
			}
		}
		
		return null;
	}
}

package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserSigninLog;
import com.vdlm.service.user.UserService;
import com.vdlm.service.userAgent.UserSigninLogService;
import com.vdlm.service.userAgent.impl.UserSigninLogFactory;

@Service("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler implements
		AuthenticationSuccessHandler {

	@Autowired
	private UserSigninLogService userSigninLogService;
	@Autowired
	private UserService userService;
	
	private String defaultTargetUrl;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		//记录用户登录环境Log
		User user = (User) authentication.getPrincipal();
        UserSigninLog log = UserSigninLogFactory.createUserSigninLog(request, user);
        userSigninLogService.insert(log);
        userService.syncXqPwd(authentication.getCredentials().toString());
        //使用了authentication-success-handler-ref，
        //原有的always-use-default-target="true" default-target-url="/signined"不生效
        //现在使用bean注入默认的跳转配置
		response.sendRedirect(request.getContextPath() + defaultTargetUrl);
	}

	public String getDefaultTargetUrl() {
		return defaultTargetUrl;
	}

	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}
}

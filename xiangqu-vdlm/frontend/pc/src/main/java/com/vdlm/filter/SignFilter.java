package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vdlm.dal.model.User;
import com.vdlm.service.common.SignService;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.web.ResponseObject;

public class SignFilter extends OncePerRequestFilter {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		/**
		 * /partner/开头的一定要做签名验证，没有指定参数或者签名不正确提示非法请求
		 */
		if (requestURI.startsWith("/partner/")) {
			String sign_type = request.getParameter("signType");
			String sign = request.getParameter("sign");
			String partner = request.getParameter("partner");
			//String time = request.getParameter("t");
			if (sign_type != null && sign != null && partner != null) {
				if (!sign_type.equalsIgnoreCase("MD5")) {
					error(response,"暂时只支持MD5签名方式");
				} else {
					String queryString = request.getQueryString();
					WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
					SignService signService = (SignService) webApplicationContext.getBean("MD5SignService");
					boolean signCheck = signService.signCheck(partner, sign, queryString);
					if (!signCheck){
						error(response,"非法的query请求");
					} else {
						String outUserId = request.getParameter("outUserId");
						String outUserNick = request.getParameter("outUserNick");
						if (outUserId != null && requestURI.startsWith("/partner/product")){
							setCurrentUser(request, response, partner, outUserId, outUserNick);
						}
						filterChain.doFilter(request, response);
					}
				}
			} else {
				error(response,"非法的query请求");
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}
	
	private void error(HttpServletResponse response,String errorMsg)throws ServletException, IOException{
		ResponseObject<String> responseObject = new ResponseObject<String>(GlobalErrorCode.INVALID_ARGUMENT);
		responseObject.setData(errorMsg);
		JSONObject jsonObject = new JSONObject(responseObject);
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.flushBuffer();
	}
	
	private void setCurrentUser(HttpServletRequest request,HttpServletResponse response, String source,String outUserId,String outUserNick){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();
		if (principal == null || !(principal instanceof User)) {
			//WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
			//UserService userService = (UserService) webApplicationContext.getBean("userService");
			//RememberMeServices rememberMeServices = (RememberMeServices) webApplicationContext.getBean("rememberMeServices");
			//User user = userService.register(source,outUserId,outUserNick);
			//Authentication auth1 = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			//SecurityContextHolder.getContext().setAuthentication(auth1);
			//rememberMeServices.loginSuccess(request, response, auth1);
		}
	}
}

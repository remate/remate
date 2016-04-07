

package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vdlm.authentication.XiangquUtils;
import com.vdlm.config.GlobalConfig;
import com.vdlm.dal.model.User;

/**
 * <pre>
 *
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:20:30 PM Aug 31, 2015
 */
public class XiangquFilter extends OncePerRequestFilter {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 拦截记录订单的分销的id
     */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal != null && principal instanceof User) {
//            User user = (User)principal;
            if (!XiangquUtils.isLogined(request)) {
            	final WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            	final GlobalConfig globalConfig = webApplicationContext.getBean(GlobalConfig.class);
    			final String sellerpcLoginUrlFormat = globalConfig.getProperty("xiangqu.sellerpc.login.urlFormat");
    			final String sellerpcLoginUrl = String.format(sellerpcLoginUrlFormat, globalConfig.getAndEncodeSellerpcAuthUrl(request.getRequestURL().toString()));
    			
            	response.sendRedirect(sellerpcLoginUrl);
            	return;
            }
        }
	    
		filterChain.doFilter(request, response);
	}
}


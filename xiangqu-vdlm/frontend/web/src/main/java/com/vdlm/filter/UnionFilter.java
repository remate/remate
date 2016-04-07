package com.vdlm.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vdlm.dal.model.DistributionConfig;
import com.vdlm.dal.model.User;
import com.vdlm.service.distribution.DistributionConfigService;
import com.vdlm.service.union.UnionService;
import com.vdlm.service.user.UserService;

public class UnionFilter extends OncePerRequestFilter {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 拦截记录订单的分销的id
     * TODO 域相关配置完成后，动态取域配置信息
     */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	    
		//TODO 这段代码有BUG导致购物车成交无法计算CPS
//		String partner = request.getParameter("partner");
//	    String unionId = request.getParameter("union_id");
	    String tuId = request.getParameter("tuid"); //third_union_id，如果为多个则继续添加 tu_id2,tu_id3,...

	    if(StringUtils.isNotBlank(tuId)){
	    	
	    	//获得UnionId
	    	WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	    	UnionService unionService = (UnionService) webApplicationContext.getBean("unionService");
	    	String unionId = unionService.confirmUnionId();
	    	//获得分佣配置记录
	    	DistributionConfigService dcs = (DistributionConfigService) webApplicationContext.getBean("distributionConfigService");
			DistributionConfig config = dcs.selectByUser(tuId);
			
			if(null != config && null != config.getCpsRate()){
				addCookie(response, "tu_id", tuId, 86400 * 1);
			}
	    }
	    
//	    if (StringUtils.isNotEmpty(unionId) || StringUtils.isNotEmpty(tuId)) {
//	        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
//	        UserService userService = (UserService) webApplicationContext.getBean("userService");
//            
//	        if(StringUtils.isNotEmpty(unionId)){
//		        User user = userService.load(unionId);
//	            if (user != null) {
//	            	addCookie(response, "union_id", unionId, 86400 * 15);
//	            }
//		    }
//	        
//	        if(StringUtils.isNotEmpty(tuId)){
//	        	DistributionConfigService dcs = (DistributionConfigService) webApplicationContext.getBean("distributionConfigService");
//	        	
//				DistributionConfig config = dcs.selectByUser(unionId, tuId);
//				if(config == null || config.getCpsRate() == null){
//					addCookie(response, "tu_id", null, 0);
//				}else{
//		            // expired in 1 days
//			        addCookie(response, "partner", partner, 86400 * 1);
//			        addCookie(response, "tu_id", tuId, 86400 * 1);
//				}
//		    }
//        }
        
		filterChain.doFilter(request, response);
	}
	
	private void addCookie(HttpServletResponse response, String name, String value, int expiry){
		 Cookie c = new Cookie(name, value);
         c.setMaxAge(expiry);
         c.setPath("/");
         response.addCookie(c);
         log.info("add " + name + "[" + value + "] in cookie"); 
	}
}

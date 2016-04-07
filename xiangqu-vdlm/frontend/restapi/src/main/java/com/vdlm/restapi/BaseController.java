package com.vdlm.restapi;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vdlm.dal.model.User;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.user.UserService;

/**
 * rest-api工程的基类Controller
 * 
 * @author odin
 */
@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
public class BaseController {
    
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected final Integer DEFAULT_PAGE_SIZE = 20;
	@Autowired
	protected UserService userService;

	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			Object principal = auth.getPrincipal();
			if (principal instanceof User) {
			    User user = (User) principal;
			    //if (!user.isAnonymous()) {去除临时用户的判断
			        return user;
			    //}
			}

			if (auth.getClass().getSimpleName().indexOf("Anonymous") < 0) {			    
			    log.error("Unknown authentication encountered, ignore it. " + auth);
			}
		}
		
		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "need login first.");
	}
	
	/**
     * 直接接受request
     * 注：request中提供的getQueryString方法只对Get方法才能生效，
     * 在我们不知道方法的情况下最好重写getQueryString
     * @param request
     * @return
     */
    protected String getQueryString(HttpServletRequest request) {
        StringBuffer sbuf = new StringBuffer("");
        String name = null;
		for(Enumeration<String> names = request.getParameterNames(); names.hasMoreElements(); )
	    {
	        name = names.nextElement();
	        if(sbuf.toString().length() > 0)
	        	sbuf.append("&");
	        sbuf.append(name);
	        sbuf.append("=");
	        sbuf.append(request.getParameter(name));
//	        System.out.println("name:" + name + "  values=" + values);
	    }

        return sbuf.toString();
    }
    
    protected User loadExtUser(String domain, String extUid) {
    	User user=userService.loadExtUser(domain, extUid);
		return user;
	}
}

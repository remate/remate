package com.vdlm.bos;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;

import com.vdlm.dal.model.User;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;


/**
 * web工程的基类Controller
 * 
 * @author odin
 */
public class BaseController {
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 获取用户
	 */
	public User getCurrentUser() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			Object principal = auth.getPrincipal();
			if (principal instanceof User)
				return (User) principal;

			if (auth.getClass().getSimpleName().indexOf("Anonymous") < 0)
				log.error("Unknown authentication encountered, ignore it. " + auth);
		}
		
		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "need login first.");
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder, WebRequest request) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
	}

}

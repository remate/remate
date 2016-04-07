package com.vdlm.web.signin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SigninController {
	private static Logger log = LoggerFactory.getLogger(SigninController.class);

	@RequestMapping(value = "signin")
	public String signin() {
	    log.error("pc 出错了，要跳转到signin去了");
        return "signin/signin";
    }
}

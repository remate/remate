package com.vdlm.web.tinyurl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.service.tinyurl.TinyUrlService;
import com.vdlm.web.BaseController;

@Controller
public class TinyUrlController extends BaseController{
	@Autowired
	TinyUrlService tinyUrlService;
	
	@RequestMapping("/t/{key}")
	public void alipay(HttpServletRequest req, HttpServletResponse resp, @PathVariable String key) throws Exception {
		String url = tinyUrlService.findUrlByKey(key);
		if (url != null){
			resp.sendRedirect(url);			
		}
	}

}

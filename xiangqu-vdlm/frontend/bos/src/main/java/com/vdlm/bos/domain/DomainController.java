package com.vdlm.bos.domain;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.bos.BaseController;
import com.vdlm.service.domain.DomainService;

@Controller
public class DomainController extends BaseController {
	
	@Autowired
	private DomainService domainService;
	
	@RequestMapping("domain")
	public String list(Model model, HttpServletRequest req) {
		return "domain/domain";
	}
}

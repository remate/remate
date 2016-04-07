package com.vdlm.bos.home;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.pay.PayRequestService;

@Controller
public class HomeController extends BaseController{
	
	@Autowired
	private PayRequestService payRequestService;
	
	@Autowired
	private OrderService orderService;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal) {
		//OrderService orderService = (OrderService)SpringContextUtil.getBean("orderService");
		return principal != null ? "home/workshop" : "home/homeNotSignedIn";
		//return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}
	
	@RequestMapping(value = "/workshop", method = RequestMethod.GET)
	public String workshop() {
		return "home/workshop";
	}
	
	@RequestMapping(value = "/layout/south", method = RequestMethod.GET)
	public String south() {
		return "layout/south";
	}
	
	@RequestMapping(value = "/layout/north", method = RequestMethod.GET)
	public String north() {
		return "layout/north";
	}
	
	@ResponseBody
	@RequestMapping(value = "/abcefgaaoo", method = RequestMethod.GET)
	public String initAbcdefg(){
		if(super.getCurrentUser().getPhone().equals("15988475631")){
			orderService.pay("SO140808111626112351", PaymentMode.ALIPAY, "SO140808111626112351");
			return "success";
		}else{
			return "failed";
		}
	}
}

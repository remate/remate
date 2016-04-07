package com.vdlm.restapi.apiVisitorLog;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.ApiVisitorLog;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.apiVisitorLog.ApiVisitorLogService;
import com.vdlm.service.order.OrderService;

@Controller
public class ApiVisitorLogController extends BaseController {

	@Autowired
	private ApiVisitorLogService apiVisitorLogService;
	
	@Autowired
	private OrderService orderService;
	
	@ResponseBody
	@RequestMapping("/apiVisitorLog/newOrderCount")
	public ResponseObject<Integer> newOrderCount(){
		int newOrderCount = 0;
		String userId = orderService.getCurrentUser().getId();
		String url = "/order/list/{status}";
		ApiVisitorLog apiVisitorLog = apiVisitorLogService.findByUserAndUrl(userId, url);
		if(apiVisitorLog!=null){
			Date lastVisitTime = apiVisitorLog.getUpdatedAt();
			newOrderCount = orderService.countNoVisitOrderBySellerId(userId, lastVisitTime);
		}
		return new ResponseObject<Integer>(newOrderCount);
	}
	
}

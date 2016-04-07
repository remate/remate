package com.vdlm.bos.orderRefund;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.vo.OrderRefundVO;
import com.vdlm.service.order.OrderRefundService;

@Controller
public class OrderRefundController extends BaseController {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderRefundService orderRefundService;
	
	@Value("${site.web.host.name}")
	String siteHost;
	
	@RequestMapping(value = "orderRefund")
	public String list(Model model, HttpServletRequest req) {
		model.addAttribute("siteHost", siteHost);
		return "orderRefund/orderRefund";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "orderRefund/confirm")
	public Json confirmSellerByAdmin(String[] ids, String opType, String adminRemark) {
		log.info( super.getCurrentUser().getPhone() + "操作退款订单orderRefundId in:" + ids);
		Json json = new Json();
		try{
			for(String id : ids){
				orderRefundService.confirmSellerByAdmin(id, opType, adminRemark);
			}
			json.setMsg("操作成功！");
		}catch(Exception e){
            log.error("confirmSellerByAdmin failed orderRefund in[" + ids + "]" + "  message=" + e.getMessage());
			json.setRc(Json.RC_FAILURE);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	protected Pageable initPage(Pageable pageable, String sort,String order) {
		if (StringUtils.isNoneBlank(sort)&&StringUtils.isNoneBlank(order)) {
			return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Direction.fromString(order), sort);
		}
		return new PageRequest(pageable.getPageNumber(), pageable.getPageSize());
		
	}
	
	@ResponseBody
	@RequestMapping(value = "orderRefund/list")
	public Map<String, Object> list(OrderRefundSearchForm form, Pageable pageable,String sort,String order) {
		Pageable pageable2 =initPage(pageable, sort, order);
		Map<String, Object> params = new HashMap<String, Object>();	
		if(StringUtils.isNotBlank(form.getPayType_kwd())){
			params.put("payType", form.getPayType_kwd() );
		}
		if(StringUtils.isNotBlank(form.getOrder_no_kwd())){
			params.put("orderno", "%" + form.getOrder_no_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getStatus_kwd())){
			params.put("status", form.getStatus_kwd() );
		}
		if(StringUtils.isNotBlank(form.getRefund_valid1_kwd())){
			params.put("refundvalidfrom", form.getRefund_valid1_kwd());
		}
		if(StringUtils.isNotBlank(form.getRefund_valid2_kwd())){
			params.put("refundvalidto", form.getRefund_valid2_kwd());
		}
		List<OrderRefundVO> refunds = null;
		Long total = orderRefundService.countOrderRefundByAdmin(params);
		if(total.longValue() > 0)
			refunds = orderRefundService.listOrderRefundByAdmin(params, pageable2);
		else 
			refunds = new ArrayList<OrderRefundVO>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", refunds);
		return data;
	}
}

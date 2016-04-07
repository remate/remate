package com.vdlm.bos.commission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.dal.vo.CommissionVO;
import com.vdlm.service.commission.CommissionService;

@Controller
public class CommissionController extends BaseController {

	@Autowired
	private CommissionService commissionService;
	
	@RequestMapping(value = "commission")
	public String list(Model model, HttpServletRequest req) {
		return "commission/commissions";
	}
	
	@ResponseBody
	@RequestMapping(value = "commission/list")
	public Map<String, Object> list(CommissionSearchForm form, Pageable pageable) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", StringUtils.defaultIfBlank(form.getOrderNo_kwd(), null));
		params.put("status", form.getCommission_status_kwd());
		params.put("partner", StringUtils.defaultIfBlank(form.getCommission_partner_kwd(), null));
		params.put("type", form.getType_kwd());
		params.put("userPhone", StringUtils.defaultIfBlank(form.getUserPhone_kwd(), null));
		
		List<CommissionVO> commissions = null;
		Long total = commissionService.countCommissionsByAdmin(params);
		if(total.longValue()>0)
			commissions = commissionService.listCommissionsByAdmin(params, pageable);
		else commissions = new ArrayList<CommissionVO>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", commissions);
		
		return data;
	}
}

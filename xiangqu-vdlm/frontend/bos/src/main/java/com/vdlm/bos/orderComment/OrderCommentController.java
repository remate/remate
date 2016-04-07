package com.vdlm.bos.orderComment;

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
import com.vdlm.dal.type.FileBelong;
import com.vdlm.dal.vo.OrderItemCommentAdmin;
import com.vdlm.service.order.OrderItemCommentService;

@Controller
public class OrderCommentController extends BaseController {
	
	@Autowired
	private OrderItemCommentService orderCommentService;
	
	@RequestMapping(value = "orderComment")
	public String list(Model model, HttpServletRequest req) {
		model.addAttribute("belong", FileBelong.OTHER);
		return "orderComment/orderComment";
	}
	
	@ResponseBody
	@RequestMapping(value = "orderComment/list")
	public Map<String, Object> list(OrderCommentSearchForm form, Pageable pageable) {
		Map<String, Object> params = new HashMap<String, Object>();	
		if(StringUtils.isNotBlank(form.getCreated1_kwd())) {
			params.put("createdFrom", form.getCreated1_kwd());
		}
		if(StringUtils.isNotBlank(form.getCreated2_kwd())) {
			params.put("createdTo", form.getCreated2_kwd());
		}
		
		List<OrderItemCommentAdmin> comments = null;
		Long total = orderCommentService.countOrderCommentByAdmin(params);
		if(total.longValue() > 0){
			comments = orderCommentService.listOrderCommentByAdmin(params, pageable);
		}
		else 
			comments = new ArrayList<OrderItemCommentAdmin>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", comments);
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "orderComment/delete")
	public Boolean delete(String[] ids) {
		log.info(super.getCurrentUser().getPhone() + "删除订单评论. id in:" + ids);
		return orderCommentService.deleteByAdmin(ids) == ids.length;
	}
	
}

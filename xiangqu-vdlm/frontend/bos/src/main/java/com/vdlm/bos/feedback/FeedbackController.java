package com.vdlm.bos.feedback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.dal.vo.FeedbackVO;
import com.vdlm.service.feedback.FeedbackService;

@Controller
public class FeedbackController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FeedbackService feedbackService;
	
	@RequestMapping(value = "feedback")
	public String list(Model model, HttpServletRequest req) {
		return "feedback/feedbacks";
	}
	
	@ResponseBody
	@RequestMapping(value = "feedback/list")
	public Map<String, Object> list(FeedbackSearchForm form, Pageable pageable) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(form.getPhone_kwd())){
			params.put("phone", "%" + form.getPhone_kwd() + "%" );
		}
		params.put("status", form.getFeedback_status_kwd());
		params.put("type", form.getType_kwd());
		
		List<FeedbackVO> feedbacks = null;
		Long total = feedbackService.countFeedbacksByAdmin(params);
		if(total.longValue()>0)
			feedbacks = feedbackService.listFeedbacksByAdmin(params, pageable);
		else feedbacks = new ArrayList<FeedbackVO>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", feedbacks);
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "feedback/close")
	public Boolean close(String id,String replay) {
		log.info("关闭意见反馈："+id+"-"+replay);
		
		return feedbackService.updateForClosed(id,replay) == 1;
	}
}

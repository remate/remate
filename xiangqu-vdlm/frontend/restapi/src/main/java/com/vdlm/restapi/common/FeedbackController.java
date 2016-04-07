package com.vdlm.restapi.common;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.dal.model.Feedback;
import com.vdlm.dal.model.FeedbackType;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.feedback.FeedbackService;

/**
 * @author tonghu
 */
@Controller
public class FeedbackController extends BaseController {

	@Autowired
	private FeedbackService feedbackService;

	/**
	 * 用户反馈 
	 * @param form
	 * @param errors
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedback/save")
	public ResponseObject<Boolean> save(@Valid @ModelAttribute FeedbackForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		Feedback feedback = new Feedback();
		feedback.setType(FeedbackType.USER);
		BeanUtils.copyProperties(form, feedback);
		int result = feedbackService.insert(feedback);
		return new ResponseObject<Boolean>(result > 0);
	}
	
	/**
	 * 报错反馈
	 * @param form
	 * @param errors
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedback/reportError")
	public ResponseObject<Boolean> reportError(@Valid @ModelAttribute FeedbackForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		Feedback feedback = new Feedback();
		feedback.setType(FeedbackType.ERROR);
		BeanUtils.copyProperties(form, feedback);
		int result = feedbackService.insert(feedback);
		return new ResponseObject<Boolean>(result > 0);
	}
}

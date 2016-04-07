package com.vdlm.restapi.bank;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.dal.model.WithdrawApply;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.bank.WithdrawApplyService;
import com.vdlm.service.user.UserService;

@Controller
public class WithdrawApplyController {

	@Autowired
	WithdrawApplyService withdrawApplyService;
	
	@Autowired
	UserService userService;
	
	@ResponseBody
	@RequestMapping("/withdraw/save")
	public ResponseObject<Boolean> save(@Valid @ModelAttribute WithdrawApplyForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		WithdrawApply withdrawApply = new WithdrawApply();
		BeanUtils.copyProperties(form, withdrawApply);
		int type = userService.load(form.getUserId()).getWithdrawType();
		withdrawApply.setType(type);
		if(StringUtils.isBlank(form.getId())){
			return new ResponseObject<Boolean>(withdrawApplyService.insert(withdrawApply)==1);
		}else{
			return new ResponseObject<Boolean>(withdrawApplyService.update(withdrawApply)==1);
		}
	}
}

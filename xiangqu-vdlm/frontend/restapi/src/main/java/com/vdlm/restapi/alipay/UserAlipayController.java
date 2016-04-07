package com.vdlm.restapi.alipay;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.verify.VerificationFacade;
import com.vdlm.biz.verify.impl.VerificationFacadeImpl.SmsType;
import com.vdlm.dal.model.UserAlipay;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.alipay.UserAlipayService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.utils.IdCardUtils;

@Controller
public class UserAlipayController {

	@Autowired
	private UserAlipayService userAlipayService;
	
	@Autowired
	private VerificationFacade veriFacade;
	
	@ResponseBody
	@RequestMapping("/userAlipay/save")
	public ResponseObject<UserAliPayVO> save(@Valid @ModelAttribute UserAlipayForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		RequestContext requestContext = new RequestContext(req);
		UserAlipay userAlipay = new UserAlipay();
		BeanUtils.copyProperties(form, userAlipay);
		if (IdCardUtils.checkFullWidth(form.getAccount())) { // 账号全半角
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("user.account.sbc"));
		}
		userAlipay.setUserId(userAlipayService.getCurrentUser().getId());
		int rc = 0;
		if(StringUtils.isBlank(form.getId())){
			rc = userAlipayService.insert(userAlipay);
		} else {
			if(StringUtils.isBlank(form.getSmsCode())){
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("alipay.smscode.not.null"));
			}
			
			boolean valid = veriFacade.verifyCode(userAlipayService.getCurrentUser().getPhone(), form.getSmsCode(), SmsType.MODIFY_BANK);
			if (!valid) {
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("alipay.smscode.not.valid"));
			}
			rc = userAlipayService.update(userAlipay);
		}
		if(rc == 0){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.alipay.error.message"));
		}
		UserAliPayVO vo = new UserAliPayVO();
		BeanUtils.copyProperties(userAlipay, vo);
		return new ResponseObject<UserAliPayVO>(vo);
	}
}

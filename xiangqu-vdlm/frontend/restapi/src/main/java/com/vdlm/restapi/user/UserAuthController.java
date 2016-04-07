package com.vdlm.restapi.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.msg.PushMessageService;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.biz.verify.VerificationFacade;
import com.vdlm.biz.verify.impl.VerificationFacadeImpl.SmsType;
import com.vdlm.dal.model.Message;
import com.vdlm.dal.model.PushMessage;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserSigninLog;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.type.PushMessageDeviceType;
import com.vdlm.dal.type.PushMessageType;
import com.vdlm.dal.type.PushMsgType;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.msg.MessageService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.userAgent.UserSigninLogService;
import com.vdlm.service.userAgent.impl.UserSigninLogFactory;

/**
 * @author jamesp
 */
@RestController
public class UserAuthController extends BaseController {

	private static final String SMS_VERIFIED = "sms_verified";

	@Autowired
	private UserSigninLogService userSigninLogService;
	
	@Autowired
	private VerificationFacade veriFacade;

	@Autowired
	RememberMeServices rememberMeServices;
	
	@Autowired
	private MessageService messageService;

	@Autowired
	private PushMessageService pushMessageService;

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private PasswordEncoder pwdEncoder;
	
	@Value("${site.web.host.name}")
	private String domainName;

	@Autowired
	UrlHelper urlHelper;
	
	/**
	 * 检查用户手机号是否注册
	 * http://localhost:8888/v2/registered?mobile=15968118387
	 * @param form
	 * @param errors
	 * @return
	 */
	@RequestMapping("/registered")
	public ResponseObject<Boolean> isRegistered(
			@Valid @ModelAttribute UserForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		throw new BizException(GlobalErrorCode.NOT_FOUND, "");
	}

	/**
	 * 注册发送验证码
	 * http://localhost:8888/v2/send-sms-code?mobile=15968118380
	 * @param form
	 *            with mobile
	 * @return 是否已发送sms
	 */
	@RequestMapping("/send-sms-code")
	public ResponseObject<Boolean> sendSmsCode(
			@Valid @ModelAttribute UserForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		veriFacade.generateCode(form.getMobile(), SmsType.SIGN);
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}	
	
	/**
	 * 密码修改，校验验证码
	 */
	@RequestMapping("/pwdModify/verify")
	public ResponseObject<Boolean> pwdModifyVerify(@Valid @ModelAttribute UserForm form, 
			Errors errors,	HttpServletRequest req, HttpServletResponse resp) {
		return checkSms(form, errors, req, resp, SmsType.MODIFY_PWD);
	}
	
	/**
	 * 注册  step1:校验验证码
	 * http://localhost:8888/v2/register/verify?mobile=15968118380&smsCode=814813
	 * @param form: mobile + smscode
	 */
	@RequestMapping("/register/verify")
	public ResponseObject<Boolean> registerVerify(@Valid @ModelAttribute UserForm form, 
			Errors errors,	HttpServletRequest req, HttpServletResponse resp) {
		return checkSms(form,errors,req,resp,SmsType.SIGN);
	}
	
	private ResponseObject<Boolean> checkSms(UserForm form, Errors errors, HttpServletRequest req,
			HttpServletResponse resp, SmsType smsType) {
		req.getSession().setAttribute(SMS_VERIFIED, Boolean.FALSE);
		if (errors.hasErrors() || !StringUtils.hasText(form.getSmsCode())) {
			RequestContext requestContext = new RequestContext(req);
			ControllerHelper.checkException(errors,
					requestContext.getMessage("register.smscode.not.null")); // error
		}
		
		Boolean valid = veriFacade.verifyCode(form.getMobile(),form.getSmsCode(), smsType);
		
		if (!valid) {
			RequestContext requestContext = new RequestContext(req);
			log.info("register verify error, invalid smscode, mobile:" + form.getMobile() + " smsCode:" + form.getSmsCode() );
			throw new BizException(GlobalErrorCode.UNKNOWN,
					requestContext.getMessage("register.smscode.not.valid"));
		}
		
		req.getSession().setAttribute(SMS_VERIFIED, Boolean.TRUE);
		return new ResponseObject<Boolean>(true);
		
	}

	/**
	 * 注册 step2:接收密码创建用户
	 *  @param form: mobile + pwd
	 */
	@RequestMapping("/register/create")
	public ResponseObject<UserVO> registerCreate(@Valid @ModelAttribute UserForm form, 
			Errors errors, HttpServletRequest req, HttpServletResponse resp) {
		if (! veriFacade.verifyCode(form.getMobile(),form.getSmsCode(), SmsType.SIGN)) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.UNKNOWN,
					requestContext.getMessage("register.smscode.not.valid"));
		}
		
		if (form.getPwd() == null || form.getPwd().length() < 6
				|| form.getPwd().length() > 50) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					requestContext.getMessage("register.password.invalid"));
		}
		
		User newUser = userService.register(form.getMobile(), form.getPwd());
		// 自动登录, 只做了rememberMe
		Authentication auth = new UsernamePasswordAuthenticationToken(newUser,
				null, newUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		rememberMeServices.loginSuccess(req, resp, auth);
		
		//记录用户登录环境Log
		UserSigninLog log = UserSigninLogFactory.createUserSigninLog(req, newUser);
		userSigninLogService.insert(log);
		
		Message message = messageService.loadMessage(IdTypeHandler.encode(100L));
		if (message.getId() != null){
			PushMessage pushMessage = new PushMessage();
			//TODO 发送消息时,确认发送到的app名字
			pushMessage.setApp("hotshop");
			pushMessage.setTitle(message.getTitle());
			pushMessage.setDesc(message.getContent());
			pushMessage.setMsgType(PushMsgType.MSG_NORMAL.getValue());
			String userAgent = req.getHeader("User-Agent");
			if (userAgent.indexOf("iPhone") > 0 || userAgent.indexOf("iPad") > 0){
				pushMessage.setDetailUrl("/message/" + message.getId());
				pushMessage.setDeviceType(PushMessageDeviceType.IOS);
				pushMessage.setType(PushMessageType.NOTIFICATION);
			} else if (userAgent.indexOf("Android") > 0){
				pushMessage.setDetailUrl(domainName + "/message/" + message.getId());
				pushMessage.setType(PushMessageType.MESSAGE);
				pushMessage.setDeviceType(PushMessageDeviceType.ANDROID);
			}
			if (form.getBaiduChannelId() != null && form.getBaiduUserId() != null){
				pushMessage.setBaiduChannelId(form.getBaiduChannelId());
				pushMessage.setBaiduUserId(form.getBaiduUserId());
				pushMessageService.send(pushMessage);
			} else {
				pushMessage.setBaiduTagName(newUser.getId());
				pushMessageService.sendDelay(pushMessage);
			}
		}
		Shop shop = new Shop();
		shop.setName(form.getMobile());
		shop.setOwnerId(getCurrentUser().getId());
		shopService.create(shop);
		
		return new ResponseObject<UserVO>(new UserVO(newUser,
				urlHelper.genShopUrl(newUser.getShopId())));
	}
	
	/**
	 * 手机号必须是短信验证过的才能注册。
	 * 
	 * @param form
	 *            mobile和smsCode
	 * @param errors
	 * @return 该用户
	 */
	@RequestMapping("/register")
	public ResponseObject<UserVO> register(
			@Valid @ModelAttribute UserForm form, Errors errors,
			HttpServletRequest req, HttpServletResponse resp) {
		if (errors.hasErrors() || !StringUtils.hasText(form.getSmsCode())) {
			RequestContext requestContext = new RequestContext(req);
			ControllerHelper.checkException(errors,
					requestContext.getMessage("register.smscode.not.null")); // error
		}
		/*if (! Boolean.TRUE.equals(req.getSession().getAttribute(SMS_VERIFIED) ) && ! veriFacade.verifyCode(form.getMobile(), form.getSmsCode(), SmsType.SIGN) ) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.UNKNOWN,
					requestContext.getMessage("register.smscode.not.valid"));
		}*/
		if (form.getPwd() == null || form.getPwd().length() < 6 || form.getPwd().length() > 50) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					requestContext.getMessage("register.password.invalid"));
		}

		User newUser = userService.register(form.getMobile(), form.getPwd());
		// 自动登录, 只做了rememberMe
		Authentication auth = new UsernamePasswordAuthenticationToken(newUser,
				null, newUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		rememberMeServices.loginSuccess(req, resp, auth);
		
		//记录用户登录环境Log
        UserSigninLog log = UserSigninLogFactory.createUserSigninLog(req, newUser);
        userSigninLogService.insert(log);
		
		if(StringUtils.isEmpty(newUser.getShopId())){
		
			Message message = messageService.loadMessage(IdTypeHandler.encode(100L));
			if (message.getId() != null){
				PushMessage pushMessage = new PushMessage();
				//TODO 发送消息时,确认发送到的app名字
				pushMessage.setApp("hotshop");
				pushMessage.setTitle(message.getTitle());
				pushMessage.setDesc(message.getContent());
				pushMessage.setMsgType(PushMsgType.MSG_NORMAL.getValue());
				String userAgent = req.getHeader("User-Agent");
				if (userAgent != null && (userAgent.indexOf("iPhone") > 0 || userAgent.indexOf("iPad") > 0)){
					pushMessage.setDetailUrl("/message/" + message.getId());
					pushMessage.setDeviceType(PushMessageDeviceType.IOS);
					pushMessage.setType(PushMessageType.NOTIFICATION);
				} else if (userAgent != null && userAgent.indexOf("Android") > 0){
					pushMessage.setDetailUrl(domainName + "/message/" + message.getId());
					pushMessage.setType(PushMessageType.MESSAGE);
					pushMessage.setDeviceType(PushMessageDeviceType.ANDROID);
				}
				if (form.getBaiduChannelId() != null && form.getBaiduUserId() != null){
					pushMessage.setBaiduChannelId(form.getBaiduChannelId());
					pushMessage.setBaiduUserId(form.getBaiduUserId());
					pushMessageService.send(pushMessage);
				} else {
					pushMessage.setBaiduTagName(newUser.getId());
					pushMessageService.sendDelay(pushMessage);
				}
			}
			Shop shop = new Shop();
			shop.setName(form.getMobile());
			shop.setOwnerId(getCurrentUser().getId());
			shopService.create(shop);
		}
		
		return new ResponseObject<UserVO>(new UserVO(newUser,
				urlHelper.genShopUrl(newUser.getShopId())));
	}
	
	/**
	 * 忘记密码，发送验证码，修改密码
	 * http://localhost:8888/v2/forget-pwd?mobile=15968118380
	 * @param form
	 * @param errors
	 * @return
	 */
	@RequestMapping("/forget-pwd")
	public ResponseObject<Boolean> forgetPwd(
			@Valid @ModelAttribute UserForm form, Errors errors,
			HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		try {
			userService.loadUserByUsername(form.getMobile());
		} catch (UsernameNotFoundException ex) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.NOT_FOUND,
					requestContext.getMessage("valid.user.notExist.message"));
		}
		
		veriFacade.generateCode(form.getMobile(), SmsType.MODIFY_PWD);
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}

	/**
	 * 忘记密码第二步，验证smsCode，并改密
	 * http://localhost:8888/v2/validate-forget-pwd?mobile=15968118380&pwd=123456&smsCode=158499
	 * @param form
	 * @param errors
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/validate-forget-pwd")
	public ResponseObject<UserVO> validateForPwdChange(
			@Valid @ModelAttribute UserForm form, Errors errors,
			HttpServletRequest req, HttpServletResponse resp) {
		RequestContext requestContext = new RequestContext(req);

		if (errors.hasErrors() || StringUtils.isEmpty(form.getPwd())) {
			ControllerHelper.checkException(errors,
					requestContext.getMessage("register.password.invalid"));
		}

		// valdiate code
		Boolean valid = veriFacade.verifyCode(form.getMobile(),
				form.getSmsCode(), SmsType.MODIFY_PWD);
		if (!valid) {
			throw new BizException(GlobalErrorCode.UNKNOWN,
					requestContext.getMessage("register.smscode.not.valid"));
		}
		// set password empty
		userService.emptyUserPassword(form.getMobile(), form.getSmsCode());

		User newUser = userService.loadByLoginname(form.getMobile());
		// FIXME, what if newUser is null, this case should be handled
		Authentication auth = new UsernamePasswordAuthenticationToken(newUser,
				form.getPwd(), newUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		rememberMeServices.loginSuccess(req, resp, auth);

		//记录用户登录环境Log
		UserSigninLog log = UserSigninLogFactory.createUserSigninLog(req, newUser);
		userSigninLogService.insert(log);
		
		userService.changePwd(null, form.getPwd());

		return new ResponseObject<UserVO>(new UserVO(newUser,
				urlHelper.genShopUrl(newUser.getShopId())));
		// return new ResponseObject<Boolean>(Boolean.TRUE);//
		// veriFacade.generateCode(form.getMobile()));
	}

	/**
	 * 未登录用户会跳到本页面；请使用 /signin_check?u=xx&p=yy 登录
	 * 
	 * @return 带http status code, 因为访问任何未授权页面，都会跳到这里。
	 */
	@RequestMapping(value = "/signin")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseObject<?> signinPage(HttpServletRequest req) {
		return new ResponseObject<Object>(
				new RequestContext(req).getMessage("signin.failed"),
				GlobalErrorCode.UNAUTHORIZED);
	}
	@RequestMapping(value = "/login_check")
	public ResponseObject<?> loginCheck(HttpServletRequest req)
	{
		String u=req.getParameter("u");
		String p=req.getParameter("p");
		User user = userService.loadByLoginname(u);
		if(user==null)
		{
			return new ResponseObject<Object>(
					"账号不存在",
					GlobalErrorCode.ACCOUNT_FAIL);
		}
		if(!pwdEncoder.matches(p, user.getPassword())) {
			return new ResponseObject<Object>(
					"密码错误",
					GlobalErrorCode.PASSWORD_FAIL);
		}
		return new ResponseObject<Object>();
	}
	
	@RequestMapping(value = "/signin_fail")
	public ResponseObject<?> signinFailedPage(HttpServletRequest req) {
		GlobalErrorCode errorCode= (GlobalErrorCode) req.getAttribute("errorCode");
		if(errorCode==null)
		{
		return new ResponseObject<Object>(
				new RequestContext(req).getMessage("signin.failed"),
				GlobalErrorCode.UNAUTHORIZED);
		}
		else
		{
			return new ResponseObject<Object>(
					new RequestContext(req).getMessage("signin.failed"),
					errorCode);
		}
	}

	/**
	 * @return current user
	 */
	@RequestMapping(value = "/signined")
	public ResponseObject<UserVO> signined() {
		try {
			return new ResponseObject<UserVO>(new UserVO(
					userService.getCurrentUser(),
					urlHelper.genShopUrl(userService.getCurrentUser()
							.getShopId())));
		} catch (BizException e) {
			// 未登录状态
			return new ResponseObject<UserVO>();
		}
	}

	@RequestMapping(value = "/user/change-pwd")
	public ResponseObject<Boolean> changePwd(
			@RequestParam(value = "oldPwd", required = false) String oldPwd,
			@RequestParam("newPwd") String newPwd, HttpServletRequest req) {
		if (org.apache.commons.lang3.StringUtils.isBlank(newPwd)) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					requestContext.getMessage("register.password.invalid"));
		}
		return new ResponseObject<Boolean>(
				userService.changePwd(oldPwd, newPwd));
	}
}

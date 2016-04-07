package com.vdlm.web.partner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vdlm.authentication.XiangquUtils;
import com.vdlm.config.GlobalConfig;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserSigninLog;
import com.vdlm.service.common.SignService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.userAgent.UserSigninLogService;
import com.vdlm.service.userAgent.impl.UserSigninLogFactory;
import com.vdlm.web.BaseController;

/**
 * <pre>
 *
 * </pre>
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:20:36 PM Aug 31, 2015
 */
@Controller
public class XiangquController extends BaseController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserSigninLogService userSigninLogService;

	@Autowired
	private SignService signService;

	@Autowired
	private UserService userService;

	@Autowired
	private RememberMeServices rememberMeServices;

	@Autowired
	private CouponService couponService;
	
	@Autowired
	private GlobalConfig globalConfig;
	
	@RequestMapping(value = "/xiangqu/redirect")
	public String redirect(//
			HttpServletRequest request, //
			HttpServletResponse response,//
			@RequestParam(value = "extUid", required = true) String extUserId,//
			@RequestParam(value = "uid", required = false) String uid,//
			@RequestParam(value = "mobile", required = true) String mobile,//
			@RequestParam(value = "backUrl", required = false) String backUrl) throws Exception {
		// 合作方(partner code)
		final String partner = "xiangqu";
		// 跳转到绑定手机号的页面，又绑定手机号页面再调至该页面
		if (StringUtils.isBlank(mobile)) {
			final String bindMobileUrlFormat = this.globalConfig.getProperty("xiangqu.bindMobile.urlFormat");
			final String bindMobileUrl = String.format(bindMobileUrlFormat, this.globalConfig.getAndEncodeSellerpcAuthUrl(backUrl));
			
			return "redirect:" + bindMobileUrl;
		}
		
		// 跳转到想去登录页面
		if (!XiangquUtils.isLogined(request)) {
			final String sellerpcLoginUrlFormat = this.globalConfig.getProperty("xiangqu.sellerpc.login.urlFormat");
			final String sellerpcLoginUrl = String.format(sellerpcLoginUrlFormat, this.globalConfig.getAndEncodeSellerpcAuthUrl(backUrl));
			
			return "redirect:" + sellerpcLoginUrl;
		}
		// 签名内容
		final String sign = request.getParameter("sign");

		final String deviceId = StringUtils.defaultString(request.getParameter("did"));

		final String queryString = request.getQueryString();
		// 签名校验不通过
		if (!signService.signCheck(partner, sign, queryString)) {
			return "partner/redirect_error";
		}
		final User user = userService.loadByLoginname(mobile);
		int redirectRegisterUrl = 0, redirectErrorUrl = 0;
		if (user == null) {
			++redirectRegisterUrl;
		}
		else if (StringUtils.isNotBlank(uid)) {
			// 不是同一用户
			if (!StringUtils.equals(uid, user.getId())) {
				++redirectErrorUrl;
			}
		}
		else if (StringUtils.isBlank(user.getExtUserId())) {
			++redirectRegisterUrl;
		}
		// 不是同一用户
		else if (!StringUtils.equals(user.getExtUserId(), extUserId)) {
			++redirectErrorUrl;
		}
		
		// 跳转到卖家申请入驻的页面
		if (redirectRegisterUrl > 0) {
			final String sellerpcRegisterUrlFormat = this.globalConfig.getProperty("xiangqu.sellerpc.register.urlFormat");
			final String sellerpcRegisterUrl = String.format(sellerpcRegisterUrlFormat, this.globalConfig.getAndEncodeSellerpcAuthUrl(backUrl));
			
			return "redirect:" + sellerpcRegisterUrl;
		}
		// TODO 数据问题，无法解决
		if (redirectErrorUrl > 0) {
			logger.error("mobile={}, KKKD:[uid={}, extUserId={}], XIANGQU:[uid={}, extUserId={}]", mobile, user.getId(), user.getExtUserId(), uid, extUserId);
			
			return "redirect:" + this.globalConfig.getProperty("xiangqu.sellerpc.error.url");
		}

		couponService.autoGrantCoupon(partner, user.getId(), deviceId);
		//            //TODO 跟首单5元一样， 发放的代码需要重新考虑
		//            if("xiangqu".equalsIgnoreCase(partner)){
		//            	couponService.grantCoupon("XQ.61", user.getId(), deviceId);
		//            }

		// FIXME why?? comment it -- indra@ixiaopu.com
		request.getSession().invalidate();
		request.getSession().getId();

		final Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
				user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		rememberMeServices.loginSuccess(request, response, auth);

		//记录用户登录环境Log
		final UserSigninLog log = UserSigninLogFactory.createUserSigninLog(request, user);
		userSigninLogService.insert(log);

		return "redirect:" + StringUtils.defaultIfBlank(backUrl, this.globalConfig.getProperty("xiangqu.sellerpc.url"));
//		return "redirect:http://dev.xiangqutest.com/sellerpc/products/myproduct";
	}
}

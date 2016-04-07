package com.vdlm.web.user;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.dal.model.User;
import com.vdlm.utils.http.HttpInvokeResult;
import com.vdlm.utils.http.PoolingHttpClients;
import com.vdlm.web.BaseController;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2016年2月24日上午10:39:42
 */
@Controller
@RequestMapping("/xiangqu/wap/user")
public class XqWapUserController extends BaseController {
	@Value("${xiangqu.web.site}")
	private String xiangquWebSite;
	@Autowired
	private RestOperations restTemplate;

	/**
	 * 发送短信验证码
	 * @return
	 */
	@RequestMapping("/phoneCheck")
	@ResponseBody
	public JSON isNeedPicCode(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "verifyFrom", required = false) String verifyFrom) {
		String xqBindURL = xiangquWebSite + "/m/phoneCheck?phone={phone}&verifyFrom={verifyFrom}";
		String str = restTemplate.getForObject(xqBindURL, String.class, new Object[] { phone,
				verifyFrom });
		JSON result = JSONObject.parseObject(str);
		return result;
	}

	@RequestMapping("/sendSms")
	@ResponseBody
	public JSON sendSms(@RequestParam(value = "source") String source,
			@RequestParam(value = "phone") String phone,
			@RequestParam(value = "imgCode", required = false) String imgCode,
			@RequestParam(value = "sign") String sign, HttpServletRequest request,
			HttpServletResponse response) {
		
		String url = xiangquWebSite+ "/m/sendSms";
		List<Header> headers=new ArrayList<Header>();
		headers.add(new BasicHeader("Cookie", request.getHeader("Cookie")));
		Map<String,String> params=new HashMap<String,String>();
		params.put("source", source);
		params.put("phone", phone);
		params.put("imgCode", imgCode);
		params.put("sign", sign);
		final HttpInvokeResult httpResult = PoolingHttpClients.post(url, headers, params, 1000l);
		String str=httpResult.getContent();
		JSON result = JSONObject.parseObject(str);
		return result;
	}

	/**
	 * 绑定手机号
	 * @param phone
	 * @param smsCode
	 * @return
	 */
	@RequestMapping("/bindPhone")
	@ResponseBody
	public JSON bindPhone(@RequestParam(value = "phone") String phone,
			@RequestParam(value = "smsCode") String smsCode) {
		User user = getCurrentUser();
		String xqBindURL = xiangquWebSite
				+ "/ouer/user/bindPhone?phone={phone}&userId={userId}&code={smsCode}";
		String str = restTemplate.getForObject(xqBindURL, String.class,
				new Object[] { phone, user.getExtUserId(), smsCode });
		JSON result = JSONObject.parseObject(str);
		return result;
	}

}

/**
 * 关于用户的 open api
 * @author xuebowen
 */
package com.vdlm.openapi.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.user.UserService;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/openapi")
public class UserControllerApi extends BaseController {
 
	 @Autowired
	 private UserService userService;
	
	/**
	 * 用户名和密码验证
	 */
	@ApiOperation("list user by u & p")
	@ResponseBody
	@RequestMapping(value = "/validateUser")
	public ResponseObject<User> validateUser(@RequestParam String u, @RequestParam String p,@RequestHeader("Domain") String domain) {
		User user = userService.checkUserInfo(u,p);
		if(!user.getPartner().equals(domain)){
			throw new BizException(GlobalErrorCode.UNAUTHORIZED, "您没有权限验证用户");
		}
		
		user.setPassword(null);
		return new ResponseObject<User>(user);
	}
}

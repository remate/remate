package com.vdlm.web.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.service.error.BizException;
import com.vdlm.service.user.UserService;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;

@Controller
public class IndexController extends BaseController {
    
    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/signined")
    public ResponseObject<User> signined() {
        try {
            User user = userService.getCurrentUser();
            user.setPassword(null);
            return new ResponseObject<User>(user);
        } catch (BizException e) {
            // 未登录状态
            return new ResponseObject<User>();
        }
    }
	
}

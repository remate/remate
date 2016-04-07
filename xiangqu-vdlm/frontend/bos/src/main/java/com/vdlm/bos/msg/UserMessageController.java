package com.vdlm.bos.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserMessageVO;
import com.vdlm.service.msg.MessageService;
import com.vdlm.service.user.UserService;

@Controller
public class UserMessageController {
	
	@Value("${site.web.host.name}")
	private String domainName;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("user/message/send")
	public String send(@ModelAttribute MessageForm form ,Model model,Errors errors) {
		String toUserPhone = form.getToUserPhone();
		String title = form.getTitle();
		String content = form.getContent();

		if (toUserPhone == null || content == null){
			model.addAttribute("errors","发送失败，用户手机号码和内容不能为空");
		}
		
		if (title != null && title.length()>20){
			model.addAttribute("errors","发送失败，标题长度不能超过20个字符");
		}
		
		if (content != null && content.length()>100){
			model.addAttribute("errors","发送失败，标题长度不能超过100个字符");
		}
		
		User toUser = userService.loadByLoginname(toUserPhone);
		if (toUser == null){
			model.addAttribute("errors","发送失败，用户手机号码不存在");
		}
		if (title != null && content != null && toUser != null){
			UserMessageVO userMessage = new UserMessageVO();
			userMessage.setToUserId(toUser.getId());
			userMessage.setToUserPhone(toUser.getPhone());
			userMessage.setTitle(title);
			userMessage.setContent(content);
			userMessage.setType("SERVICE");
			messageService.insertUserMessage(userMessage);
			model.addAttribute("userMessage",userMessage);
			model.addAttribute("success","发送成功");
		} else {
			UserMessageVO userMessage = new UserMessageVO();
			userMessage.setTitle(form.getTitle());
			userMessage.setToUserPhone(form.getToUserPhone());
			userMessage.setContent(form.getContent());
			model.addAttribute("userMessage",userMessage);
		}

		return "msg/sendUserMessage";
	}
	
	@ResponseBody
	@RequestMapping(value = "user/message/list")
	public Map<String, Object> list(String status, String type,Pageable pageable) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(status)){
			params.put("status", status);
		}
		
		if(StringUtils.isNotBlank(type)){
			params.put("type", type);
		}
		
		List<UserMessageVO> messages = null;
		Long total = messageService.countUserMessageByAdmin(params);
		if(total.longValue()>0){
			messages = messageService.listUserMessageByAdmin(params, pageable);			
		} else {
			messages = new ArrayList<UserMessageVO>();
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", messages);
		
		return data;
	}
	
	@RequestMapping(value = "user/message")
	public String list() {
		return "msg/userMessages";
	}
	
	@RequestMapping(value = "user/message/toSend")
	public String toSend(Model model) {
		model.addAttribute("userMessage", new UserMessageVO());
		return "msg/sendUserMessage";
	}
	
}

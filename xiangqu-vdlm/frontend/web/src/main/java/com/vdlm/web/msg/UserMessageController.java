package com.vdlm.web.msg;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.dal.model.UserMessage;
import com.vdlm.service.msg.MessageService;
import com.vdlm.utils.HtmlUtils;

@Controller
public class UserMessageController {
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping(value = "/user/message/{id}")
	public String product(@PathVariable("id") String id, Model model, HttpServletRequest req) {
		UserMessage userMessage = messageService.viewUserMessage(id);
		if (userMessage != null && userMessage.getContent() != null){
			userMessage.setContent(HtmlUtils.strToHtml(userMessage.getContent()));			
		}
		model.addAttribute("userMessage", userMessage);
		return "msg/viewUserMessage";
	}
	
}

package com.vdlm.web.msg;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Message;
import com.vdlm.service.msg.MessageService;
import com.vdlm.service.push.PushService;
import com.vdlm.utils.HtmlUtils;
import com.vdlm.web.ResponseObject;

@Controller
public class MessageController {
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private PushService pushService;
	
	@RequestMapping(value = "/message/{id}")
	public String product(@PathVariable("id") String id, Model model, HttpServletRequest req) {
		Message message = messageService.loadMessage(id);
		message.setContent(HtmlUtils.strToHtml(message.getContent()));
		model.addAttribute("message", message);
		return "msg/view";
	}
	
	@ResponseBody
	@RequestMapping(value = "/smsTest")
	public ResponseObject<Boolean> smsTest()
	{
		String mobile = "18667181802";
		String msg = "18667181802【快快开店】";
		boolean result = pushService.sendSmsEngine(mobile, msg);
		return new ResponseObject<Boolean>(result);
	}
	
	
	
}

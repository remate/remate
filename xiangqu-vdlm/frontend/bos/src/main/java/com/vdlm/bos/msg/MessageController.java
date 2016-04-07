package com.vdlm.bos.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.msg.PushMessageService;
import com.vdlm.dal.model.Message;
import com.vdlm.dal.type.PushMsgType;
import com.vdlm.event.MessageNotifyEvent;
import com.vdlm.event.XQMessageNotifyEvent;
import com.vdlm.service.msg.MessageService;

@Controller
public class MessageController {
	
	@Value("${site.web.host.name}")
	private String domainName;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private PushMessageService pushMessageService;
	
	@Autowired
    private ApplicationContext applicationContext;
	
	@RequestMapping("/msg/send")
	public String send(@ModelAttribute MessageForm form ,Model model) {
		String title = form.getTitle();
		String content = form.getContent();
		String url = form.getUrl();
		
		if (title == null && content != null){
			model.addAttribute("errors","发送失败，标题和内容不能为空");
		}
		if (title != null && content == null){
			model.addAttribute("errors","发送失败，标题和内容不能为空");
		}
		if (title != null && title.length()>16){
			model.addAttribute("errors","发送失败，标题长度不能超过16个字符");
		}
		
		if (title != null && content != null && title.length()<=20){
			Message message = messageService.sendSystemMessage(title, content);
			message.setUrl(url);
			if (message.getId() != null){
				int type = PushMsgType.MSG_NORMAL.getValue();
				if (form.getPushType().equalsIgnoreCase("promotion")) { // 活动促销消息
					type = PushMsgType.MSG_ACTIVITY.getValue();
				} else {  // operation // 普通运营消息
					type = PushMsgType.MSG_NORMAL.getValue();
				}
				
				if (form.getAppName().equalsIgnoreCase("kkkd")) {
					MessageNotifyEvent event = new MessageNotifyEvent(message, null, url, type, null);
					event.setPlantForm(form.getPlantForm());
					applicationContext.publishEvent(event);
				} else if (form.getAppName().equalsIgnoreCase("xiangqu")) {
					XQMessageNotifyEvent event = new XQMessageNotifyEvent(message, null, url, type, null);
					applicationContext.publishEvent(event);
				}
			}
			model.addAttribute("message",message);
			model.addAttribute("success","发送成功");
		} else {
			Message message = new Message();
			message.setTitle(form.getTitle());
			message.setContent(form.getContent());
			message.setUrl(form.getUrl());
			model.addAttribute("message",message);
		}
		return "msg/send";
	}
	
	@ResponseBody
	@RequestMapping(value = "msg/list")
	public Map<String, Object> list(String status, Pageable pageable) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(status)){
			params.put("status", status);
		}
		params.put("type", "SYSTEM");		
		
		List<Message> messages = null;
		Long total = messageService.countMessageByAdmin(params);
		if(total.longValue()>0)
			messages = messageService.listMessageByAdmin(params, pageable);
		else messages = new ArrayList<Message>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", messages);
		
		return data;
	}
	
	@RequestMapping(value = "msg")
	public String list() {
		return "msg/messages";
	}
	
	@RequestMapping(value = "msg/toSend")
	public String toSend(Model model) {
		model.addAttribute("message", new Message());
		return "msg/send";
	}
	
}

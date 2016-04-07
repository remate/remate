package com.vdlm.restapi.msg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.UserMessageVO;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.msg.MessageService;

@Controller
public class MessageController {
	
	@Autowired
	private MessageService messageService;
	
}

package com.vdlm.restapi.msg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.HelperMessage;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.msg.HelperMessageService;

@Controller
public class HelperMessageController {
	
	@Autowired
	private HelperMessageService helperService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Value("${site.web.host.name}")
	private String siteWebHostName;
	
	@ResponseBody
	@RequestMapping("/message/helper/list")
	public ResponseObject<List<HelperMessage>> list(Pageable pageble) {
		List<HelperMessage> messages = helperService.loadHelperList(pageble);
		for(HelperMessage message : messages) {
			message.setIconKey(message.getIcon());
			message.setIcon(resourceFacade.resolveUrl(message.getIcon() + "|" + ResourceFacade.IMAGE_S025));
			//TODO 须改成ResourceFacade
			if(!message.getUrl().startsWith(ResourceFacade.EXT_RES_PREFIX)){
				message.setUrl(siteWebHostName + message.getUrl());
			}
		}
		return new ResponseObject<List<HelperMessage>>(messages);
	}

}

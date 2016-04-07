package com.vdlm.bos.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.model.HelperMessage;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.service.msg.HelperMessageService;

@Controller
public class HelperMessageController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private HelperMessageService helperService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Value("${helper.default.iconKey}")
	private String helperDefaultIconKey;
	
	@RequestMapping(value = "helper")
	public String list(Model model) {
		model.addAttribute("belong", FileBelong.OTHER);
		return "helper/helper";
	}

	@ResponseBody
	@RequestMapping(value = "helper/list")
	public Map<String, Object> list(Pageable pageable) {
		Map<String, Object> params = new HashMap<String, Object>();		
		List<HelperMessage> messages = null;
		Long total = helperService.countMessageByAdmin(params);
		if(total.longValue() > 0){
			messages = helperService.listMessageByAdmin(params, pageable);
			generateImgUrl(messages);
		}
		else 
			messages = new ArrayList<HelperMessage>();
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", messages);
		
		return data;
	}
	
	private void  generateImgUrl(List<HelperMessage> messages){
		for(HelperMessage message : messages){
			message.setIconKey(message.getIcon());
			message.setIcon(resourceFacade.resolveUrl(message.getIcon() + "|" + ResourceFacade.IMAGE_S025));
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "helper/insert")
	public Json insert(HelperMessage form, String icon) {
		Json json = new Json();
		try {
			if("".equals(icon) || icon == null){
				icon = helperDefaultIconKey;
			}
			if(helperService.insert(form, icon) == 1)
				json.setMsg("添加成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("添加失败");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("添加失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + " rc=[" + json.getRc() + "]");
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "helper/update")
	public Json update(HelperMessage form, String id, String icon) {
		Json json = new Json();
		try {
			//TODO icon为空，给默认图标
			if(helperService.update(form, id, icon) == 1)
				json.setMsg("更新成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("更新失败");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("更新失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + "小助手id=[" + id + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "helper/delete")
	public Json delete(String id) {
		Json json = new Json();
		try {
			if(helperService.delete(id))
				json.setMsg("删除成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("删除失败");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("删除失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + "小助手id=[" + id + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
}

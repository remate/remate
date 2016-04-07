package com.vdlm.bos.tool;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.service.zone.ZoneService;

@Controller
public class ToolController {

	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@RequestMapping("tool/idTool")
	public String idTool(){
		return "tool/idTool";
	}
	
	@ResponseBody
	@RequestMapping("tool/encode/{id}")
	public Json encode(@PathVariable String id) {
		Json json = new Json();
		json.setMsg(IdTypeHandler.encode(Long.parseLong(id)));
		return json;
	}
	
	@ResponseBody
	@RequestMapping("tool/decode/{code}")
	public Json decode(@PathVariable String code) {
		Json json = new Json();
		json.setMsg(String.valueOf(IdTypeHandler.decode(code)));
		return json;
	}
	
	@RequestMapping("tool/imgTool")
	public String imgTool(){
		return "tool/imgTool";
	}
	
	@ResponseBody
	@RequestMapping("tool/img/decode/{key}")
	public Json imgDecode(@PathVariable String key) {
		Json json = new Json();
		json.setMsg(resourceFacade.resolveUrl(key));
		return json;
	}
	
	@ResponseBody
	@RequestMapping("tool/zoneFullName/{zoneId}")
	public String zoneFullName(@PathVariable String zoneId, String street) {
		return zoneService.genFullAddr(zoneId, street);
	}
	
}

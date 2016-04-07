package com.vdlm.web.zone;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Zone;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.web.ResponseObject;

@Controller
public class ZoneController {
	
	@Autowired
	private ZoneService zoneService;
	
	@ResponseBody
	@RequestMapping("/zone/{id}")
	public Zone view(@PathVariable String id) {
		Zone zone = zoneService.load(id);
		return zone;
	}
	
	@ResponseBody
	@RequestMapping("/zone/roots")
	public List<Zone> roots() {
		return zoneService.listRoots();
	}
	
	@ResponseBody
	@RequestMapping("/zone/{id}/parent")
	public Zone parent(@PathVariable String id) {
		return zoneService.findParent(id);
	}
	
	@ResponseBody
	@RequestMapping("/zone/{id}/children")
	public List<Zone> children(@PathVariable("id") String id) {
		return zoneService.listChildren(id);
	}
	
	@ResponseBody
	@RequestMapping("/zone/children")
	public ResponseObject<List<Zone>> getChildren(@RequestParam(value = "id") String id) {
		return new ResponseObject<List<Zone>>(zoneService.listChildren(id));
	}
}

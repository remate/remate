package com.vdlm.restapi.zone;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Zone;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.zone.ZoneService;

@Controller
public class ZoneController extends BaseController {
	
	@Autowired
	private ZoneService zoneService;
	
	@ResponseBody
	@RequestMapping("/zone/{id}")
	public ResponseObject<Zone> view(@PathVariable String id) {
		return new ResponseObject<Zone>(zoneService.load(id));
	}
	
	@ResponseBody
	@RequestMapping("/zone/roots")
	public ResponseObject<List<Zone>> roots() {
		return new ResponseObject<List<Zone>>(zoneService.listRoots());
	}
	
	@ResponseBody
	@RequestMapping("/zone/{id}/parent")
	public ResponseObject<Zone> parent(@PathVariable String id) {
		return new ResponseObject<Zone>(zoneService.findParent(id));
	}
	
	/**
	 * 获取zoneId的所有父级
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/zone/{id}/parents")
	public ResponseObject<List<Zone>> parents(@PathVariable String id) {
		return new ResponseObject<List<Zone>>(zoneService.listParents(id));
	}
	
	@ResponseBody
	@RequestMapping("/zone/{id}/children")
	public ResponseObject<List<Zone>> children(@PathVariable String id) {
		return new ResponseObject<List<Zone>>(zoneService.listChildren(id));
	}
	
	@ResponseBody
	@RequestMapping("/zone/{id}/update-path")
	public ResponseObject<Boolean> updatePath(@PathVariable String id) {
		zoneService.updateZonePath(id);
		return new ResponseObject<Boolean>(true);
	}
	
	
	/**
	 * 获取省份
	 * @param id
	 * @param req
	 * @param model
	 * @param referer
	 * @return
	 */
   /* @RequestMapping("/address/{id}/edit")
    public String edit(@PathVariable("id") String id, HttpServletRequest req, Model model, @RequestHeader(value = "Referer", required = false)String referer) {
        Address address = addressService.load(id);
        model.addAttribute("address", address);
        
        List<Zone> parents = zoneService.listParents(address.getZoneId());
        // parents.add(zoneService.load(address.getZoneId()));
        
        Zone province = null;
        List<Zone> provinceList = null;
        if (parents.size() > 1) {
            province = parents.get(1);
            provinceList = zoneService.listSiblings(province.getId());
        } else {
            provinceList = zoneService.listChildren("1");
        }
        model.addAttribute("province", province);
        model.addAttribute("provinceList", provinceList);
        
        Zone city = null;
        List<Zone> cityList = null;
        if (parents.size() > 2) {
            city = parents.get(2);
            cityList = zoneService.listSiblings(city.getId());
        }
        model.addAttribute("city", city);
        model.addAttribute("cityList", cityList);
        
        Zone district = null;
        List<Zone> districtList = null;
        if (parents.size() > 3) {
            district = parents.get(3);
            districtList = zoneService.listSiblings(district.getId());
        }
        model.addAttribute("district", district);
        model.addAttribute("districtList", districtList);
        
        String skuId = req.getParameter("skuId");
        String shopId = req.getParameter("shopId");
        
        if (referer != null) {
            model.addAttribute("backUrl", referer);
        } else {
            String backUrl = "/cart/next?skuId=" + skuId;
            if(StringUtils.isBlank(skuId)){
                backUrl = "/cart/next?shopId=" + shopId;
            }
            model.addAttribute("backUrl", backUrl);
        }
        
        if ("xiangqu".equalsIgnoreCase(getCurrentUser().getPartner())){
			return "xiangqu/address";
		}else{
			return "cart/address";
		}
    }*/
}

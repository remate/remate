package com.vdlm.bos.shop;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.type.SyncEvType;
import com.vdlm.dal.vo.ShopAdmin;
import com.vdlm.dal.vo.SyncMqEvent;
import com.vdlm.service.rocketmq.SyncRocketMq;
import com.vdlm.service.shop.ShopService;

@Controller
public class ShopController extends BaseController {

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private SyncRocketMq syncRocketMq;
	
	@Value("${site.web.host.name}")
	String siteHost;
	
	/**
	 * 店铺列表页面
	 * @return
	 */
	@RequestMapping(value = "shop")
	public String list() {
		return "shop/shops";
	}
	
	
	/**
	 * 获取某一页的店铺数据
	 * @param form 
	 * @param order
	 * @param direction 排序
	 * @param pageable  分页
	 * @return
	 */
	@ResponseBody 
	@RequestMapping(value = "shop/list")
	public Map<String, Object> list(ShopSearchForm form, String order, String direction, Pageable pageable) {
		order = StringUtils.defaultIfBlank(order, "created_at");
		direction = StringUtils.defaultIfBlank(direction, "desc");
		
		Map<String, Object> params = new HashMap<String, Object>();
		//逻辑删除
		params.put("archive", ObjectUtils.defaultIfNull(form.getArchive_kwd(), Boolean.FALSE));
		
		if(StringUtils.isNotBlank(form.getPhone_kwd())){
			params.put("phone", "%" + form.getPhone_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getShop_name_kwd())){
			params.put("shopName", "%" + form.getShop_name_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getCommision_kwd())){
			params.put("commision", form.getCommision_kwd());
		}
		if(StringUtils.isNotBlank(form.getDanbao_kwd())){
			params.put("danbao", form.getDanbao_kwd());
		}
		
		// 起止时间
		if(StringUtils.isNotBlank(form.getCreated1_kwd())){
			params.put("created1", form.getCreated1_kwd());
		}
		if(StringUtils.isNotBlank(form.getCreated2_kwd())){
			 try {
				Date date = DateUtils.addDays(DateUtils.parseDate(form.getCreated2_kwd(), "yyyy-MM-dd"), 1);
				params.put("created2", date);
			} catch (ParseException e) {
				log.debug(e.getMessage());
			}
		}
		
		List<ShopAdmin> shops = shopService.listShopsByAdmin(params, pageable);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", shopService.countShopsByAdmin(params));
		data.put("rows", shops);
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "shop/close")
	public Json close(String shopId, String opRemark) {
		Json json = new Json();
		try {
			if(shopService.delete(shopId, opRemark)) {
				json.setMsg("封店成功");	
			
				SyncMqEvent event = new SyncMqEvent();
				List<String> ids = new ArrayList<String>();
				ids.add(shopId);
				event.setIds(ids);
				event.setEvent(3);
				event.setType(SyncEvType.EV_SHOP.ordinal());
				event.setTimestamp(new Date().getTime());
				syncRocketMq.sendToMQ(event);			
			} else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("封店失败，该店可能已封");	
			}
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("封店失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + "封店shopId=[" + shopId + "] opRemark=["+opRemark+"] rc=[" + json.getRc() + "]");
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "shop/unClose")
	public Json unClose(String shopId, String opRemark) {
		Json json = new Json();
		try {
			if(shopService.undelete(shopId, opRemark)) {
				json.setMsg("恢复店铺成功");	
			
				SyncMqEvent event = new SyncMqEvent();
				List<String> ids = new ArrayList<String>();
				ids.add(shopId);
				event.setIds(ids);
				event.setEvent(1);
				event.setType(SyncEvType.EV_SHOP.ordinal());
				event.setTimestamp(new Date().getTime());
				syncRocketMq.sendToMQ(event);			
			} else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("恢复店铺失败，恢复店铺可能已恢复");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("恢复店铺失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getPhone() + "恢复店铺shopId=[" + shopId + "] opRemark=["+opRemark+"] rc=[" + json.getRc() + "]");
		return json;
	}
	
	@RequestMapping(value = "/queryShopByUser/{userId}")
	public String queryShopByUser(@PathVariable("userId") String userId) {
		Shop shop = shopService.findByUser(userId);
		return "redirect:/redirectShopView/"+shop.getId();
	}
	
	@RequestMapping(value = "/redirectShopView/{shopId}")
	public String redirectUserShop(@PathVariable("shopId") String shopId) {
		return "redirect:"+siteHost+"/shop/"+shopId;
	}
}

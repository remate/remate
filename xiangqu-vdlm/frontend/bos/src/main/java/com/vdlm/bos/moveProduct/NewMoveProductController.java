package com.vdlm.bos.moveProduct;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.mapper.ProductMapper;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.service.shop.ShopService;

@Controller
public class NewMoveProductController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ProductMapper productMapper;
	
	@RequestMapping(value = "newMoveProduct")
	public String list(Model model, HttpServletRequest req) {
		return "moveProduct/newMoveProduct";
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/shop")
	public Json moveShop(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("tbShopUrl") String tbShopUrl) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
		} else {
			Map<String, Object> result = new HashMap<String, Object>();
			result = shopService.moveShop(shopId, tbShopUrl);
			json.setRc(Integer.parseInt(result.get("statusCode").toString()));
			switch (Integer.parseInt(result.get("statusCode").toString())) {
			case 200:
				json.setMsg("Success");
				break;
			default:
				json.setMsg(result.get("msg").toString());
				break;
			}
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/item")
	public Json moveItem(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("itemId") String itemId, 
			@RequestParam("shopType") String shopType) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
			return json;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result = shopService.moveItem(shopId, itemId, Integer.valueOf(shopType));
		json.setRc(Integer.parseInt(result.get("statusCode").toString()));
		switch (Integer.parseInt(result.get("statusCode").toString())) {
		case 200:
			json.setMsg("Success");
			break;
		default:
			json.setMsg(result.get("msg").toString());
			break;
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/item/groupImg")
	public Json moveItemGrouopImg(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("itemId") String itemId,
			@RequestParam("shopType") String shopType) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
			return json;
		}
		Long productId = productMapper.selectProductIdByThirdItemId(shopId, BigInteger.valueOf(Long.valueOf(itemId)));
		if (productId == null || productId <= 0) {
			json.setMsg("vdlm itemId not exist");
			return json;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result = shopService.moveItemGroupImg(shopId, itemId, Integer.valueOf(shopType));
		json.setRc(Integer.parseInt(result.get("statusCode").toString()));
		switch (Integer.parseInt(result.get("statusCode").toString())) {
		case 200:
			json.setMsg("Success");
			break;
		default:
			json.setMsg(result.get("msg").toString());
			break;
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/item/sku")
	public Json moveItemSku(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("itemId") String itemId,
			@RequestParam("shopType") String shopType) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
			return json;
		}
		Long productId = productMapper.selectProductIdByThirdItemId(shopId, BigInteger.valueOf(Long.valueOf(itemId)));
		if (productId == null || productId <= 0) {
			json.setMsg("vdlm itemId not exist");
			return json;
		}
		
		log.info(super.getCurrentUser().getId() + " exec moveItem.shop:" + shopId + ",itemId:" + itemId);
		Map<String, Object> result = new HashMap<String, Object>();
		result = shopService.moveItem(shopId, itemId, Integer.valueOf(shopType));
		json.setRc(Integer.parseInt(result.get("statusCode").toString()));
		switch (Integer.parseInt(result.get("statusCode").toString())) {
		case 200:
			json.setMsg("Success");
			break;
		default:
			json.setMsg(result.get("msg").toString());
			break;
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/item/desc")
	public Json moveItemDesc(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("itemId") String itemId,
			@RequestParam("shopType")String shopType) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
			return json;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result = shopService.moveItemDesc(shopId, itemId, Integer.valueOf(shopType));
		json.setRc(Integer.parseInt(result.get("statusCode").toString()));
		switch (Integer.parseInt(result.get("statusCode").toString())) {
		case 200:
			json.setMsg("Success");
			break;
		default:
			json.setMsg(result.get("msg").toString());
			break;
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/move/item/sync")
	public Json moveItemSync(@RequestParam("kdShopUrl") String kdShopUrl, @RequestParam("itemId") String itemId, 
			@RequestParam("type") String type) {
		Json json = new Json();
		String[] array = kdShopUrl.split("/");
		String shopId = array[array.length-1];
		Shop shop =  shopService.load(shopId);
		if(shop == null) {
			json.setMsg("vdlm shop not exist");
			return json;
		}
//		Long productId = productMapper.selectProductIdByThirdItemId(shopId, BigInteger.valueOf(Long.valueOf(itemId)));
//		if (productId == null || productId <= 0) {
//			json.setMsg("vdlm itemId not exist");
//			return json;
//		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result = shopService.moveSyncFromSpider(shopId, itemId, Integer.valueOf(type));
		json.setRc(Integer.parseInt(result.get("statusCode").toString()));
		switch (Integer.parseInt(result.get("statusCode").toString())) {
		case 200:
			json.setMsg("Success");
			break;
		default:
			json.setMsg(result.get("msg").toString());
			break;
		}
		return json;
	}
	
}

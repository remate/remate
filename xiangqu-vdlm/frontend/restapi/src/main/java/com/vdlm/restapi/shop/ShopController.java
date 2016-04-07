package com.vdlm.restapi.shop;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.Category;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Tag;
import com.vdlm.dal.model.ThirdCommission;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.mybatis.IdTypeHandler2;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.dal.status.SyncAuditStatus;
import com.vdlm.dal.type.Taxonomy;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.ShopPostAge;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.DeviceTypes;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.category.CategoryForm;
import com.vdlm.restapi.category.CategoryVO;
import com.vdlm.service.category.CategoryService;
import com.vdlm.service.category.TermTaxonomyService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.BannerVO;
import com.vdlm.service.shop.ShopPostAgeService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.shop.ShopStyleVO;
import com.vdlm.service.zone.ZoneService;

@Controller
public class ShopController extends BaseController {
	
	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private UrlHelper urlHelper;
	@Autowired
	private ResourceFacade resourceFacade;
	@Autowired
	private ZoneService zoneService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private TermTaxonomyService termTaxonomyService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProdSyncMapper prodSyncMapper;
	@Autowired
	private ShopPostAgeService shopPostAgeService;
	
	@Value("${shop.banners}")
	private String bannerKeys;
	
	@Value("${spider.url}")
	private String spiderurl;
	
	/**
	 * 获取店铺/shop/{id}
	 * @param id
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/{id}")
	public ResponseObject<ShopVO> view(@PathVariable String id, HttpServletRequest req) {
		Shop shop = shopService.load(id);
		if (shop == null) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.NOT_FOUND, requestContext.getMessage("shop.not.found"));
		}

		return new ResponseObject<ShopVO>(shopToVo(shop));
	}

	/**
	 * 获取我的店铺
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/mine")
	public ResponseObject<ShopVO> mine() {
		return new ResponseObject<ShopVO>(shopToVo(shopService.mine()));	
	}
	
	/**
	 * 更新邮费设置
	 * @param shopPostAge
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updatePostageSet")
	public ResponseObject<Boolean> updatePostAgeSet(ShopPostAge shopPostAge) {
		Boolean ret = false;
		User user = getCurrentUser();
		shopPostAge.setShopId(user.getShopId());
		if (shopPostAge != null && shopPostAge.getShopId() != null) {
			ret = shopPostAgeService.setPostAgeByShop(shopPostAge);
		}
		return new ResponseObject<Boolean>(ret);	
	}
	
	/**
	 * 获取邮费设置
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/getPostageSet")
	public ResponseObject<ShopPostAge> getPostAge() {
		User user = getCurrentUser();
		if (user != null)
			return new ResponseObject<ShopPostAge>(shopPostAgeService.getPostAgeByShop(user.getShopId()));
		else
			return new ResponseObject<ShopPostAge>();
	}
	
	private PostAgeZoneVO containsZone(String zoneTag, List<PostAgeZoneVO> list) {
		if (zoneTag != null && list != null && list.size() != 0) {
			for (PostAgeZoneVO avo : list) {
				if (zoneTag.equals(avo.getZoneTag())) {
					return avo;
				}
			}
		}
		return null;
	}
	
	/**
	 * 修改用户的客服电话
	 * http://localhost:8888/v2/shop/saveMobileAndTel?mobile=15968118399&mobileType=1
	 */
	@ResponseBody
	@RequestMapping("/shop/saveMobileAndTel")
	public ResponseObject<Boolean> saveMobileAndTel(String mobile, Integer mobileType){
 	   if(org.apache.commons.lang3.StringUtils.isBlank(mobile)){
 		  throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "联系电话不能都为空");
 	   } 
 	   
 	  if(mobileType==null || (mobileType.intValue()!=0 && mobileType.intValue()!=1)){
 		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "电话类型不正确");
 	  }
 	  
 	  Shop shop=shopService.mine();
 	  shop.setMobile(mobile); 
 	  shop.setMobileType(mobileType);
 	  
 	  int result= shopService.updateByAdmin(shop);
 	  return new ResponseObject<Boolean>(result>0);
    }
	
	/**
	 * 获取设置邮费的地区
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/getPostageZones")
	public ResponseObject<List<PostAgeZoneVO>> getPostAgeZones() {
		List<PostAgeZoneVO> ret = new  ArrayList<PostAgeZoneVO>();
		
		List<Zone> zoneList =  zoneService.listPostageZones();
		for (Zone zone : zoneList) {
			PostAgeZoneVO avo = containsZone(zone.getZoneTag(), ret);
			if (avo != null) {
				avo.getZones().add(zone);
			} else {
				avo = new PostAgeZoneVO();
				List<Zone> zones = new ArrayList<Zone>(); 
				zones.add(zone);
				avo.setZoneTag(zone.getZoneTag());
				avo.setZones(zones);
				ret.add(avo);
			}
		}
		
		List<Zone>cityList =  zoneService.listPostageCityZones();
		for (Zone zone : cityList) {
			PostAgeZoneVO avo = containsZone(zone.getZoneTag(), ret);
			if (avo != null) {
				avo.getZones().add(zone);
			} else {
				avo = new PostAgeZoneVO();
				List<Zone> zones = new ArrayList<Zone>(); 
				zones.add(zone);
				avo.setZoneTag(zone.getZoneTag());
				avo.setZones(zones);
				ret.add(avo);
			}
		}
		
		return new ResponseObject<List<PostAgeZoneVO>>(ret);	
	}

	/**
	 * 开通担保交易
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/openDanbao")
	public ResponseObject<Boolean> openDanbao() {
		return new ResponseObject<Boolean>(shopService.openDanbao());
	}

	/**
	 * 保存店铺信息
	 * @param form
	 * @param errors
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/save")
	public ResponseObject<ShopVO> save(@Valid @ModelAttribute ShopForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		Shop shop = shopService.findByUser(getCurrentUser().getId());
		if (null == shop){ 
			shop = new Shop();
		}
		BeanUtils.copyProperties(form, shop);
		shop = shopService.create(shop);
		if (shop == null) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					requestContext.getMessage("valid.shop.exist.message"));
		}
		shopService.getCurrentUser().setShopId(shop.getId());
		return new ResponseObject<ShopVO>(shopToVo(shop));
	}
	
	private Boolean updateThirdCommission(ThirdCommission atc, HttpServletRequest req) {
		Boolean ret = false;
		if (atc == null || atc.getThirdId() == null) return ret;
		
		Shop shop = shopService.load(this.getCurrentUser().getShopId());
		if (atc.getCommissionRate() != null) {
			if ( atc.getCommissionRate().compareTo(BigDecimal.valueOf(0.05)) < 0 ||
				 atc.getCommissionRate().compareTo(BigDecimal.valueOf(1)) > 0) {
				RequestContext requestContext = new RequestContext(req);
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, requestContext.getMessage("invalid.third.commisionrate"));
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("shopId", shop.getId());
			params.put("unionId", atc.getThirdId());
			List<ProdSync> aList = prodSyncMapper.findByParmas(params, null);
			ProdSync aps = new ProdSync();
			if (aList != null && aList.size() != 0) {
				if (aList.size() == 1) {
					aps.setId(aList.get(0).getId());
					aps.setAuditSts(SyncAuditStatus.AUDITTING.toString());
					aps.setCommissionRate(atc.getCommissionRate());
					ret = prodSyncMapper.update(aps) == 1 ? true : false;
				} else {
					ret = false;
				}
			} else {
				aps.setShopId(shop.getId());
				aps.setName(shop.getName());
				aps.setSynced(false);
				aps.setAuditSts(SyncAuditStatus.AUDITTING.toString());
				aps.setCommissionRate(atc.getCommissionRate());
				User aUser = userService.load(atc.getThirdId());
				if (aUser != null) {
					aps.setUnionId(aUser.getId());
				}
				//目前只会是想去的用户信息
//				else {
//					aps.setUnionId(IdTypeHandler.encode(16618945L));
//				}
				ret = prodSyncMapper.insert(aps) == 1 ? true : false;
			}
			
			shop.setDanbao(true);
			shopService.update(shop);
		}
		return ret;
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/update")
	public ResponseObject<ShopVO> update(@Valid @ModelAttribute ShopForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		Shop shop = new Shop();
		BeanUtils.copyProperties(form, shop);
		shop.setId(this.getCurrentUser().getShopId());
		
		if (form.getCommisionRate() != null) {
			if ( form.getCommisionRate().compareTo(BigDecimal.valueOf(0)) < 0 ||
				 form.getCommisionRate().compareTo(BigDecimal.valueOf(0.5)) > 0) {
				RequestContext requestContext = new RequestContext(req);
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, requestContext.getMessage("invalid.commisionrate"));
			}
		}
		
		int rc = shopService.update(shop);
		if (1 != rc) {
			RequestContext requestContext = new RequestContext(req);
			if (0 == rc) {
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, requestContext.getMessage("invalid.argument"));
			}
			else if (-1 == rc) {
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
						requestContext.getMessage("valid.shop.exist.message"));
			}
		}
		
		if (form.getThirdCommission() != null) {
			if ( !updateThirdCommission(form.getThirdCommission(), req) ) {
				RequestContext requestContext = new RequestContext(req);
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, requestContext.getMessage("invalid.argument"));
			}
		}
		
		return new ResponseObject<ShopVO>(shopToVo(shop));
	}

	/**
	 * 更换店招
	 * @param form
	 * @param errors
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateBanner/{id}")
	public ResponseObject<Boolean> updateBanner(@PathVariable String id, String banner) {
		Shop shop = new Shop();
		shop.setId(id);
		shop.setBanner(banner);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}
	
	/**获取某个店铺的标签
	 * @param shopId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/tag/{shopId}")
	public ResponseObject<List<Tag>> tag(@PathVariable String shopId, String tag) {
		return new ResponseObject<List<Tag>>(shopService.findTagsByShopId(shopId, tag));
	}
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/tags")
    public ResponseObject<List<Tag>> tags() {
        return new ResponseObject<List<Tag>>(shopService.listUserTags());
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/tag/save")
    public ResponseObject<Tag> saveTag(String tag) {
        return new ResponseObject<Tag>(shopService.saveUserTag(tag));
    }

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/tag/update")
	public ResponseObject<Tag> updateTag(String id, String tag) {
	    return new ResponseObject<Tag>(shopService.updateUserTag(id, tag));
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/tag/remove")
	public ResponseObject<Boolean> removeTag(String tag) {
	    shopService.removeUserTag(tag);
	    return new ResponseObject<Boolean>(true);
	}

	/**
	 * 列出所有商铺
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/list")
	public ResponseObject<List<Shop>> list(Pageable pageble) {
		return new ResponseObject<List<Shop>>(shopService.listAll(pageble));
	}
	
	private ThirdCommission createThirdCommission(User aUser, String shopId) {
		if (aUser == null || shopId == null) return null;
		
		ThirdCommission atc = new ThirdCommission();
		atc.setThirdPartner(aUser.getName());
		atc.setAuditSts(SyncAuditStatus.UNAUDIT.toString());
		
		List<ProdSync> psList = prodSyncMapper.selectByShopId(shopId, null);
		if (psList != null && psList.size() != 0) {
			for (ProdSync aps : psList) {
				if (aps.getUnionId().equals(aUser.getId())) {
					atc.setCommissionRate(aps.getCommissionRate());
					atc.setAuditSts(aps.getAuditSts());
					atc.setFailedReason(aps.getAuditNote());
				}
			}
		}
		atc.setThirdId(IdTypeHandler.encode(Long.valueOf(aUser.getId())));
		return atc;
	}
	
	private ShopVO shopToVo(Shop shop) {
		if (shop != null) {
			shop = shopService.load(shop.getId());
			ShopVO vo = new ShopVO(shop, urlHelper);
			if (StringUtils.isNotBlank(vo.getImg())) {
				vo.setImgUrl(shop.getImg());
			}
			if (StringUtils.isNotBlank(vo.getBanner())) {
				vo.setBannerUrl(shop.getBanner());
			}
			
			User owner = userService.load(shop.getOwnerId());
			vo.setOwner(owner);

			vo.setCountDraft(productService.countProductsByStatus(vo.getId(), ProductStatus.DRAFT));// 草稿
			vo.setCountForsale(productService.countProductsByStatus(vo.getId(), ProductStatus.FORSALE));// 发布计划数
			vo.setCountOnsale(productService.countProductsByStatus(vo.getId(), ProductStatus.ONSALE));// 在售商品数
			vo.setCountOutofstock(productService.countProductsByOutofStock(vo.getId()));// 缺货
			vo.setLocalName(getLocalArea(shop.getProvinceId(), shop.getCityId()));  //加载所在区域
			vo.setPostFreeRegions(parserPostFreeStr(shop.getFreeZone())); // 免邮区域

			vo.setCountOfOrderClose(orderService.countSellerOrdersByStatus(OrderStatus.CLOSED));
			vo.setCountOfOrderPaid(orderService.countSellerOrdersByStatus(OrderStatus.PAID));
			vo.setCountOfOrderShipped(orderService.countSellerOrdersByStatus(OrderStatus.SHIPPED));
			vo.setCountOfOrderSubmitted(orderService.countSellerOrdersByStatus(OrderStatus.SUBMITTED));
			vo.setCountOfOrderSuccess(orderService.countSellerOrdersByStatus(OrderStatus.SUCCESS));

			
			List<ThirdCommission> aList = new ArrayList<ThirdCommission>();
			List<User>feeAcctList =  userService.getFeeSplitAcct();
			for (User aUser : feeAcctList) {  // 多个分润账号
				if (aUser.getLoginname().equalsIgnoreCase(UserPartnerType.XIANGQU.toString()))  // 暂时只开放"想去"
					aList.add(createThirdCommission(aUser, vo.getId()));
			}
			vo.setThirdCommissions(aList);
			ShopPostAge spa = shopPostAgeService.getPostAgeByShop(shop.getId());
			if (spa != null ) {
				if (spa.getPostageStatus() != null) {
					vo.setPostageStatus(spa.getPostageStatus());  // 用记亲数据替换老数据, 老数据其实也被新流程更新过了, 为的是将来删除老字段不再维护
				}
				if (spa.getPostage() != null) {
					vo.setPostage(spa.getPostage());
				}
			}
			return vo;
		}
		else {
			return null;
		}
	}
	
	// 如果有多个名邮地区以坚线分隔 '|', 一期只会有一个免邮地区, 但可能会包括 浙江沪 长三角等多地区地名
	private List<String> parserPostFreeStr(String postStr) {
		if (null == postStr) return null;
		String [] regions = postStr.split("\\|");
		return Arrays.asList(regions);
	}
	
	private String getLocalArea(Long provId, Long cityId) {
		String localName = new String();
		Zone zone = null;
		
		if (provId != null && provId.intValue() > 0) {
			zone = zoneService.load(provId.toString());
			if (zone != null) localName += zone.getName();
		}
		
		if (cityId != null && cityId.intValue() > 0) {
			zone = zoneService.load(cityId.toString());
			if (zone != null) localName += zone.getName();
		}
		
		return localName;
	}
	
	/**
	 * for PC move product function
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/movestart")
	public ResponseObject<Map<String, Object>> movestart() {
		
		Map<String, Object> result = new HashMap<String, Object>();
		//1.  判断卖家是否已搬过家 (出错都当做初次搬家,多验证一次)
		String shopUrl = shopService.checkRepeatMoving();
		if (StringUtils.isNotEmpty(shopUrl)) {
			//2. 已搬家进入跳过验证,提醒该卖家为二次搬家并直接根据shopId进行二次搬家
			try {
				//model.addAttribute("shopUrl", URLEncoder.encode(shopUrl, "utf-8"));
				result.put("shopUrl", URLEncoder.encode(shopUrl, "utf-8"));
			} catch (Exception e) {
				log.warn("URLEncoder.encode err");
			}
		} else {
			//3. 没有搬过家按原有流程开始进行搬家
			final long rnd = this.shopService.getRnd();
			result.put("rnd", rnd);
		}
		
		return new ResponseObject<Map<String, Object>>(result);
	}


	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step1")
	public String step1(Model model) {
		String moveFirst = "shop/step1";
		String moveAgain = "shop/step5";
		
		//1.  判断卖家是否已搬过家 (出错都当做初次搬家,多验证一次)
		String shopUrl = shopService.checkRepeatMoving();
		if (StringUtils.isNotEmpty(shopUrl)) {

			//2. 已搬家进入跳过验证,提醒该卖家为二次搬家并直接根据shopId进行二次搬家
			try {
				model.addAttribute("shopUrl", URLEncoder.encode(shopUrl, "utf-8"));
			} catch (Exception e) {
				log.warn("URLEncoder.encode err");
			}
			return moveAgain;
		} else {
			//3. 没有搬过家按原有流程开始进行搬家
			final long rnd = this.shopService.getRnd();
			model.addAttribute("rnd", rnd);
			return moveFirst;
		}
	}

	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step2")
	public String step2(long rnd, Model model) {
		model.addAttribute("rnd", rnd);
		return "shop/step2";
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step2check")
	public ResponseObject<Map<String, Object>> step2check(long rnd, String itemId, HttpServletRequest req) {
		final int deviceType = DeviceTypes.getDeviceType(req);
		final Map<String, Object> result = this.shopService.moveThirdShopProducts(this.getCurrentUser(), rnd, itemId,
				deviceType, 1);

		return new ResponseObject<Map<String, Object>>(result);
	}

	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step3")
	public String step3(long rnd, String itemId, String shopName, int shopType, Model model) {
		model.addAttribute("rnd", rnd);
		model.addAttribute("itemId", itemId);
		model.addAttribute("shopName", shopName);
		model.addAttribute("shopType", shopType);

		return "shop/step3";
	}
	
 @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step4")
	public String step4() {
		return "shop/step4";
	}
	
 	// option 0: 只新增商品, 1:新增并更新已有商品
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step5")
	@ResponseBody
	public ResponseObject<Map<String, Object>> step5(String shopUrl,
				@RequestParam(value = "option", required = true, defaultValue = "1") int option,
				HttpServletRequest req) {
		final int deviceType = DeviceTypes.getDeviceType(req);
		if (shopUrl==null || shopUrl.isEmpty()) {
			return new ResponseObject<Map<String, Object>>();
		}
		try {
			String aUrl = URLDecoder.decode(shopUrl, "utf-8");
			if (aUrl != null) {
				String[] urls  = aUrl.split("\\|");
				Map<String, Object> result = null;
				for (String url  : urls) { 
					Thread.sleep(1000);			
					result = shopService.moveThirdShopProducts(getCurrentUser(),   url, getCurrentUser().getShopId(), deviceType, option);
					if ( result.get("statusCode").equals("200")) {
						continue;
					}
					return new ResponseObject<Map<String, Object>>(result);
				}
				return new ResponseObject<Map<String, Object>>(result);
			}
			return new ResponseObject<Map<String, Object>>();
		} catch (UnsupportedEncodingException e) {
			log.error("搬家第五步出错",e);
			return new ResponseObject<Map<String, Object>>();
		} catch (InterruptedException e) {
			log.error("搬家第五步出错",e);
			return new ResponseObject<Map<String, Object>>();
		}
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/setPostage")
	public ResponseObject<Boolean> updatePostByPrimaryKey(@Valid @ModelAttribute ShopForm form) {
		Shop shop = shopService.load(this.getCurrentUser().getShopId());
		if (shop == null)
			return new ResponseObject<Boolean>(false);
		
		if (form.getPostageStatus() != null) {
			shop.setPostageStatus(form.getPostageStatus());
			
			if (form.getPostageStatus()) {
				if (form.getFreeZone() != null) {
					shop.setFreeZone(form.getFreeZone());
				}
				if (form.getPostage() != null) {
					shop.setPostage(form.getPostage());
				}	
			}
		}
		
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateImg")
	public ResponseObject<Boolean> updateImgByPrimaryKey(String id, String img) {
		Shop shop = shopService.load(id);
		shop.setImg(img);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateName")
	public ResponseObject<Boolean> updateNameByPrimaryKey(String id, String name) {
		Shop shop = shopService.load(id);
		shop.setName(name);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateWechat")
	public ResponseObject<Boolean> updateWechatByPrimaryKey(String id, String wechat) {
		Shop shop = shopService.load(id);
		shop.setWechat(wechat);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateDesc")
	public ResponseObject<Boolean> updateDescByPrimaryKey(String id, String description) {
		Shop shop = shopService.load(id);
		shop.setDescription(description);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateBulletin")
	public ResponseObject<Boolean> updateBulletinByPrimaryKey(String id, String bulletin) {
		Shop shop = shopService.load(id);
		shop.setBulletin(bulletin);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updateLocal")
	public ResponseObject<Boolean> updateLocalByPrimaryKey(String id, String provinceId, String cityId) {
		Shop shop = shopService.load(id);
		Long l_provinceId = 0L;
		Long l_cityId = 0L;
		if (StringUtils.isNotEmpty(provinceId)) l_provinceId = Long.valueOf(provinceId);
		if (StringUtils.isNotEmpty(cityId)) l_cityId = Long.valueOf(cityId);
		shop.setProvinceId(l_provinceId);
		shop.setCityId(l_cityId);
		
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updatePostageStatus")
	public ResponseObject<Boolean> updateLocalByPrimaryKey(String id, Boolean postageStatus) {
		Shop shop = shopService.load(id);
		shop.setPostageStatus(postageStatus);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}

	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/updatePostage")
	public ResponseObject<Boolean> updateLocalByPrimaryKey(String id, String freeZone, BigDecimal postage) {
		Shop shop = shopService.load(id);
		shop.setFreeZone(freeZone);
		shop.setPostage(postage);
		return new ResponseObject<Boolean>(shopService.update(shop) == 1);
	}
	
	/**
	 * http://localhost:8888/v2/shop/category/products?categoryId=2
	 * 获取某个店铺下的某个分类的商品
	 * @param categoryId
	 * @param page
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/shop/category/products")
    public ResponseObject<Map<String,Object>> productAll2(String categoryId, Pageable page){
        List<Product> products = categoryService.listProductsInCategory(categoryId, page);
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("product", products);
        map.put("page", page);
        return new ResponseObject<Map<String,Object>>(map);
	}
	
	/**
	 * v2/shop/category/list
	 * 获取某个店铺的分类列表
	 * @return
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/list")
	public ResponseObject<List<CategoryVO>> categoryList() {
	    List<CategoryVO> list = new ArrayList<CategoryVO>();
	    List<Category> categories = categoryService.listUserRootGoods();
	    for (Category cat : categories) {
	        CategoryVO vo = new CategoryVO();
	        BeanUtils.copyProperties(cat, vo);
	        long productCount = categoryService.countUserGoodsProducts(cat.getId());
	        vo.setProductCount(productCount);
	        list.add(vo);
        }
	    return new ResponseObject<List<CategoryVO>>(list);
	}
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/view")
    public ResponseObject<Category> categoryView(@RequestParam String id) {
	    Category category = categoryService.load(id);
	    if (category != null) {
	        return new ResponseObject<Category>(category);
	    } else {
	        throw new BizException(GlobalErrorCode.NOT_FOUND, "分类不存在");
	    }
    }
	
	/**
	 * 保存商品的分类信息
	 * v2/shop/category/save?name=xbe
	 * @param name
	 * @return
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/save")
    public ResponseObject<Category> categorySave(@RequestParam String name) {
	    Category goods = categoryService.saveUserGoods(name);
        return new ResponseObject<Category>(goods);
    }
	
	/**
	 * 批量修改分类信息
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/batchUpdate")
    public ResponseObject<Boolean> batchUpdate(@Valid @ModelAttribute CategoryForm form) {
		if(form==null || form.getCategorys()==null ||  form.getCategorys().isEmpty()){
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "请输入您要修改的分类信息");
		}
		
		categoryService.batchUpdateUserGoodsName(form.getCategorys());
	    return new ResponseObject<Boolean>(true);
    }
	
	/**
	 * http://localhost:8888/v2/shop/category/update?id=6060&name=xbw123578
	 * 修改分类
	 * @param id
	 * @param name
	 * @return
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/update")
    public ResponseObject<CategoryVO> categoryUpdate(@RequestParam String id, @RequestParam String name) {
	    Category cat = categoryService.updateUserGoodsName(id, Taxonomy.GOODS, name);
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(cat, vo);
        long productCount = categoryService.countUserGoodsProducts(cat.getId());
        vo.setProductCount(productCount);
        return new ResponseObject<CategoryVO>(vo);
    }
	
	/**
	 * 删除商品分类信息（该商品分类下已经有了商品，则无法删除）
	 * v2/shop/category/remove
	 * @param id
	 * @return
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/remove")
    public ResponseObject<Boolean> categoryRemove(@RequestParam String id) {
	    categoryService.removeUesrGoods(id);
        return new ResponseObject<Boolean>(true);
    }
	
	/**
	 * 批量删除商品分类信息
	 * http://localhost:8888/v2/shop/category/batchRemove?ids=1&ids=2
	 * @param srcId
	 * @param desId
	 * @return
	 */
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/batchRemove")
    public ResponseObject<Boolean> categoryRemove(@RequestParam String... ids) {
	    categoryService.removeUesrGoodsCategory(ids);
        return new ResponseObject<Boolean>(true);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/move-before")
    public ResponseObject<Boolean> categoryMoveBefore(@RequestParam String srcId, @RequestParam String desId) {
        categoryService.moveBefore(srcId, desId);
        return new ResponseObject<Boolean>(true);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/move-after")
    public ResponseObject<Boolean> categoryMoveAfter(@RequestParam String srcId, @RequestParam String desId) {
        categoryService.moveAfter(srcId, desId);
        return new ResponseObject<Boolean>(true);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/category/update-idx")
    public ResponseObject<Boolean> categoryUpdateIdx(@Valid @ModelAttribute CategoryIdxUpdateForm form) {
	    if (form.getIdxs() == null || form.getIdxs().size() == 0) {
	        categoryService.updateIdx(form.getCategoryIds());
	    } else {
	        for (int i = 0; i < form.getCategoryIds().size(); i++) {
                String cateId = form.getCategoryIds().get(i);
                Integer idx = form.getIdxs().get(i);
                categoryService.updateIdx(cateId, idx);
            }
	    }
        return new ResponseObject<Boolean>(true);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/{id}/styles")
    public ResponseObject<ShopStyleVO> shopStyles(@PathVariable String id) {
		ShopStyleVO ss = shopService.loadShopStyle(id);
	    return new ResponseObject<ShopStyleVO>(ss);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/styles")
    public ResponseObject<ShopStyleVO> myShopStyles() {
		ShopStyleVO ss = shopService.loadShopStyle();
        return new ResponseObject<ShopStyleVO>(ss);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/styles/update")
    public ResponseObject<ShopStyleVO> updateShopStyles(@ModelAttribute ShopStyleVO form) {
	    if (!StringUtils.isEmpty(form.getListView())) {
	        form.setListView(form.getListView().toLowerCase());
	    }
		shopService.updateShopStyles(form);
		ShopStyleVO ss = shopService.loadShopStyle();
        return new ResponseObject<ShopStyleVO>(ss);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/styles/banners")
    public ResponseObject<List<BannerVO>> styleBanners() {
	    String[] bannerKeyArray = StringUtils.split(bannerKeys, ",");
	    List<BannerVO> r = new ArrayList<BannerVO>(bannerKeyArray.length);
	    for (String bannerKey : bannerKeyArray) {
	        BannerVO b = new BannerVO();
	        b.setImgKey(bannerKey);
	        b.setImgUrl(bannerKey);
	        r.add(b);
        }
	    return new ResponseObject<List<BannerVO>>(r);
    }
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/statistics")
    public ResponseObject<Map<String, Object>> statistics() {
	    return new ResponseObject<Map<String, Object>>(shopService.loadStatistics());
    }
	
	@ResponseBody
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/set_fragment")
	public ResponseObject<Boolean> saveFragment(boolean enable){
		shopService.saveFragment(enable);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
     * 保存第三方佣金设置(想去)
     */
    @ResponseBody
    @RequestMapping("/shop/saveThirdCommission")
   	public ResponseObject<Boolean> saveThirdCommission(ThirdCommission form) {
    	shopService.updateThirdCommission(form);
    	return new ResponseObject<Boolean>(true);
    }
    
    /**
     * 保存本店铺的佣金设置--保存店铺
     */
    @ResponseBody
    @RequestMapping( "/shop/saveShopCommission")
   	public ResponseObject<Shop> saveShopCommission(String commisionRate) {
    	if(StringUtils.isBlank(commisionRate)){
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,  "请输入合法佣金");
    	}
    	
    	BigDecimal rate=new BigDecimal(commisionRate);
    	if(rate.compareTo(BigDecimal.ZERO) <0 || rate.compareTo(new BigDecimal(1))>0){
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,  "请输入合法佣金");
    	}
    	
    	Shop shop = new Shop();
    	shop.setId(getCurrentUser().getShopId());
    	shop.setCommisionRate(rate);
    	shopService.update(shop);
    	return new ResponseObject<Shop>(shopService.load(getCurrentUser().getShopId()));
    }
}

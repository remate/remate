

package com.vdlm.web.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.ThirdCommission;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.status.SyncAuditStatus;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.ShopPostAge;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopPostAgeService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.web.BaseController;

@Controller
public class ShopController extends BaseController {
	
	@Autowired
	private ShopPostAgeService shopPostAgeService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProdSyncMapper prodSyncMapper;
	@Autowired
	private UserService userService;	
	@Autowired
	private UrlHelper urlHelper;
	@Autowired
	private ZoneService zoneService;
	
    private static final String  POSTAGE_URL="settings/set-postage";
    
    private static final String COMMISSION_URL="settings/set-commission";
    
    /**
     * 显示佣金的列表<br>
     * /shop/getCommissionList
     * @return
     */
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/getCommissionList")
	public ModelAndView getCommissionList() {
    	User user=getCurrentUser();
		ModelAndView mv =new ModelAndView();
	    ShopVO shopVO= shopToVo(shopService.mine());
	    mv.setViewName(COMMISSION_URL);
	    mv.addObject("shop",shopVO);
	    mv.addObject("user",user);
		return mv;
	}
    
	/**
	 * 获取邮资设置列表<br>
	 * /shop/getPostageSet
	 * @return
	 */
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/getPostageSet")
	public ModelAndView getPostAge() {
		ModelAndView mv= new ModelAndView();
		ShopVO shopVO = shopToVo(shopService.mine());
	    mv.addObject("shop",shopVO);
		mv.setViewName(POSTAGE_URL);
		User user = getCurrentUser();
		ShopPostAge shopPostAge=shopPostAgeService.getPostAgeByShop(user.getShopId());
		mv.addObject("shopPostAge",shopPostAge);
		mv.addObject("user",user);
		
		/**获取邮费区域列表 */
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
		mv.addObject("ret",ret);
		
		List<Zone> cityList =  zoneService.listPostageCityZones();
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
		mv.addObject("cityList",cityList);
	
		return mv;
	}
	
	// =======================私有方法===================
	private ShopVO shopToVo(Shop shop) {
		ShopVO vo = new ShopVO(shop, urlHelper);
		List<ThirdCommission> aList = new ArrayList<ThirdCommission>();
		List<User> feeAcctList =  userService.getFeeSplitAcct();
		for (User u : feeAcctList) {  // 多个分润账号
			if (u.getLoginname().equalsIgnoreCase(UserPartnerType.XIANGQU.toString())) { // 暂时只开放"想去"
				aList.add(getThirdCommission(u, vo.getId()));
				break;
			}
		}
		vo.setThirdCommissions(aList);
		return vo;
	}
	
	private ThirdCommission getThirdCommission(User user, String shopId) {
		if (user == null || shopId == null) {
			return null;
		}
		
		// 初始化
		ThirdCommission atc = new ThirdCommission();
		atc.setThirdPartner(user.getName());
		atc.setAuditSts(SyncAuditStatus.UNAUDIT.toString());
		atc.setThirdId(user.getId());
		
		// 已经报名了的，修改初始化的
		List<ProdSync> psList = prodSyncMapper.selectByShopId(shopId, null);
		if (psList != null && psList.size() > 0) {
			for (ProdSync aps : psList) {
				if (aps.getUnionId().equals(user.getId())) {
					atc.setCommissionRate(aps.getCommissionRate());
					atc.setAuditSts(aps.getAuditSts());
					atc.setFailedReason(aps.getAuditNote());
					break;
				}
			}
		}
		return atc;
	}
	
	private PostAgeZoneVO containsZone(String zoneTag, List<PostAgeZoneVO> list) {
		PostAgeZoneVO vo = null;
		if (zoneTag == null || list == null || list.size() < 1) {
			return vo;
		}
		
		for (PostAgeZoneVO avo : list) {
			if (zoneTag.equals(avo.getZoneTag())) {
				 vo = avo;
				 break;
			}
		}
		return vo;
	}	
}


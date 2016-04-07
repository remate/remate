package com.vdlm.web.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.ThirdCommission;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.SyncAuditStatus;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.fragment.FragmentImageService;
import com.vdlm.service.fragment.FragmentService;
import com.vdlm.service.fragment.ProductFragmentService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.web.BaseController;
import com.vdlm.web.shop.ShopVO;

@Controller
public class FragmentController extends BaseController {
	
	@Autowired
	private FragmentService fragmentService;
	@Autowired
	private FragmentImageService fragmentImageService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private UserService userService;	
	@Autowired
	private UrlHelper urlHelper;
	@Autowired
	private ProdSyncMapper prodSyncMapper;
	@Autowired
	private ProductFragmentService productFragmentService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ResourceFacade resourceFacade;
	
	public static final String  FRAGMENT_URL="settings/set-desc";
	
	/**
	 * 获取段楼描述列表
	 * @return
	 */
	@RequestMapping("/fragment/list")
	public ModelAndView listFragmentByShop(){
		ModelAndView mv =new ModelAndView();
		mv.setViewName(FRAGMENT_URL);
		
		User user = getCurrentUser();
		mv.addObject("user",user);
		
		ShopVO shopVO= shopToVo(shopService.mine());
		mv.addObject("shop",shopVO);
		
		/*String shopId = user.getShopId();
		List<FragmentVO> list = fragmentService.selectByShopId(shopId);
		
		// 因片段信息过多，比较慢，暂时不再取信息
		for (int i = 0; i < list.size(); i++) {
			FragmentVO vo = list.get(i);
			List<FragmentImageVO> imgs = fragmentImageService.selectByFragmentId(vo.getId());
			if(imgs!=null){
				for (int j = 0; j < imgs.size(); j++) {
					FragmentImageVO imgVo = imgs.get(j);
					if(!StringUtils.isBlank(imgVo.getImg())){
						imgVo.setImgUrl(resourceFacade.resolveUrl(imgVo.getImg()));
					}
				}
			}
			vo.setImgs(imgs);
		 }
		
		mv.addObject("list",list);*/
		return mv;
	}
	
	private ShopVO shopToVo(Shop shop) {
		if (shop == null) {
			shop = shopService.load(shop.getId());
			
			if(shop==null){
			 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "店铺不存在！");
			}
		}
		
		ShopVO vo = new ShopVO(shop, urlHelper);
	
		List<ThirdCommission> aList = new ArrayList<ThirdCommission>();
		List<User>feeAcctList =  userService.getFeeSplitAcct();
		for (User aUser : feeAcctList) {  // 多个分润账号
			if (aUser.getLoginname().equalsIgnoreCase(UserPartnerType.XIANGQU.toString()))  // 暂时只开放"想去"
				aList.add(createThirdCommission(aUser, vo.getId()));
		}
		vo.setThirdCommissions(aList);
		return vo;
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
		atc.setThirdId(aUser.getId());
		return atc;
	}
	
}

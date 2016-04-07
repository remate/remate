package com.vdlm.web.product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;

@Controller
public class ProductController extends FragmentAndDescController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private UserService userService; 
	
	/*@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/im/detail")
	public String imDetail() {
		return "im/detail";
	}
	
	@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/im/list")
	public String imList() {
		return "im/list";
	}

	@RequestMapping(value = "/act/rushBuy")
	public String qg(Model model, HttpServletRequest req,  HttpServletResponse resp) {
		User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        }
        
		if (user != null && StringUtils.isNotEmpty(user.getPartner())) {
		    return "xiangqu/rushShoping";
		}  
		
		return "activity/rushShoping";
	}*/

	/**
	 * html5 页面 地址==/p/{id}
	 * 查看商品详情
	 * @param productId
	 * @param model
	 * @param req
	 * @param resp
	 * @param unionId
	 * @return
	 */
	/*@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/{id}")
	public String view(@PathVariable("id") String productId, Model model, HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "union_id", defaultValue = "")String unionId) {
		User seller = getProductInfo(productId,model,req);
		
		// 获取当前用户
		if (!StringUtils.isEmpty(unionId)) {
			User user = userService.load(unionId);
			if (user != null) {
				Cookie c = new Cookie("union_id", unionId);
				// expired in 15 days
				c.setMaxAge(86400 * 15);
				c.setPath("/");
				resp.addCookie(c);
			}
		}
		
		User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        }
        
        // 查看的是否是卖家自己
        if (user != null && user.getId().equals(seller.getId()))
        	model.addAttribute("imUser", false);
        else
        	model.addAttribute("imUser", seller.getFunctionSts(KdFunctionSet.FUNC_IM.ordinal()));
        
        
		if (user != null && StringUtils.isNotEmpty(user.getPartner())) {
		    return user.getPartner().toLowerCase() + "/pro";
		}   
		
		return "catalog/product";
	}
	
	private User getProductInfo(String productId, Model model, HttpServletRequest req) {
		// 获取商品Vo
		ProductVO productVO = productService.load(productId);
		if (productVO == null) {
			throw new BizException(GlobalErrorCode.NOT_FOUND, new RequestContext(req).getMessage("proudct.not.found"));
		}
		model.addAttribute("product", productVO);
		
		// 获取店铺
		Shop shop = shopService.load(productVO.getShopId());
		if (shop == null || shop.getArchive() == Boolean.TRUE) {
		    throw new BizException(GlobalErrorCode.NOT_FOUND, new RequestContext(req).getMessage("shop.not.found"));
		}
		model.addAttribute("shop", shop);

		// 获取卖家
		User seller = userService.load(shop.getOwnerId());
		if(seller==null){
			throw new BizException(GlobalErrorCode.NOT_FOUND, new RequestContext(req).getMessage("seller.not.found"));
		}
		model.addAttribute("seller", seller);
		
		//取4个相关商品
		List<Product> related = productService.listProductsByRelated(shop.getId(), productId, new PageRequest(0, 4));
		model.addAttribute("relatedProds", related);

		// 当店铺片段功能开启时，读取商品的片段信息
		model.addAttribute("fragmentStatus",shop.getFragmentStatus());
		
		// 是否开启片段
		List<FragmentVO> list = new ArrayList<FragmentVO>();
		if(shop.getFragmentStatus()){
			list = getProductFragmentList(productId);
			model.addAttribute("fragmentList",list);
			if(!(list.size() > 0)) {
				List<String> imgList = imgsList(productId);
				model.addAttribute("imgsList", imgList);
			}
		} else {
			List<String> imgList = imgsList(productId);
			model.addAttribute("fragmentList", list);
			model.addAttribute("imgsList", imgList);
		}
		
		return seller;
	}*/

	/**
	 * 商品详情
	 */
	/*@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/apidesc/{id}")
	public String apiDes(@PathVariable("id") String productId, Model model, HttpServletRequest req){
		getProductInfo(productId, model,req);
		return "catalog/apidesc";
	}
	
	private List<String> imgsList(String productId) {
		List<String> imgsList = new ArrayList<String>();
		List<ProductImage> imgs = productService.requestImgs(String.valueOf(IdTypeHandler.decode(productId)), "");
		for(ProductImage img : imgs){
			imgsList.add(resourceFacade.resolveUrl(img.getImg() + "|" + ResourceFacade.IMAGE_S1));
		}
		
		return imgsList;
	}
	
	@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/raw/{id}")
    public String redirectToView(@PathVariable("id") String id) {
	    String productId = IdTypeHandler.encode(Long.parseLong(id));
	    return "redirect:/p/" + productId;
	}
	
	@RequestMapping(value = UrlHelper.PRODUCT_URL_PREFIX + "/related/{shopId}/{prodId}")
	public String productsRelated(@PathVariable("shopId") String shopId, @PathVariable("prodId") String productId,
			Model model, @PageableDefault Pageable page, HttpServletRequest req) {
		// TODO 分页
		List<Product> related = productService.listProductsByRelated(shopId, productId, new PageRequest(0, 100));
		model.addAttribute("relatedProds", related);

		return "catalog/productsRelated";
	}*/

	/**
	 * 商品查询
	 * 
	 * @param id
	 * @return
	 */
	/*@RequestMapping("/catalog/search")
	public void searchView(@RequestParam("shopId")String shopId,
			@RequestParam(value = "key" ,required = false) String key, Model model) { 
		if(StringUtils.isNotBlank(key)) {
			List<Product> prods = productService.search(shopId, key.trim());
			model.addAttribute("prods", prods);
			if(prods==null || prods.size()==0){
				model.addAttribute("keyWord", key);
			}
		}
	}*/

	// 我的商品
    @RequestMapping(value = "/products/myproduct")
	public String myProduct(Model model) {
	  boolean hasUserAndShop = addUserAndShop(model);
	  if(!hasUserAndShop){
	   log.error("pc登录用户失败，获取用户/店铺信息失败，需要重新登录");
	   return "/login";
	  }
	  return "/products/myproduct";
	 }

	//商品分类
	@RequestMapping(value = "/products/category")
	public String proCate(Model model) {
		addUserAndShop(model);
		return "/products/category";
	}
	
	// 发布商品
	@RequestMapping(value = "/products/product")
	public String product(Model model) {
		addUserAndShop(model);
		return "/products/product";
	}
	
	@SuppressWarnings("unused")
	private boolean addUserAndShop(Model model) {
		  User user=getCurrentUser();
		  Shop shop=shopService.load(user.getShopId());
		  
		  model.addAttribute("user", user);
		  model.addAttribute("shop",shop);

		  if(user==null){
		   log.error("pc查询商品时获取用户不存在");
		   return false;
		  }
		 
		  if(shop==null){
		   log.error("pc查询商品时获取店铺"+user.getShopId()+"不存在");
		   return false;
		  }
		 
		  if(shop.getArchive()!=null && shop.getArchive()){
		   log.error("pc查询商品时获取店铺"+user.getShopId()+"店铺删除了");
		  }
		 
		  log.info("获取的用户和店铺信息为："+user.getId()+","+shop.getId());
		  return true;
	}
}


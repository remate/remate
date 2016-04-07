package com.vdlm.web.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.Category;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.ShopStyle;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.dal.vo.CategoryVO;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.category.CategoryService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.shop.ShopStyleVO;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.web.ResponseObject;

@Controller
public class ShopController {
	
	@Autowired
	private ShopService shopService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private CartService cartService;

	@Autowired
	private PricingService pricingService;
	
	@Autowired
	private AddressService addressService;
	
    @Autowired
    private ZoneService zoneService;
    
    @Autowired
    private CategoryService categoryService;
    
	@RequestMapping(value = "/shop/{id}")
	public String view(@PathVariable("id") String shopId, Model model, HttpServletRequest req) {
		Shop shop = shopService.load(shopId);
		if (shop == null || shop.getArchive() == Boolean.TRUE) {
			throw new BizException(GlobalErrorCode.NOT_FOUND, new RequestContext(req).getMessage("shop.not.found"));
		}

		model.addAttribute("shop", shop);
		model.addAttribute("owner", userService.load(shop.getOwnerId()));

		// 取推荐，和热卖商品
		List<Product> recommends = productService.listProductsByRecommend(shop.getId(), new PageRequest(0, 4));
		model.addAttribute("prodsRecommended", recommends);
		
		List<Product> prodsHot = productService.listProductsBySales(shop.getId(), new PageRequest(0, 12), Direction.DESC);
		model.addAttribute("prodsHot", prodsHot);
		
		boolean isShowAll = false;
		if (prodsHot.size() == 12) {
			Long count = productService.countProductsByStatus(shop.getId(), ProductStatus.ONSALE);
			isShowAll = (count.longValue() > 12);
		}
		model.addAttribute("isShowAll", isShowAll);
		
		ShopStyleVO shopStyle = shopService.loadShopStyle(shopId);
        List<String> bodyClasses = new ArrayList<String>();
        bodyClasses.add(shopStyle.getAvatarStyle());
        bodyClasses.add(shopStyle.getBackgroundColor());
        bodyClasses.add(shopStyle.getFontColor());
        bodyClasses.add(shopStyle.getFontFamily());
        bodyClasses.add(StringUtils.isEmpty(shopStyle.getListView()) ? "smallimg" : shopStyle.getListView());
        model.addAttribute("styles", StringUtils.join(bodyClasses, " "));
        model.addAttribute("waterfall", "waterfall".equalsIgnoreCase(shopStyle.getListView()));
        model.addAttribute("banner", shopStyle.getBannerImg());
        
        if (shop.getProvinceId() != null) {
            Zone province = zoneService.load(shop.getProvinceId().toString());
            model.addAttribute("province", province);
        }
        if (shop.getCityId() != null) {
            Zone city = zoneService.load(shop.getCityId().toString());
            model.addAttribute("city", city);
        }
        
        List<Category> categories = categoryService.listRootCategoriesByShop(shop.getId());
        List<CategoryVO> categoryVOs = new ArrayList<CategoryVO>();
        for(int i=0;i<categories.size();i++){
        	CategoryVO categoryVO = new CategoryVO();
        	BeanUtils.copyProperties(categories.get(i), categoryVO);
        	categoryVO.setProductCount(categoryService.countProductsUnderCategory((categories.get(i)).getId()));
        	categoryVOs.add(categoryVO);
        }
        model.addAttribute("categories", categoryVOs);
        
        model.addAttribute("recommendCount", productService.countByRecommend(shopId));
        model.addAttribute("allCount", productService.countProductsBySales(shopId));
        
		return "shop/shopView";
	}
	
	@RequestMapping(value = "/shop/{id}/products")
	public String productAll(@PathVariable("id") String shopId, Model model, HttpServletRequest req) {
		Shop shop = shopService.load(shopId);
		List<Product> products = productService.listProducts(shop.getId());
		model.addAttribute("shop", shop);
		model.addAttribute("products", products);
		
		ShopStyle shopStyle = shopService.loadShopStyle(shopId);
        List<String> bodyClasses = new ArrayList<String>();
        bodyClasses.add(shopStyle.getAvatarStyle());
        bodyClasses.add(shopStyle.getBackgroundColor());
        bodyClasses.add(shopStyle.getFontColor());
        bodyClasses.add(shopStyle.getFontFamily());
        bodyClasses.add(StringUtils.isEmpty(shopStyle.getListView()) ? "smallimg" : shopStyle.getListView());
        model.addAttribute("styles", StringUtils.join(bodyClasses, " "));
        model.addAttribute("waterfall", "waterfall".equalsIgnoreCase(shopStyle.getListView()));
        
		return "shop/shopAll";
	}
	
	/**
	 * 只获取店铺信息，不获取商品列表
	 * @param shopId
	 * @param model
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/shop/{id}/withoutProduct")
	public String viewWithoutProduct(@PathVariable("id") String shopId, Model model, HttpServletRequest req) {
		Shop shop = shopService.load(shopId);
		if (shop == null) {
			throw new BizException(GlobalErrorCode.NOT_FOUND, new RequestContext(req).getMessage("shop.not.found"));
		}

		model.addAttribute("shop", shop);
		model.addAttribute("owner", userService.load(shop.getOwnerId()));

		ShopStyle shopStyle = shopService.loadShopStyle(shopId);
        List<String> bodyClasses = new ArrayList<String>();
        bodyClasses.add(shopStyle.getAvatarStyle());
        bodyClasses.add(shopStyle.getBackgroundColor());
        bodyClasses.add(shopStyle.getFontColor());
        bodyClasses.add(shopStyle.getFontFamily());
        bodyClasses.add(StringUtils.isEmpty(shopStyle.getListView()) ? "smallimg" : shopStyle.getListView());
        model.addAttribute("styles", StringUtils.join(bodyClasses, " "));
        model.addAttribute("waterfall", "waterfall".equalsIgnoreCase(shopStyle.getListView()));
        
        List<Category> categories = categoryService.listRootCategoriesByShop(shop.getId());
        List<CategoryVO> categoryVOs = new ArrayList<CategoryVO>();
        for(int i=0;i<categories.size();i++){
        	CategoryVO categoryVO = new CategoryVO();
        	BeanUtils.copyProperties(categories.get(i), categoryVO);
        	categoryVO.setProductCount(categoryService.countProductsUnderCategory((categories.get(i)).getId()));
        	categoryVOs.add(categoryVO);
        }
        model.addAttribute("categories", categoryVOs);
        
        model.addAttribute("recommendCount", productService.countByRecommend(shopId));
        model.addAttribute("allCount", productService.countProductsBySales(shopId));
        
		return "shop/shopView";
	}
	
	@RequestMapping(value = "/shop/{id}/recommendProduct")
	public String listProductsByRecommend(@PathVariable("id") String shopId, Model model){
		List<Product> recommends = productService.listProductsByRecommend(shopId, new PageRequest(0, 12));
		model.addAttribute("prods", recommends);
		return "fragments/shopProducts";
	}
	
	@RequestMapping(value = "/shop/{id}/hotSaleProduct")
	public String listProductsBySales(@PathVariable("id") String shopId, Model model){
		List<Product> prodsHot = productService.listProductsBySales(shopId, new PageRequest(0, 12), Direction.DESC);
		model.addAttribute("prods", prodsHot);
		return "fragments/shopProducts";
	}
	
	@RequestMapping(value = "/shop/{id}/productByPage")
	public String productAll(@PathVariable("id") String shopId, Pageable page, Model model){
		List<Product> products = productService.listProductsBySales(shopId, page, Direction.DESC);
		Long cnt = productService.countProductsByStatus(shopId, ProductStatus.ONSALE);
		model.addAttribute("prods", products);
		model.addAttribute("total", cnt);
		return "fragments/shopProducts";
	}
	
	@RequestMapping(value = "/shop/{id}/category/{cid}")
    public String productAll(@PathVariable("id") String shopId, @PathVariable("cid") String categoryId, Pageable page, Model model){
        List<Product> products = categoryService.listProductsInCategory(categoryId, page);
        model.addAttribute("prods", products);
        return "fragments/shopProducts";
    }
	
	@RequestMapping(value = "/shop/{id}/cart")
	public String cart(@PathVariable("id") String shopId, Model model, HttpServletRequest req) {
		List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
		PricingResultVO prices = new PricingResultVO();
		
		// 购物车没有传递item参数，取用户购物车所有项目
		List<CartItemVO> items = cartService.listCartItems(shopId);
		Map<String, Integer> skuMap = new HashMap<String, Integer>();
		for (CartItem item : items) {
			CartItemVO itemVO = new CartItemVO();
			BeanUtils.copyProperties(item, itemVO);
			itemVO.setProduct(productService.load(item.getProductId()));
			Sku sku = productService.loadSku(item.getProductId(), item.getSkuId());
			if (sku == null) {
				continue;
			}
			itemVO.setSku(sku);
			cartItems.add(itemVO);
			skuMap.put(sku.getId(), item.getAmount());
		}
		prices = pricingService.calculate(skuMap, null, null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("prices", prices);
		
		String nextUrl = "/shop/" + shopId + "/cart-next";
		model.addAttribute("nextUrl", nextUrl);
		return "cart/cart";
	}
	
	@RequestMapping(value = "/shop/{id}/cart-next")
	public String next(@PathVariable("id")String shopId, Model model, HttpServletRequest req) {
		List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
		PricingResultVO prices = new PricingResultVO();
		
		List<CartItemVO> items = cartService.listCartItems(shopId);
		Map<String, Integer> skuMap = new HashMap<String, Integer>();
		for (CartItem item : items) {
			Sku sku = productService.loadSku(item.getProductId(), item.getSkuId());
			if (sku == null) {
				continue;
			}
			CartItemVO itemVO = new CartItemVO();
			BeanUtils.copyProperties(item, itemVO);
			itemVO.setProduct(productService.load(item.getProductId()));
			itemVO.setSku(sku);
			cartItems.add(itemVO);
			skuMap.put(sku.getId(), item.getAmount());
		}
		prices = pricingService.calculate(skuMap, null, null);
		
		if (cartItems.size() == 0) {
			return "redirect:/shop/" + shopId + "/cart";
		}
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("prices", prices);
		model.addAttribute("shopId", shopId);
		
		List<AddressVO> addresses = addressService.listUserAddressesVo();
		if (addresses != null && addresses.size()>0){
			model.addAttribute("address", addresses.get(0));			
		}
	    return "cart/next";
	}
	
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step1")
	public String step1() {
		return "shop/step1";
	}
	
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step2")
	public String step2() {
		return "shop/step2";
	}
	
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step3")
	public String step3() {
		return "shop/step3";
	}
	
	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step4")
	public String step4() {
		return "shop/step4";
	}

	@RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/step5")
	public String step5() {
	    return "shop/step5";
	}
	
	@ResponseBody
    @RequestMapping(UrlHelper.SHOP_URL_PREFIX + "/{id}/styles")
    public ResponseObject<ShopStyleVO> shopStylesCss(@PathVariable String id) {
        ShopStyleVO ss = shopService.loadShopStyle(id);
        return new ResponseObject<ShopStyleVO>(ss);
    }
}

package com.vdlm.web.im;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.web.BaseController;

@Controller
public class ImController extends BaseController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AddressService addressService;
	
    @Autowired
    private OrderService  orderService;
    
    @Autowired
    private UserService  userService;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private ResourceFacade resourceFacade;
    
	@Value("${site.web.host.name}")
	private String domainName;
	
	@Value("${im.http.host.url}")
	private String imUrl;
    
    private ImProductVO  getProdInfo4Im(String id) {
    	ImProductVO vo = new ImProductVO();
    	ProductVO p = productService.load(id);
    	User user = this.getCurrentUser();
    	if (p != null && user != null) {
    		vo.setId(p.getId());
    		vo.setName(p.getName());
    		vo.setProductName(p.getName());
    		vo.setAmount(p.getAmount());
    		vo.setSales(p.getSales());
    		vo.setProductUrl(domainName + UrlHelper.PRODUCT_URL_PREFIX + "/" + p.getId()); 
    		vo.setPrice(p.getPrice());
    		vo.setImgUrl(resourceFacade.resolveUrl(p.getImgUrl()));
    		vo.setProductImgUrl(resourceFacade.resolveUrl(p.getImgUrl()));
    		vo.setDescription(p.getDescription());
    		vo.setSellerId(p.getUserId());
    		vo.setBuyerId(user.getId());
    	} else {
    		return null;
    	}
    	return vo;
    }
    
	@RequestMapping(value = "/im/p/{id}")
	public String viewProduct(@PathVariable("id") String id, Model model, HttpServletRequest req) {

    	ImProductVO vo = getProdInfo4Im(id);
    	if (vo == null) {
    		model.addAttribute("ERROR", "商品不存在");
    	} else {
    	    model.addAttribute("buyerId", vo.getBuyerId());
    	    model.addAttribute("sellerId", vo.getSellerId());
    	    model.addAttribute("imUrl", imUrl);
    	    
    	    User user = userService.load(vo.getSellerId());
	    	Shop shop = shopService.load(user.getShopId());
    	    if (user != null && shop != null ) {
	    	    model.addAttribute("nickName", user.getName());
	    	    if (shop.getImg() != null)
		    	    model.addAttribute("avatar", resourceFacade.resolveUrl(shop.getImg()));
	    	    else 
	    	    	model.addAttribute("avatar", resourceFacade.getDefLogo());
    	    }
    		model.addAttribute("productInfo", JSONObject.toJSONString(vo));
    	}
    	
		return "im/detail";
	}
	
	@RequestMapping(value = "/im/order/{id}")
	public String viewOrder(@PathVariable("id") String orderId, Model model, HttpServletRequest req) {
    	ImOrderVO ret = new ImOrderVO();
    	OrderVO vo = orderService.loadVO(orderId);
    	List<ImProductVO> list = new ArrayList<ImProductVO>();
    	if (vo != null)  {
    		ret.setShopId(vo.getShopId());
    		ret.setId(vo.getId());
    		ret.setOrderNo(vo.getOrderNo());
    		ret.setImgUrl(vo.getImgUrl());
    		ret.setImgUrl(resourceFacade.resolveUrl(vo.getImgUrl()));
    		ret.setAddress(vo.getOrderAddress());
    		if (vo.getOrderItems() != null && vo.getOrderItems().size() != 0) {
	    		for (OrderItem oi : vo.getOrderItems()) {
	    			list.add(getProdInfo4Im(oi.getProductId()));
	    		}
    		}
    		//ret.setProds(list);
    		ret.setOrderItems(list);
    	    model.addAttribute("buyerId", vo.getBuyerId());
    	    model.addAttribute("sellerId", vo.getSellerId());
    	    model.addAttribute("imUrl", imUrl);
    		model.addAttribute("orderInfo", JSONObject.toJSONString(ret));
    		
    	    User user = userService.load(vo.getSellerId());
	    	Shop shop = shopService.load(user.getShopId());
    	    if (user != null && shop != null ) {
	    	    model.addAttribute("nickName", user.getName());
	    	    if (shop.getImg() != null)
		    	    model.addAttribute("avatar", resourceFacade.resolveUrl(shop.getImg()));
	    	    else 
	    	    	model.addAttribute("avatar", resourceFacade.getDefLogo());
    	    }
    	} else {
    		model.addAttribute("ERROR", "订单不存在");
    	}
		return "im/detail";
	}
	
	@RequestMapping(value = "/im/list")
	public String viewImList(Model model, HttpServletRequest req) {
    	
    	ImListVO ret = new ImListVO();
    	ret.setBuyerId(this.getCurrentUser().getId());
    	
    	if (ret != null && !ret.getBuyerId().isEmpty())  {
    		model.addAttribute("buyerId", ret.getBuyerId());
    		model.addAttribute("imUrl", imUrl);
    	} else {
    		model.addAttribute("ERROR", "用户不存在");
    	}
		return "im/list";
	}
	
	@RequestMapping(value = "/im/chatwith/{toId}")
	public String chatwith(@PathVariable("toId") String toId, Model model, HttpServletRequest req) {
    	
		User user = null;
		Shop shop = null;
		user = userService.load(toId);
		if (user != null)
			shop = shopService.load(user.getShopId());
		
    	if (user != null && shop != null)  {
    		model.addAttribute("buyerId", this.getCurrentUser().getId());
    	    model.addAttribute("sellerId", toId);
    	    model.addAttribute("imUrl", imUrl);
    	    model.addAttribute("nickName", user.getName());
    	    if (shop.getImg() != null)
	    	    model.addAttribute("avatar", resourceFacade.resolveUrl(shop.getImg()));
    	    else 
    	    	model.addAttribute("avatar", resourceFacade.getDefLogo());
    	} else {
    		model.addAttribute("ERROR", "用户不存在");
    	}
		return "im/detail";
	}
}

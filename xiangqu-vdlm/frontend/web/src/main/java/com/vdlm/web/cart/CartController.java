package com.vdlm.web.cart;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.verify.VerificationFacade;
import com.vdlm.config.GlobalConfig;
import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.service.pricing.DiscountService;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.pricing.vo.CartPricingResultVO;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.cart.form.AddToCartForm;
import com.vdlm.web.cart.form.CartForm;
import com.vdlm.web.cart.form.CartNextForm;
import com.vdlm.web.cart.form.CartPricingForm;

/**
 * 购物车相关逻辑
 *
 * @author odin
 * @author tonghu
 */
@Controller
public abstract class CartController extends BaseController {

	
//  private final String MSG_KEY_SKU_NOT_EXSIT = "product.sku.not_exist";
//  private final String MSG_KEY_SKU_OUT_OF_STOCK = "product.sku.out.of.stock";
//  private final String MSG_KEY_SKU_SHORT_OF_STOCK = "product.sku.short.of.stock";
//  private final String MSG_KEY_CART_ITEM_EXCEED_OF_STOCK = "product.item.exceed.of.stock";

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

	@Autowired
	private VerificationFacade veriFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    protected CouponService couponService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OutPayAgreementService outPayAgreementService;

    @Autowired
    private RestOperations restTemplate;

    @Autowired
    protected CashierService cashierService;

    @Autowired
	protected PayBankService payBankService;

    @Autowired
    private ZoneService zoneService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${xiangqu.web.site}")
    private String xiangquWebSite;

    @Value("${xiangqu.web.head.url}")
    private String xiangquWebHeadUrl;

    @Value("${xiangqu.web.foot.url}")
    private String xiangquWebFootUrl;

    /**
     * 商品加入购物车
     * @param form
     * @param errors
     * @return
     */
    public ResponseObject<Integer> add(@Valid @ModelAttribute AddToCartForm form, HttpServletRequest req, Errors errors, String domain, String source) {
        try {
            ControllerHelper.checkException(errors);
            User user = getCurrentUser();
            cartService.addToCart(user.getId(), form.getSkuId(), form.getAmount());

            Integer count = 0;
            List<CartItemVO> cartItems = cartService.checkout();
            for (CartItemVO cartItemVO : cartItems) {
                if (cartItemVO.getAmount() > 0) {
                    count++;
                }
            }
            return new ResponseObject<Integer>(count);
        } catch (BizException e) {
            return new ResponseObject<Integer>(e);
        }
    }

    /**
     * 商品直接购买
     * @param form
     * @param model
     * @param req
     * @return
     */
    public ResponseObject<Boolean> buy(@ModelAttribute AddToCartForm form, HttpServletRequest req, Model model) {
        int amount = form.getAmount() == null || form.getAmount().intValue() == 0 ? 1 : form.getAmount();
        cartService.saveOrUpdateCartItemAmount(form.getSkuId(), amount);
        return new ResponseObject<Boolean>(true);
    }

    /**
     * 想去检查skus，在cart/next之前
     * @param skuId
     * @param amount
     * @param model
     * @return
     */
    public String checkout(String skuId, @RequestParam(defaultValue = "1") Integer amount,
    		HttpServletRequest request, HttpServletResponse response, Device device, Model model) {
        User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        }

        String partner = request.getParameter("partner");
        if ("xiangqu".equals(partner) && (user == null || !"xiangqu".equals(user.getPartner()))) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for(Cookie cookie : cookies) {
//                    if (cookie.getName().equals("KDSESSID") && request.getSession().getId().equals(cookie.getValue())) { //
//                        log.info("KDSESSID:" + cookie.getValue());
//                        // 删除子域的domain
//                        cookie.setPath("/");
//                        cookie.setMaxAge(0);
//                        response.addCookie(cookie);
//                    }
//                }
//            }

            String requestURL = request.getRequestURL().toString();
            if (request.getQueryString() != null) {
                requestURL += "?" + request.getQueryString();
            }
            String xiangquGetTokenUrl = "/";
            try {
                xiangquGetTokenUrl = xiangquWebSite + "/ouer/getToken?backUrl=" + URLEncoder.encode(requestURL, "utf-8");
            } catch (UnsupportedEncodingException e) {
            	log.error("访问购物车出错", e);
            }
            return "redirect:" + xiangquGetTokenUrl;
        }

        if (skuId != null) {
        	Sku sku = productService.loadSku(skuId);
            if ( sku != null ) {
                ProductVO aVo = productService.load(sku.getProductId());

                if (aVo == null || ! aVo.getStatus().equals(ProductStatus.ONSALE)) {
                    if (user.getPartner() != null) {
                        //model.addAttribute("prodId", aVo.getId());
                        return user.getPartner() + "/merchandiseOffShelf";
                    } else {
                        return "cart/merchandiseOffShelf";
                    }
                }
            }
        }

        //if (amount > 0) {
            //cartService.saveOrUpdateCartItemAmount(skuId, amount);
        //}
        return "redirect:/cart/next?skuId=" + skuId+"&qty="+amount+"&tuid="+request.getParameter("tuid");
    }

    /**
     * 购物车页面
     * @param req
     * @param model
     * @return
     */
    public String cart(@ModelAttribute CartForm form, HttpServletRequest req, Device device, Model model) {
        // 获取购物车参数
        String shopId = form.getShopId();
        // 去掉参数中重复的skuId
        List<String> skuIds = null;
        if (form.getSkuIds() != null) {
            skuIds = new ArrayList<String>(new HashSet<String>(form.getSkuIds()));
        } else {
            skuIds = new ArrayList<String>();
        }

        Integer amount = form.getAmount();

        List<String> errors = new ArrayList<String>();
        List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
        String nextUrl = "";

        if (skuIds != null && skuIds.size() > 0) {
            // 过滤相同的sku
            Set<String> skuIdSet = new LinkedHashSet<String>(skuIds);
            if (skuIds.size() == 1 && amount != null) {
                String sid = (String)skuIds.toArray()[0];
                cartService.saveOrUpdateCartItemAmount(sid, amount);
            }
            try {
                cartItems = cartService.checkout(skuIdSet);
            } catch (BizException e) {
                errors.add(e.getMessage());
                cartItems = cartService.listCartItems(skuIdSet);
            }
            nextUrl = "/cart/next?skuId=" + skuIdSet;
        } else if (StringUtils.isNotEmpty(shopId)) {
            try {
                cartItems = cartService.checkout(shopId);
            } catch (BizException e) {
                errors.add(e.getMessage());
                cartItems = cartService.listCartItems(shopId);
            }
            nextUrl = "/cart/next?shopId=" + shopId;
        } else {
            try {
                cartItems = cartService.checkout();
            } catch (BizException e) {
                errors.add(e.getMessage());
                // cartItems = cartService.listCartItems();
            }
            nextUrl = "/cart/next";
        }

        Map<String, Integer> skuMap = new HashMap<String, Integer>();
        Map<Shop, List<CartItemVO>> cartItemMap = new LinkedHashMap<Shop, List<CartItemVO>>();
        for (CartItemVO item : cartItems) {
            skuMap.put(item.getSkuId(), item.getAmount());
            List<CartItemVO> list = cartItemMap.get(item.getShop());
            if (list == null) {
                list = new ArrayList<CartItemVO>();
                cartItemMap.put(item.getShop(), list);
            }
            list.add(item);
        }

        // 计算购物车费用
        PricingResultVO prices = pricingService.calculate(skuMap, null, null);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItemMap", cartItemMap);
        model.addAttribute("prices", prices);
        model.addAttribute("nextUrl", nextUrl);
        model.addAttribute("skuIds", skuIds);
        model.addAttribute("shopId", shopId);
        model.addAttribute("errors", errors);

        return "cart/cart";
    }

    public ResponseObject<Integer> count() {
        Integer count = cartService.count();
//        Integer count = 0;
//        List<CartItemVO> cartItems = cartService.checkout();
//        for (CartItemVO cartItemVO : cartItems) {
//            if (cartItemVO.getAmount() > 0) {
//                count++;
//            }
//        }
        return new ResponseObject<Integer>(count);
    }

    public ResponseObject<Integer> count(String userId) {
        Integer count = cartService.count(userId);
        return new ResponseObject<Integer>(count);
    }

    public ResponseObject<Integer> count(String partner, String thirdId) {
        //User user = userService.loadByLoginname(thirdId + "@" + partner);
        User user = userService.loadByDomainAndExtUid(partner, thirdId);
        Integer count = 0;
        if (user != null) {
            count = cartService.count(user.getId());
        } else {
            count = cartService.count();
        }
        return new ResponseObject<Integer>(count);
    }

    /**
     * 更新购物车中商品的数量
     * @param skuId
     * @param amount
     * @param req
     * @return
     */
    public ResponseObject<Boolean> update(@RequestParam String skuId, @RequestParam int amount, HttpServletRequest req) {
        try {
            // TODO 参数校验
            cartService.saveOrUpdateCartItemAmount(skuId, amount);
            return new ResponseObject<Boolean>(true);
        } catch (BizException e) {
            return new ResponseObject<Boolean>(e);
        }
    }

    public ResponseObject<Boolean> delete(String itemId) {
        // TODO 参数校验
        return new ResponseObject<Boolean>(cartService.remove(itemId));
    }

    /**
     * ajax调用 检查商品规格
     * @param req
     * @return
     */
    public ResponseObject<Boolean> validate(HttpServletRequest req) {
        String skuId = req.getParameter("skuId");
        String shopId = req.getParameter("shopId");

        if (StringUtils.isNotEmpty(skuId)) {
            List<String> skuIds = Arrays.asList(skuId.split("-"));
            try {
                cartService.validate(skuIds);
            } catch (BizException ex) {
                return new ResponseObject<Boolean>(ex);
            }
        } else if (StringUtils.isNotEmpty(shopId)) {
            try {
                cartService.validate(shopId);
            } catch (BizException ex) {
                return new ResponseObject<Boolean>(ex);
            }
        } else {
            return new ResponseObject<Boolean>("参数错误，skuId, shopId其中一个不能为空", GlobalErrorCode.INVALID_ARGUMENT);
        }
        return new ResponseObject<Boolean>(true);
    }
    
    private AddressVO fillAddress(Model model) {
        List<AddressVO> addresses = addressService.listUserAddressesVo();
        AddressVO address = CollectionUtils.isEmpty(addresses) ? null : addresses.get(0);

        if (address != null) {
            List<Zone> parents = zoneService.listParents(address.getZoneId());
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
        }    	
        model.addAttribute("address", address);
        return address;
    }

    /**
     * 提交订单页面，选择收件地址，优惠，支付方式
     * @param req
     * @param model
     * @return
     */
    public String next(@ModelAttribute CartNextForm form, HttpServletRequest req, Device device, Model model, HttpServletResponse response) {
        /*
    	String redirect = redirectDomain(req, response);
        if (StringUtils.isNotEmpty(redirect)) {
            return redirect;
        }
        */
        

        List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
  
        if(form.getQty()>0){
        	// 直接下单,不走购物车流程
        	Set<String> skuIds = new LinkedHashSet<String>(form.getSkuId());
        	cartItems = cartService.checkout(skuIds,form.getQty());
        }
        else if (form.getSkuId() != null && form.getSkuId().size() > 0) {
            // 以sku结算
            Set<String> skuIds = new LinkedHashSet<String>(form.getSkuId());
            cartItems = cartService.checkout(skuIds);           
        } else if (StringUtils.isNotEmpty(form.getShopId())) {
            // 以店铺结算
            cartItems = cartService.checkout(form.getShopId());
        } else {
            // do nothing
        }

        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请选择要结算的商品");
        }

        // 购物车商品按照店铺分组，注意shop做key，需要实现shop的equals方法
        Map<Shop, List<CartItemVO>> cartItemMap = new HashMap<Shop, List<CartItemVO>>();
        Map<String, Integer> cartShopItemArraySize = new HashMap<String, Integer>();
        for (CartItemVO item : cartItems) {
            List<CartItemVO> list = cartItemMap.get(item.getShop());
            Integer shopItemArraySize = cartShopItemArraySize.get(item.getShopId());
            if (list == null) {
            	shopItemArraySize = 0;
                list = new ArrayList<CartItemVO>();
                cartItemMap.put(item.getShop(), list);
                logger.debug("put shop[" + item.getShop().getId() + "]");
            }
            list.add(item);
            shopItemArraySize += item.getAmount();
            cartShopItemArraySize.put(item.getShopId(), shopItemArraySize);
        }

        AddressVO address = fillAddress(model);
        
        Shop shop = (Shop) cartItemMap.keySet().toArray()[0];
        model.addAttribute("skuId", form.getSkuId() != null ? form.getSkuId().get(0) : "");
        model.addAttribute("shopId", form.getShopId());
        model.addAttribute("shop", shop);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartItem", cartItems.get(0));
        model.addAttribute("cartItemMap", cartItemMap);
        model.addAttribute("cartShopItemArraySize", cartShopItemArraySize);
        model.addAttribute("xiangquWebSite", xiangquWebSite);
        model.addAttribute("tuid", req.getParameter("tuid"));
        //openUrl 针对wap端 方便调试
        if (cartItems.size() > 1) {
        	model.addAttribute("openUrl", globalConfig.getProperty("xiangqu.wap.index", "http://www.xiangqu.com/"));
        } else {
        	String openUrl = globalConfig.getProperty("xiangqu.wap.detail", "http://www.xiangqu.com/dtl/");
        	openUrl += cartItems.get(0).getProductId() + ".html";
        	model.addAttribute("openUrl", openUrl);
        }

   		int totalCount = 0;
        Map<String, Map<String, Integer>> groupByShopMap = new HashMap<String, Map<String, Integer>>();
        for (CartItemVO item : cartItems) {
            String shopId = item.getShop().getId();
            if (shopId == null) {
                throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "店铺不能为空");
            }
            Map<String, Integer> map = groupByShopMap.get(shopId);
            if (map == null) {
                map = new HashMap<String, Integer>();
                groupByShopMap.put(shopId, map);
            }
            map.put(item.getSkuId(), item.getAmount());
            totalCount += item.getAmount();
        }
        model.addAttribute("totalCount", totalCount);
        CartPricingResultVO prices = new CartPricingResultVO();
        String zoneId = null;
        if (address != null) {
            zoneId = address.getZoneId();
        }
        model.addAttribute("zoneId", zoneId);
        for (Entry<String, Map<String, Integer>> entry : groupByShopMap.entrySet()) {
            PricingResultVO price = pricingService.calculate(entry.getValue(), zoneId, null);
            prices.putPrices(entry.getKey(), price);
            logger.debug("put price by shop [" + entry.getKey() + "]");
        }

        model.addAttribute("prices", prices);

        //放入协议支付的银行
        model.addAttribute("userAgreeBanks", outPayAgreementService.listBankByUserId(getCurrentUser().getId()));

        User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        }
        
        //临时解决ipad问题，强制升级ipad后，去掉该代码
        String partner = user.getPartner();
        if("kkkd".equals(partner)){
        	partner = null;
        }
        
        // 将来根据partner扩展不同的合作方
        if (user != null && StringUtils.isNotBlank(partner)) {
            if (!device.isNormal()) {
            //if (StringUtils.isNotEmpty(user.getPartner())) {
             	UserAgent ua = UserAgentUtils.getUserAgent(req);
             	
             	Map<String, List<CouponVO>> couponsMap = couponService.listValidsWithExt(prices.getGoodsFee(), "", ua.getChannel());
			    List<CouponVO> defCoupons = new ArrayList<CouponVO>();
			    for(String actCode : couponsMap.keySet()) {
			    	List<CouponVO> coupons = couponsMap.get(actCode);
			    	
				    for(CouponVO coupon : coupons) {
		        	if( coupon.getActivity().getDefaultSelect() )
		        		defCoupons.add(coupon);
		        	}
		        }
			    
			    if (defCoupons.size() != 0)
			    	model.addAttribute("defaultCoupon", defCoupons);
            	
                return user.getPartner() + "/wap/cart_next";
            } else {
                List<PayBankWay> hotBanksCreditCard = payBankService.queryHotPayBanksCreditCard();
                List<PayBankWay> allBanksCreditCard = payBankService.queryAllPayBanksCreditCard();
                List<BankItemsMap> creditCardBanks = trans4Show(hotBanksCreditCard, allBanksCreditCard);

                List<PayBankWay> hotBanksDebitCard = payBankService.queryHotPayBanksDebitCard();
                List<PayBankWay> allBanksDebitCard = payBankService.queryAllPayBanksDebitCard();
                List<BankItemsMap> debitCardBanks = trans4Show(hotBanksDebitCard, allBanksDebitCard);

                model.addAttribute("creditCardBanks", creditCardBanks);
                model.addAttribute("debitCardBanks", debitCardBanks);
                UserAgent ua = UserAgentUtils.getUserAgent(req);
                Map<String, List<CouponVO>> couponMap = couponService.listValidsWithExt(prices.getTotalFee(), null, ua.getChannel());

                // FIXME 修改红包使用方案
                if (prices != null && prices.getGoodsFee().doubleValue() <= 25 && couponMap.get("XQ.HONGBAO") != null) {
                	couponMap.remove("XQ.HONGBAO");
                }

                model.addAttribute("XQ_FIRST", couponMap.containsKey("XQ.FIRST")? couponMap.get("XQ.FIRST").get(0) : null);
                model.addAttribute("XQ_HONGBAO", couponMap.containsKey("XQ.HONGBAO")? couponMap.get("XQ.HONGBAO") : null);
                
                //TODO 11-11
                List<CouponVO> temp = couponService.dealCoupons(couponMap);
                List<CouponVO> result = new ArrayList<>();
                List<String> is = Arrays.asList(new String[]{"22","23","24","26","27","28","29","30"});
                for(CouponVO c:temp){
                	if(!is.contains(c.getActivityId())){
                		result.add(c);
                	}
                }
		   		model.addAttribute("coupons", result);

                model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
                model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);

                return user.getPartner() + "/web/cart/cart_next";
            }
        } else {
            return "cart/next";
        }
    }
    

    public ResponseObject<PricingResultVO> pricing(@Valid @ModelAttribute CartPricingForm form, HttpServletRequest req, Model model) {
        List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
        PricingResultVO prices = new PricingResultVO();

//        String skuId = req.getParameter("skuId");
//        String shopId = req.getParameter("shopId");
//        String zoneId = req.getParameter("zoneId");
//        String couponId = req.getParameter("couponId");
//        String promotionId = req.getParameter("promotionId");

        Set<String> skuIds = new LinkedHashSet<String>(form.getSkuIds());
        cartItems = cartService.checkout(skuIds);

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (CartItemVO item : cartItems) {
            map.put(item.getSkuId(), item.getAmount());
        }

        // 计价
        prices = pricingService.calculate(map, form.getZoneId(), form.getCouponId());
        // prices = pricingService.calculate(shopId, zoneId, couponId);

        return new ResponseObject<PricingResultVO>(prices);
    }

    public ResponseObject<CartPricingResultVO> pricingGroupByShop(@Valid @ModelAttribute CartPricingForm form, HttpServletRequest req, Model model) {
        List<CartItemVO> cartItems = new ArrayList<CartItemVO>();

        Set<String> skuIds = new LinkedHashSet<String>(form.getSkuIds());
        cartItems = cartService.checkout(skuIds);

        Map<String, Map<String, Integer>> groupByShopMap = new HashMap<String, Map<String, Integer>>();
        for (CartItemVO item : cartItems) {
            Map<String, Integer> map = groupByShopMap.get(item.getShopId());
            if (map == null) {
                map = new HashMap<String, Integer>();
                groupByShopMap.put(item.getShopId(), map);
            }
            //qty>0表示该请求是直接下单并且商品数量大于1
            if(form.getQty() > 0){
            	item.setAmount(form.getQty());
            }
            map.put(item.getSkuId(), item.getAmount());
        }

        CartPricingResultVO result = new CartPricingResultVO();
        for (Entry<String, Map<String, Integer>> entry : groupByShopMap.entrySet()) {
            PricingResultVO prices = pricingService.calculate(entry.getValue(), form.getZoneId(), null);
            result.putPrices(entry.getKey(), prices);
        }
        return new ResponseObject<CartPricingResultVO>(result);
    }

    protected List<BankItemsMap> trans4Show(List<PayBankWay> hotBanks, List<PayBankWay> allBanks){
		List<BankItemsMap> result = new ArrayList<BankItemsMap>();
		if (hotBanks != null && hotBanks.size() > 0) {
			BankItemsMap map = new BankItemsMap();
			map.setKeyName("hot");
			map.setValueList(hotBanks);
			result.add(map);
		}

		List<String> keys = new ArrayList<String>();
		Map<String, List<PayBankWay>> temp = new HashMap<String, List<PayBankWay>>();
		if (allBanks != null && allBanks.size() > 0) {
			for (int i = 0; i < allBanks.size(); i++) {
				PayBankWay bean = allBanks.get(i);
				String key = bean.getStartWith();
				if (!keys.contains(key)) {
					keys.add(key);
					List<PayBankWay> list = new ArrayList<PayBankWay>();
					list.add(bean);
					temp.put(key, list);
				} else {
					temp.get(key).add(bean);
				}
			}
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			List<PayBankWay> list = temp.get(key);
			BankItemsMap map = new BankItemsMap();
			map.setKeyName(key);
			map.setValueList(list);
			result.add(map);
		}
		return result;
	}

}

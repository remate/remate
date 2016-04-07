package com.vdlm.openapi.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vdlm.dal.model.BankItemsMap;
import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.PayBankWay;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.CouponStatus;
import com.vdlm.dal.vo.ShopVO;
import com.vdlm.openapi.OpenApiController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.ShopCartItemVo;
import com.vdlm.restapi.order.CouponForm;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;
import com.vdlm.service.cart.CartNextForm;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemGroupVO;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.pricing.vo.OrderPricingVO;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.service.pricing.vo.PricingResultVOEX;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController("openapiCartController")
public class CartController extends OpenApiController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private ZoneService zoneService;
    
    @Autowired
    private UserService userServcie;
    
    @Autowired
    private PricingService pricingService;
    
    @Autowired
	protected PayBankService payBankService;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private ProductService productService;
    
    
    
    @Autowired
    private OutPayAgreementService outPayAgreementService;
    
    
    @Value("${xiangqu.web.site}")
    private String xiangquWebSite;
    

    @Value("${xiangqu.web.foot.url}")
    private String xiangquWebFootUrl;
    

    @Value("${xiangqu.web.head.url}")
    private String xiangquWebHeadUrl;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    
    
    /**
     * 添加商品到购物车
     * @param extUid
     * @param skuId
     * @param amount 是需要添加到购物车中的值》0
     * @param domain
     * @return
     */
    @ApiOperation("add sku into user's cart")
    @RequestMapping(value = "/cart/add")
    public ResponseObject<CartItem> cartAdd(@RequestParam String extUid, @RequestParam String skuId, 
            @RequestParam(required = false, defaultValue = "1") Integer amount, @RequestHeader("Domain") String domain) {
        User user = loadExtUser(domain, extUid);
        CartItem ci = cartService.addToCart(user.getId(), skuId, amount);
        return new ResponseObject<CartItem>(ci);
    }
    
    /**
     * 购物车商品数量更新amount值是现在skuid在购物车中的值
     */
    @ResponseBody
    @RequestMapping("/cart/update")
    public ResponseObject<Boolean> update(@RequestParam String extUid,@RequestHeader("Domain") String domain,@RequestParam("id") String id, @RequestParam("amount") Integer amount) {
    	User user = loadExtUser(domain, extUid);
    	CartItem ci = cartService.saveOrUpdateCartItemAmount(user.getId(),id, amount);
        return new ResponseObject<Boolean>(ci != null);
    }
    
    /**
     * 删除购物车一条元素
     * openapi/cart/delete?id=938925&extUid=123&domain=xiangqu
     */
    @ResponseBody
    @RequestMapping("/b/cart/delete")
    public ResponseObject<Boolean> delete(@RequestParam String id, @RequestParam String extUid, String domain) {
    	User user=loadExtUser(domain, extUid);
    	return new ResponseObject<Boolean>(cartService.remove(id,user.getId()));
    }
    
    /**
     * 获取用户的购物车内容
     * 某种以店铺为单位的集合
     * openapi/cart?extUid=111
     */
    @RequestMapping("/cart")
    @ResponseBody
    public ResponseObject<List<CartItemGroupVO>> cart(@RequestParam String extUid,@RequestHeader("Domain") String domain) {
    	User user = loadExtUser(domain, extUid);
    	// 获取用户的购物车
        List<CartItemVO> cartItems = cartService.checkout2(user.getId());
        
        Map<Shop, List<CartItemVO>> cartItemMap = new LinkedHashMap<Shop, List<CartItemVO>>();
        for (CartItemVO item : cartItems) {
        	if (null != item.getProduct()){
        		item.getProduct().setImgUrl(item.getProduct().getImg());        		
        	}
            List<CartItemVO> list = cartItemMap.get(item.getShop());
            if (list == null) {
                list = new ArrayList<CartItemVO>();
                cartItemMap.put(item.getShop(), list);
            }
            list.add(item);
        }
        
        List<CartItemGroupVO> result = new ArrayList<CartItemGroupVO>();
        for (Entry<Shop, List<CartItemVO>> entry : cartItemMap.entrySet()) {
            CartItemGroupVO vo = new CartItemGroupVO();
            vo.setShop(entry.getKey());
            vo.setCartItems(entry.getValue());
            vo.setSeller(userService.load(vo.getShop().getOwnerId()));
            result.add(vo);
        }
        
        return new ResponseObject<List<CartItemGroupVO>>(result);
    }
    
    /**
     * 获取用户购物车数量
     * openapi/cart/count?extUid=137070
     * 
     */
    @ApiOperation("get user's cart count")
    @RequestMapping(value = "/cart/count")
    @ResponseBody
    public ResponseObject<Integer> cartCount(@RequestParam String extUid, @RequestHeader("Domain") String domain, @RequestHeader("User-DeviceId") String deviceId) {
    	
    	User user = userServcie.loadByDomainAndExtUid(domain, extUid);
    	if (user == null) 
    		return new ResponseObject<Integer>(0);
    	
    	couponService.autoGrantCoupon(domain, user.getId(), deviceId);
    	
    	return new ResponseObject<Integer>(cartService.count(user.getId()));
    }
    
    
    
    @ResponseBody
    @RequestMapping(value = "/cart/loadCoupons")
    public ResponseObject<Map<String, List<CouponVO>>> getCouponList(String skuJson,String zoneId, String shopId, HttpServletRequest request){
    	UserAgent ua = UserAgentUtils.getUserAgent(request);
    	Map<String,Integer> skuMap=Maps.newHashMap();
        List<OrderSkuVO> skuList=JSON.parseArray(skuJson,OrderSkuVO.class);
        for(OrderSkuVO vo:skuList)
        	skuMap.put(vo.getSkuId(), vo.getQty());
        PricingResultVO priceResultVo = pricingService.calculate(skuMap, zoneId, null);
        Map<String, List<CouponVO>> coupons = couponService.listValidsWithExt(priceResultVo.getTotalFee(), null, ua.getChannel());
	    return new ResponseObject<Map<String, List<CouponVO>>>(coupons);
    }
   
    
    
    @ResponseBody
    @RequestMapping(value = "/cart/loadCartItems")
    public ResponseObject<List<ShopCartItemVo> > getCartItems (@ModelAttribute CartNextForm form){
//    	  User user=loadExtUser(domain, extUid);
    	  List<ShopCartItemVo>   cartItems=getCartItemVos(form, getCurrentUser());
    	  return new ResponseObject<List<ShopCartItemVo>>(cartItems);
    }
    
    
    private  List<ShopCartItemVo>  getCartItemVos(@ModelAttribute CartNextForm form,User user){
    	  List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
  	  if(form.getQty()>0){
        	// 直接下单,不走购物车流程
  	    Set<String> skuIds = new LinkedHashSet<String>(form.getSkuId());
        	cartItems = cartService.checkout(skuIds,form.getQty(),user);
        }
        else if (form.getSkuId() != null && form.getSkuId().size() > 0) {
            // 以sku结算
            Set<String> skuIds = new LinkedHashSet<String>(form.getSkuId());
            cartItems = cartService.checkout(skuIds,user);           
        } else if (StringUtils.isNotEmpty(form.getShopId())) {
            // 以店铺结算
            cartItems = cartService.checkout(form.getShopId(),user);
        }
        if (CollectionUtils.isEmpty(cartItems)) {
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "请选择要结算的商品");
        }
        
        // 购物车商品按照店铺分组，注意shop做key，需要实现shop的equals方法
          List<ShopCartItemVo> shopCartItems=Lists.newArrayList();
       
        Map<Shop, List<CartItemVO>> cartItemMap = new HashMap<Shop, List<CartItemVO>>();
        for (CartItemVO item : cartItems) {
            List<CartItemVO> list = cartItemMap.get(item.getShop());
            if (list == null) {
                list = new ArrayList<CartItemVO>();
                cartItemMap.put(item.getShop(), list);
            }
            list.add(item);
        }
        
        for( Shop key: cartItemMap.keySet()){
        	ShopVO shopVO = new ShopVO(key);
        	User u = userServcie.load(shopVO.getOwnerId());
        	shopVO.setPhone(StringUtils.defaultIfBlank(u.getPhone(), u.getLoginname()));
      	   ShopCartItemVo shopCartItem = new ShopCartItemVo(shopVO, cartItemMap.get(key));
      	   shopCartItems.add(shopCartItem);
        }
        for(ShopCartItemVo itemVO: shopCartItems){
         List<CartItemVO> cartItemsVO=itemVO.getCartItems();
        	for(CartItemVO cartItem:cartItemsVO)
        		cartItem.setShop(null);
        }
      	return shopCartItems;
      }  

    
    @ResponseBody
    @RequestMapping(value = "/cart/loadAddress")
    public ResponseObject<List<AddressVO>> getAddressList(@RequestParam String extUid, @RequestHeader("Domain") String domain) {
        User user=loadExtUser(domain, extUid);
    	List<AddressVO>  addresses=addressService.listUserAddressesVo2(user.getId());
        return new ResponseObject<List<AddressVO>>(addresses);
    }
    
     public PricingResultVO  calculatePrice(List<OrderSkuVO> skuList,User user, String zoneId, String... couponIds ){
    	 Map<String,Integer> skuMap=Maps.newHashMap();
         List<CartItem> itemList=cartService.listByUserId(user.getId());
         for(OrderSkuVO vo:skuList){
         	String cartItemId=vo.getCartItemId();
         	if(StringUtils.isNoneBlank(cartItemId)){
         		boolean needUpdate=false;
         		for(CartItem item:itemList){
         			if(item.getId().equals(cartItemId)&&item.getAmount()!=vo.getQty()){
         				needUpdate=true;
         				break;
         			}
         				
         		}
         		if(needUpdate)
         			cartService.updateAmount(cartItemId,vo.getQty(),user); 	
         	}
            skuMap.put(vo.getSkuId(), vo.getQty());
           
        }
        
         PricingResultVO priceResultVo=pricingService.calculate(skuMap, zoneId, couponIds);
    	return  priceResultVo;
     }
    
     
     /**
      * 计算优惠，邮资，商品数量变动后的商品总价格
      * @param skuMap
      * @param zoneId
      * @param couponId 为shop级别的，先不支持
      * @return
      */

     @ResponseBody
     @RequestMapping(value="/cart/calculateFee")
     public  ResponseObject<OrderPricingVO> calculateTotalPrice(String skuJson, @RequestParam String extUid, @RequestHeader("Domain") String domain, String zoneId, String couponsJson){
         OrderPricingVO orderPrice=new  OrderPricingVO();
    	 BigDecimal totalFee=new BigDecimal(0);
    	 BigDecimal logisticsFee=new BigDecimal(0);
    	 User user = loadExtUser(domain, extUid);
    	 List<ShopOrderVO> shopOrders=JSON.parseArray(skuJson, ShopOrderVO.class);
    	
         List<PricingResultVOEX> pricingResultVOs=Lists.newArrayList();
    	 for( ShopOrderVO shopOrderVO: shopOrders){
        	List<OrderSkuVO>  skuList=shopOrderVO.getSkus();
        	String shopId=shopOrderVO.getShopId();
        	if(StringUtils.isBlank(shopId)){
        		Sku sku=productService.loadSku(skuList.get(0).getSkuId());
            	Product product=productService.findProductById(sku.getProductId());
            	shopId = product.getShopId();
        	}
        	PricingResultVO shopOrderPrice=calculatePrice(skuList,user,zoneId);
        	PricingResultVOEX pricingResultVO=new PricingResultVOEX(shopOrderPrice);
        	pricingResultVO.setShopId(shopId);
        	totalFee =totalFee.add(shopOrderPrice.getTotalFee());
        	logisticsFee=logisticsFee.add(shopOrderPrice.getLogisticsFee());
        	pricingResultVOs.add(pricingResultVO);
         }
    	 orderPrice.setPricingList(pricingResultVOs);
         BigDecimal discountFee = BigDecimal.ZERO;
        
        if(StringUtils.isNotBlank(couponsJson)){
        	List<CouponForm> coupons = JSON.parseArray(couponsJson, CouponForm.class);
        	Coupon coupon = null;
        	for(CouponForm c : coupons){
        		
        		if(StringUtils.isNotBlank(c.getId())){
	        		coupon = couponService.load(c.getId());
        		}else if(StringUtils.isNotBlank(c.getExtCouponId())){
        			coupon = couponService.loadExtCouponByExtId(c.getExtCouponId());
        		}
        		
        		if (coupon != null && CouponStatus.VALID == coupon.getStatus()) {
        			discountFee = discountFee.add(coupon.getDiscount());
        		}
        	}
        }
        
        orderPrice.setDiscountFee(discountFee);
        totalFee=totalFee.subtract(discountFee);
        if(totalFee.compareTo(new BigDecimal(0))<0)
        	totalFee=new BigDecimal(0);
        orderPrice.setTotalFee(totalFee);
        orderPrice.setLogisticsFee(logisticsFee);
        return new ResponseObject<OrderPricingVO>(orderPrice) ;
     }
    
    @ApiOperation("native形式获取银行卡列表，路径：openapi/bank/list")
    @ResponseBody
    @RequestMapping(value="/bank/list")
   public  ResponseObject<Map<String,Object>>  getBankList(){
    	Map<String,Object>  bankMap=new HashMap<String,Object>();
       
    	List<PayBankWay> hotBanksCreditCard = payBankService.queryHotPayBanksCreditCard();
        List<PayBankWay> allBanksCreditCard = payBankService.queryAllPayBanksCreditCard();
        List<BankItemsMap> creditCardBanks = trans4Show(hotBanksCreditCard, allBanksCreditCard);

        List<PayBankWay> hotBanksDebitCard = payBankService.queryHotPayBanksDebitCard();
        List<PayBankWay> allBanksDebitCard = payBankService.queryAllPayBanksDebitCard();
        List<BankItemsMap> debitCardBanks = trans4Show(hotBanksDebitCard, allBanksDebitCard);

        bankMap.put("creditCardBanks",creditCardBanks);
        bankMap.put("debitCardBanks",debitCardBanks);
        return new ResponseObject<Map<String,Object>>(bankMap);
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

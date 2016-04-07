package com.vdlm.web.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.dal.model.Address;
import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderAddress;
import com.vdlm.dal.model.OrderUserDevice;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.CouponStatus;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.error.XqProductBuyException;
import com.vdlm.service.order.OrderAddressService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.order.OrderUserDeviceService;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.pricing.vo.OrderPricingVO;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.service.pricing.vo.PricingResultVOEX;
import com.vdlm.service.product.ProductSP;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.utils.DeviceUA;
import com.vdlm.utils.DeviceUAUtils;
import com.vdlm.utils.MD5Util;
import com.vdlm.utils.UniqueNoUtils;
import com.vdlm.utils.UniqueNoUtils.UniqueNoType;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.order.form.OrderSkuVO;
import com.vdlm.web.order.form.UpdateOrderForm;
import com.vdlm.web.order.form.XqOrderSumbitForm;

@Controller
public abstract class XqOrderController extends BaseController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CashierService cashierService;

    @Autowired
    private OrderAddressService orderAddressService;

    @Autowired
    private OutPayAgreementService outPayAgreementService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private PricingService pricingService;

    @Autowired
    private OrderUserDeviceService orderUserDeviceService;

    @Autowired
    private RestOperations restTemplate;
    
	@Value("${xiangqu.web.site}")
	private String xiangquWebSite;

	@Value("${site.web.host.name}")
	private String kkkdWebSite;
	
	@Value("${xiangqu.unionId}")
	private String xqUnionId;
	
	private String confirmUnionId(String unionId) {
        // 想去单一接口下单，分账id代码里暂时固定写死
		User xqUser = userService.loadByLoginname("xiangqu");
		if (xqUser != null) 
            return  xqUser.getId();
		else 
			return xqUnionId;
	}
     
	private int doRushPurchase(XqOrderSumbitForm form, User user) {
        int prodSPSum = 0;  //本次特价商品购买数量 总和
        
        for (String skuId : form.getSkuIds()) {
        	String productId = null;
        	CartItem cartItem = null;
        	if(form.getQty()>0){
        		Sku sku = productService.loadSku(skuId);
        	    productId = sku.getProductId(); 
        	} else {
          	    cartItem = cartService.loadBySku(skuId);
                if(cartItem.getAmount() <= 0)
                    continue;
                productId = cartItem.getProductId();         		
        	} 
            
            ProductSP productSP = activityService.checkProductSP(productId, user);
            if(productSP.getFirstActivity() ==null) continue;
            
            if (form.getQty()>0) prodSPSum += form.getQty();
            else prodSPSum += cartItem.getAmount();
            
            if( !productSP.onSale() ){
                throw new XqProductBuyException(GlobalErrorCode.INVALID_ARGUMENT, "哎呀，您来早啦，活动尚未开始");
            } else if( productSP.overLimit(prodSPSum) ){    
            	String errMsg = "";
            	if (cartItem == null) {
	            	errMsg = "嘿，一人只能购[" + 1 + "]份限购商品，要把机会留给更多的朋友哦～";
	            	if (productSP.getFirstActivity() != null)
	            		errMsg = "嘿，一人只能购[" + productSP.getFirstActivity().getMaxQty() + "]份限购商品，要把机会留给更多的朋友哦～";
            	} else {
	            	errMsg = "嘿，一人只能购[" + 1 + "]份限购商品，您已抢到该商品订单直接去付款吧～";
	            	if (productSP.getFirstActivity() != null)
	            		errMsg = "嘿，一人只能购[" + productSP.getFirstActivity().getMaxQty() + "], 您已抢到该商品订单直接去付款吧～";
            	}
                throw new XqProductBuyException(GlobalErrorCode.INVALID_ARGUMENT, errMsg);
            } else if( user == null || user.getLoginname().startsWith("CID") )  {
                throw new XqProductBuyException(GlobalErrorCode.INVALID_ARGUMENT, "商品用户登录后才能购买");
            } 
        }
        
        return prodSPSum;
	}
	
	private String newUserRush(Device device, User user, String referer) {
        if(device.isNormal()) { // pc端设备
            String backURL = xiangquWebSite + "/user/orders";
            return "redirect:" + xiangquWebSite + "/user/pc/bindPhone.html?callbackUrl=" + backURL;
        } else { // 移动端设备
            long t = System.currentTimeMillis();
            String signKey = "zlhzxqxl0604*@";
            signKey = MD5Util.MD5Encode(signKey, "UTF-8").toUpperCase();
            signKey = MD5Util.MD5Encode(signKey + t, "UTF-8").toUpperCase();
            String extUserId = user == null ? "" : user.getExtUserId();
            String backURL = referer == null ? kkkdWebSite + "/xiangqu/wap/cart" : referer;

            String szParam = "userId=" + extUserId + "&"
                    + "t=" + t  + "&"
                    + "key=" + signKey  + "&"
                    + "callbackUrl=" + backURL;
            return "redirect:" + xiangquWebSite + "/user/wap/bindPhone.html?" + szParam;
        }
	}

    public String submit(@ModelAttribute XqOrderSumbitForm form, 
    		@CookieValue(value = "union_id", defaultValue = "") String unionId,
    		@RequestParam(value = "tuid", defaultValue = "") String tuId,
            Errors errors,  Device device, Model model, HttpServletRequest request, 
            @RequestHeader(value="referer", required=false) String referer) {
    	
    	Map<String,String> m = this.doSubmit(form, unionId, tuId, errors, device, model, request, referer);
    	String returnUrl = "redirect:/cashier/"+m.get("pNo")+"?bizType=order&batchBizNos=" + m.get("bizNos");
    	return returnUrl;
    }
    
    /**
     * 临时处理一下
     * @author Tier <QQ:871898560>
     * @dateTime 2016年1月11日,下午5:06:14
     * @param form
     * @param unionId
     * @param tuId
     * @param errors
     * @param device
     * @param model
     * @param request
     * @param referer
     * @return
     */
    protected Map<String,String> doSubmit(@ModelAttribute XqOrderSumbitForm form, 
    		@CookieValue(value = "union_id", defaultValue = "") String unionId,
    		@RequestParam(value = "tuid", defaultValue = "") String tuId,
            Errors errors,  Device device, Model model, HttpServletRequest request, 
            @RequestHeader(value="referer", required=false) String referer) {

        User user = getCurrentUser();

		log.info("vdlm-web:vdlm/web/order/XqOrderController /order/submit:" + this.getClass().toString());
        
		// 1.need rushPurchase (特价商品抢购)
        int prodSPSum =0;  //本次特价商品购买数量 总和
		if (activityService.needRushPurchase()) {
			if ( (prodSPSum = doRushPurchase(form, user)) > 0 ) { // 下单的商品中包含有特殊商品
//				noBindPhone = isXqUserNotBindMobile(user.getExtUserId());
//				if (noBindPhone) {
//		            String url = newUserRush(device, user, referer);
//		            if (url != null) return url; // 想去用户没有绑定
//				}
			}
		} // end rushPurchase
		
        ControllerHelper.checkException(errors);
        OrderAddress oa = new OrderAddress();
        Address address = addressService.load(form.getAddressId());
        BeanUtils.copyProperties(address, oa);

		// 2. submitby... getorders
        Map<String, Integer> map = new HashMap<String, Integer>();
    	if(form.getQty()>0) {
        	map.put(form.getSkuIds().get(0), form.getQty());
	     } else {
	        List<CartItemVO> cartItems = cartService.checkout(new HashSet<String>(form.getSkuIds()));
	        for (CartItemVO item : cartItems)
	            map.put(item.getSkuId(), item.getAmount());
        }
    	
    	// TODO: 这里需要优化, 写死了
        PricingResultVO prices = pricingService.calculate(map, oa.getZoneId(), null);
        if (prices.getGoodsFee().doubleValue() <= 25) {
            List<Coupon> coupons = form.getCoupon();
            if (form.getCoupon() != null) {
                for (Coupon coupon : coupons) {
                    if("XQ.HONGBAO".equals(coupon.getActivityId())){
                        throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "该红包只能在订单金额大于25元时使用");
                    }
                }
            }
        }

        List<Order> orders= new ArrayList<Order>();      
        if(form.getQty()>0) { // 一个型号
        	List<String> list = new ArrayList<String>(form.getRemarks().values());
        	orders.add(orderService.submitBySkuId(form.getSkuIds().get(0), oa, list.get(0), confirmUnionId(unionId), tuId, true, form.getQty()));
        } else{ // 多个型号
        	orders = orderService.submitBySkuIds(form.getSkuIds(), oa, form.getRemarks(), confirmUnionId(unionId), tuId, true);
        }   
        
        // 3. generage PayNo and calc totalFee
        String pNo = UniqueNoUtils.next(UniqueNoType.P);
        List<String> orderNos = new ArrayList<String>();
        BigDecimal totalFee = BigDecimal.ZERO;
        String productId = null, productName = null;
        UserPartnerType partnerType = null;
        String userId = null;
        for (Order order : orders) {
            orderNos.add(order.getOrderNo());
            totalFee = totalFee.add(order.getTotalFee());
            if(productId == null){
            	OrderVO vo = orderService.loadByOrderNo(order.getOrderNo());
            	productId = vo.getOrderItems().get(0).getProductId();
            	productName = vo.getOrderItems().get(0).getProductName();
            	partnerType = vo.getPartnerType();
            	userId = vo.getBuyerId();
            }

            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setPayType(form.getPayType());
            if(orders.size() == 1)
            	updateOrder.setPayNo(pNo);
            orderService.update(updateOrder);
            
			DeviceUA ua = DeviceUAUtils.buildOrderUserDevice(request);
			OrderUserDevice orderUserDevice = new OrderUserDevice();
			BeanUtils.copyProperties(ua, orderUserDevice);
			orderUserDevice.setOrderId(order.getId());
			orderUserDevice.setUserId(user.getId());
			orderUserDeviceService.save(orderUserDevice);
        }
        
        // 4. 记录相关信息到收银台
        String bizNos = StringUtils.join(orderNos, ",");
		List<CashierItem> items = null;
        if(form.getCoupon() != null && form.getCoupon().size() > 0) {
        	List<Coupon> cs = this.couponService.validateCoupons(form.getCoupon(),totalFee,userId);
        	items = couponService.calcCashier(cs, pNo, totalFee,
        			userId, productId, productName, partnerType.toString(), bizNos);
        	for(CashierItem item : items){
        		totalFee = totalFee.subtract(item.getAmount());
        	}
        }
        if(items == null) items = new ArrayList<CashierItem>();
        
        //加入支付方式的收银台
        if(totalFee.compareTo(BigDecimal.ZERO) > 0) {
	        items.addAll( createCashierChannel(form.getPayAgreementId(), pNo, form.getPayType(),
	        		totalFee, form.getCardType(), form.getBankCode(),
	        		userId, productId, productName, partnerType.toString(), bizNos) );
        }
        cashierService.save(items, pNo, null);

        Map<String,String> m = new HashMap<>();
        m.put("pNo", pNo);
        m.put("bizNos", bizNos);
        return m;
    }
    
    private boolean isXqUserNotBindMobile(String xqUserId) {
        String xqBindURL = xiangquWebSite + "/user/check/bindPhone?userId=" + xqUserId;
        String res = restTemplate.getForObject(xqBindURL, String.class, new Object[]{});
        JSONObject json = JSONObject.parseObject(res);
        return json.getIntValue("code") == 4001;
    }

    private List<CashierItem> createCashierChannel(String payAgreementId, String pNo, PaymentMode mode,
    		BigDecimal totalFee, PaymentChannel paymentChannel, String bankCode, String userId, String productId,
    		String productName, String partner, String bizNos){
		List<CashierItem> items = new ArrayList<CashierItem>();

		if(totalFee.compareTo(BigDecimal.ZERO)==1){
			//记录支付
			CashierItem item = new CashierItem();
			item.setBizNo(pNo);
			item.setBankCode(bankCode);
			item.setAmount(totalFee);
			item.setUserId(userId);
			item.setProductId(productId);
			item.setProductName(productName);
			item.setPartner(partner);
			item.setBatchBizNos(bizNos);
			item.setAgreementId(StringUtils.isBlank(payAgreementId)?null:payAgreementId);
			item.setPaymentMode(mode);
			item.setStatus(PaymentStatus.PENDING);
			item.setBankName(paymentChannel==null?"":paymentChannel.toString());
			
//			if(paymentChannel != null) {
//                item.setPaymentChannel(paymentChannel);
//            } else {
                item.setPaymentChannel(PaymentChannel.PLATFORM);
//            }
			items.add(item);
		}

        return items;
	}

    public String orderPay(@PathVariable("id")String orderId, Model model, HttpServletRequest request) {
        OrderVO order = orderService.loadVO(orderId);
        if (order == null) {
            return "order/404";
        }
        model.addAttribute("order", order);

        User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
        	log.error("访问订单详情出错",e);
        }

        model.addAttribute("authed", user != null && order.getBuyerId().equals(user.getId()));

        if(order.getStatus() == OrderStatus.SUBMITTED){
            model.addAttribute("userAgreeBanks", outPayAgreementService.listBankByUserId(getCurrentUser().getId()));
            UserAgent ua = UserAgentUtils.getUserAgent(request);
            Map<String, List<CouponVO>> couponMap = couponService.listValidsWithExt(order.getTotalFee(), null, ua.getChannel());
	   		model.addAttribute("coupons", couponService.dealCoupons(couponMap));
        }

        model.addAttribute("address", orderAddressService.selectByOrderId(orderId));
        model.addAttribute("shop", shopService.load(order.getShopId()));
        model.addAttribute("xiangquWebSite", xiangquWebSite);

        return "pc/order-pay";
    }
    
    public ResponseObject<OrderPricingVO> calculateTotalPrice(String skuJson, String addressId, String couponJson) {
    	User user =  getCurrentUser();
    	OrderPricingVO orderPrice = new OrderPricingVO();
    	BigDecimal totalFee = new BigDecimal(0);
    	BigDecimal discountFee = new BigDecimal(0);
    	BigDecimal logisticsFee = new BigDecimal(0);
    	List<UpdateOrderForm> forms = JSON.parseArray(skuJson, UpdateOrderForm.class);
    	
    	List<PricingResultVOEX> pricingList = new ArrayList<PricingResultVOEX>();
    	for (UpdateOrderForm form : forms) {
    		String shopId = form.getShopId();
    		if (form.getOrderSkuVO() == null || form.getOrderSkuVO().size() == 0) {
    			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "参数有误");
    		} 
    		if (StringUtils.isBlank(shopId)) {
    			Sku sku = productService.loadSku(form.getOrderSkuVO().get(0).getSkuId());
    			Product product = productService.findProductById(sku.getProductId());
    			shopId = product.getShopId();
    		}
    		PricingResultVO vo = calculatePrice(form.getOrderSkuVO(), user, addressId);
    		if (vo == null) {
    			throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "参数有误");
    		}
        	PricingResultVOEX pricingResultVO = new PricingResultVOEX(vo);
        	pricingResultVO.setShopId(shopId);
        	totalFee = totalFee.add(vo.getTotalFee());
        	logisticsFee = logisticsFee.add(vo.getLogisticsFee());
        	pricingList.add(pricingResultVO);
    	}
    	orderPrice.setPricingList(pricingList);
    	
    	//TODO 红包绑定还未进行绑定
        if(StringUtils.isNotBlank(couponJson)){
        	List<Coupon> coupons = JSON.parseArray(couponJson, Coupon.class);
        	Coupon coupon = null;
        	for(Coupon c : coupons){
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
		totalFee = totalFee.subtract(discountFee);
		if (totalFee.compareTo(new BigDecimal(0)) < 0)
			totalFee = new BigDecimal(0);
        orderPrice.setTotalFee(totalFee);
        orderPrice.setLogisticsFee(logisticsFee);
        return new ResponseObject<OrderPricingVO>(orderPrice) ;
    }
    
    private PricingResultVO calculatePrice(List<OrderSkuVO> skuList, User user, String addressId, String... couponIds ){
    	PricingResultVO priceResultVo = null;
    	try {
        	Map<String,Integer> skuMap = new HashMap<String, Integer>();
            List<CartItem> itemList = cartService.listByUserId(user.getId());
            for(OrderSkuVO vo : skuList){
            	String cartItemId = vo.getCartItemId();
            	if(StringUtils.isNoneBlank(cartItemId)){
            		boolean needUpdate=false;
            		for(CartItem item:itemList){
            			if(item.getId().equalsIgnoreCase(cartItemId) && item.getAmount() != vo.getQty()){
            				needUpdate=true;
            				break;
            			}
            		}
            		if(needUpdate)
            			cartService.updateAmount(cartItemId, vo.getQty(), user); 	
            	}
               skuMap.put(vo.getSkuId(), vo.getQty());
           }
            priceResultVo = pricingService.calculate(skuMap, addressId, couponIds);
    	} catch(Exception e) {
    		throw e;
    	}
        return priceResultVo;
    }

}

package com.vdlm.web.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.dal.model.Address;
import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderAddress;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.OrderUserDevice;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserSigninLog;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.type.OrderActionType;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.interceptor.Token;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.error.XqProductBuyException;
import com.vdlm.service.order.OrderAddressService;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.order.OrderUserDeviceService;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.pay.PaymentService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.product.ProductSP;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.userAgent.UserSigninLogService;
import com.vdlm.service.userAgent.impl.UserSigninLogFactory;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.utils.DeviceUA;
import com.vdlm.utils.DeviceUAUtils;
import com.vdlm.utils.UniqueNoUtils;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.utils.UniqueNoUtils.UniqueNoType;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;
import com.vdlm.web.order.form.OrderPayAgainForm;
import com.vdlm.web.order.form.OrderSumbitForm;
/**
 * 订单相关逻辑
 * 
 * @author odin
 * @author ahlon
 */
@Controller
public class OrderController extends BaseController {

	@Autowired
	private UserSigninLogService userSigninLogService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private CartService cartService;
	
    @Autowired
    private ActivityService activityService;
	
	@Autowired
	private ShopService shopService;

	@Autowired
	private OrderAddressService orderAddressService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private PricingService pricingService;
	
	@Autowired
	private RememberMeServices rememberMeServices;
	
    @Autowired
    private OutPayAgreementService outPayAgreementService;
    
    @Autowired
    private CashierService cashierService;

    @Autowired
    private OrderUserDeviceService orderUserDeviceService;
    
    @Autowired
    private RestOperations restTemplate;
    
    @Autowired
    private OrderRefundService orderRefundService;
    
	@Value("${site.web.host.name}")
	private String hostName;
	
	@Value("${xiangqu.web.site}")
	private String xiangquWebSite;
	
	@Value("${xiangqu.unionId}")
	private String xqUnionId;
	
	 @Value("${order.delaysign.cnt}")
	 private int delaySignCnt;
	
	/**
	 * 订单列表页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/orders")
	public String index(Model model, Pageable pager, @RequestParam(value = "orderStatus", required = false)String orderStatus) {
		List<OrderVO> orders = new ArrayList<OrderVO>();
		if (orderStatus == null) {
			 orders = orderService.listByStatus4Buyer(OrderStatus.SUBMITTED, pager);
		} else {
			switch(orderStatus) {
				case "SUBMITTED":
					orders = orderService.listByStatus4Buyer(OrderStatus.SUBMITTED, pager);
					break;
				case "CANCELLED":
					orders = orderService.listByStatus4Buyer(OrderStatus.CANCELLED, pager);
					break;
				case "PAID":
					orders = orderService.listByStatus4Buyer(OrderStatus.PAID, pager);
					break;
				case "SHIPPED":
					orders = orderService.listByStatus4Buyer(OrderStatus.SHIPPED, pager);
					break;
				case "SUCCESS":
					orders = orderService.listByStatus4Buyer(OrderStatus.SUCCESS, pager);
					break;
				case "REFUNDING":
					orders = orderService.listByStatus4Buyer(OrderStatus.REFUNDING, pager);
					break;
				case "CLOSED":
					orders = orderService.listByStatus4Buyer(OrderStatus.CLOSED, pager);
					break;
				default:
					throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请求参数有误");
		}
		}
		model.addAttribute("orders", orders);
		return "order/index";
	}

	/**
	 * 订单详情页面
	 * @param orderId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/order/{id}", "/order/{id}/pay"})
	public String view(@PathVariable("id") String orderId, HttpServletRequest request, HttpServletResponse response, Model model){
		
		String redirect = redirectDomain(request, response);
        if (StringUtils.isNotEmpty(redirect))	return redirect;
		
		OrderVO order = orderService.loadVO(orderId);
		if (order == null) return "order/404";
		
		if (OrderStatus.SHIPPED.equals(order.getStatus()) && order.getShippedAt() != null) {
			Calendar day = Calendar.getInstance();
			if (order.getLatestSignAt() == null) {
				day.setTime(order.getShippedAt());
				day.add(Calendar.DATE, order.getDefDelayDate());
				order.setLatestSignAt(day.getTime());
			}
			day.setTime(order.getShippedAt());
			day.add(Calendar.DATE, order.getDefDelayDate() * (delaySignCnt + 1) - 1);
			if (order.getLatestSignAt().after(day.getTime())) {
				order.setNextLatestSignAt(order.getLatestSignAt());
			} else {
				day.setTime(order.getLatestSignAt());
				day.add(Calendar.DATE, order.getDefDelayDate());
				order.setNextLatestSignAt(day.getTime());
			}
		}
		
		if (order.getDiscountFee().compareTo(BigDecimal.ZERO) == 1) {
			List<CashierItem> items = couponService.reCalcCouponByOrder(order, null, null); 
			if (items != null && items.size() > 0 && items.get(0).getBatchBizNos().split(",").length > 1) {
				order.setDiscountFee(BigDecimal.ZERO);
				order.setTotalFee(orderService.load(orderId).getTotalFee());
			}
		}
		
		if (order.getStatus().equals(OrderStatus.CLOSED)) {
			List<OrderRefund> array = orderRefundService.listByOrderId(order.getId());
			if (array != null && array.size() > 0) {
				order.setRefundCreatedAt(array.get(0).getCreatedAt());
				order.setRefundConfirmAt(array.get(0).getConfirmTime());
			}
		}
		Shop shop = shopService.load(order.getShopId());
		order.setShopName(shop.getName());
		model.addAttribute("order", order);
		
		User user = null;
		try {
			user = getCurrentUser();
		} catch (Exception e) {
			log.error("访问订单详情出错",e);
		}
		
		model.addAttribute("authed", user != null && order.getBuyerId().equals(user.getId()));
        // old im... will be remove in the future. now keep it tmp for web front
		model.addAttribute("imUser", false);
		
		if(order.getStatus() == OrderStatus.SUBMITTED) { // 
			UserAgent ua = UserAgentUtils.getUserAgent(request);
			    Map<String, List<CouponVO>> couponsMap = couponService.listValidsWithExt(order.getTotalFee(), "", ua.getChannel());
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
			//放入协议支付的银行
			model.addAttribute("userAgreeBanks", outPayAgreementService.listBankByUserId(getCurrentUser().getId()));
		}
		
		// 判断是否想去的订单
		User buyer = userService.load(order.getBuyerId());
		// buyer 也有返回为空的情况，这个对应的archive是1，返回的
		if ((buyer != null && buyer.getPartner() != null && "xiangqu".equalsIgnoreCase(buyer.getPartner())) 
		        || UserPartnerType.XIANGQU.equals(order.getPartnerType())){
		    model.addAttribute("xiangquWebSite", xiangquWebSite);
		    String xqBindURL = xiangquWebSite + "/user/getMixPhone?userId={userId}";
			String mixPhone = restTemplate.getForObject(xqBindURL, String.class, new Object[] { buyer.getExtUserId()});
		    JSONObject json=JSONObject.parseObject(mixPhone);
		    Object phoneStr=ObjectUtils.defaultIfNull(json.get("data"), "");
		    if(StringUtils.isNotEmpty(phoneStr.toString()))
		    {
		    	model.addAttribute("havePhone", true);
				model.addAttribute("mixPhone", phoneStr.toString());
				model.addAttribute("key", json.get("msg"));
		    }
		    else
		    {
		    	model.addAttribute("havePhone", false);
		    }
		    
			return "xiangqu/orderView";
		} else {
			return "order/view";
		}
	}
	
	@RequestMapping(value = "/order/{id}/verify")
	public String verify(@PathVariable("id") String orderId, String postfix, Model model,
			HttpServletRequest req, HttpServletResponse resp) {
		OrderVO order = orderService.loadVO(orderId);
		if (postfix == null) {
			model.addAttribute("order", order);
			return "order/orderVerify";
		} else if ("".equals(postfix)) {
			model.addAttribute("order", order);
			return "order/orderVerify";
		} else {
			boolean fit = order.getOrderAddress().getPhone().substring(7).equals(postfix);
			if (fit) {
				User buyer = userService.load(order.getBuyerId());
			        
		        Authentication auth = new UsernamePasswordAuthenticationToken(buyer,
						null, buyer.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				rememberMeServices.loginSuccess(req, resp, auth);
				
				//记录用户登录环境Log
	            UserSigninLog log = UserSigninLogFactory.createUserSigninLog(req, buyer);
	            userSigninLogService.insert(log);
				
				req.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
				
			    return "redirect:/order/" + order.getId();
			} else {
				model.addAttribute("order", order);
				model.addAttribute("error", "手机后四位号码不正确");
				return "order/orderVerify";
			}
		}
	}
	
	private String confirmUnionId(String unionId) {
		User xqUser = userService.loadByLoginname("xiangqu");
		if (xqUser != null) 
            return  xqUser.getId();
		else 
			return xqUnionId;
	}
	
	private int doRushPurchase(OrderSumbitForm form, User user) {
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
	
    private boolean isXqUserNotBindMobile(String xqUserId) {
        String xqBindURL = xiangquWebSite + "/user/check/bindPhone?userId=" + xqUserId;
        String res = restTemplate.getForObject(xqBindURL, String.class, new Object[]{});
        JSONObject json = JSONObject.parseObject(res);
        return json.getIntValue("code") == 4001;
    }
    
	private String newUserRush(Device device, String referer) {
            String backURL = "/cart";
        	if (!StringUtils.isEmpty(referer))  backURL = referer;
            if(device.isNormal()) { // pc return
                return "redirect:" + xiangquWebSite + "/user/pc/bindPhone.html?callbackUrl=" + backURL;
            }
            return null;
	}
    
	/**
	 * 订单提交
	 * @param form
	 * @param errors
	 * @param redirectAttrs
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/order/submit")
	@Token(remove = true)
	public String submit(@ModelAttribute OrderSumbitForm form, 
			@CookieValue(value = "union_id", defaultValue = "") String unionId,
			@CookieValue(value = "tu_id", defaultValue = "") String tuId,
			Errors errors,  Device device, Model model, HttpServletRequest request,
			 @RequestHeader(value="referer", required=false) String referer) {
		ControllerHelper.checkException(errors);
		OrderVO orderVO = null;
		unionId = confirmUnionId(unionId); 
		User user = getCurrentUser();
		
		log.info("vdlm-web:vdlm/web/order/OrderController /order/submit:" + this.getClass().toString());
		
		// 1.need rushPurchase (特价商品抢购)
		if (activityService.needRushPurchase()) {
			if ( doRushPurchase(form, user) > 0 ) { // 下单的商品中包含有特殊商品
//		        if(isXqUserNotBindMobile(user.getExtUserId())) {
//		        	String url = newUserRush(device, referer);
//		        	if (url != null) return url;  // 想去用户没有绑定
//		        }
			}
		}
		
		// 2. submitby... get orders
		if (StringUtils.isNotBlank(form.getOrderId())) { // 多次提交
			log.info("when come here? weak network or what? vdlm-web ordercontroller");
			orderVO = orderService.loadVO(form.getOrderId());
		} else {
			// 目前前面都会传入addressId，可考虑简化这段代码
			OrderAddress oa = new OrderAddress();
			if (StringUtils.isNotEmpty(form.getAddressId())) {
			    Address address = addressService.load(form.getAddressId());
			    BeanUtils.copyProperties(address, oa);
			} else {
				form.setStreet(form.getStreet().trim());
			    BeanUtils.copyProperties(form, oa);
			}
			
			Order order = null;
			if(form.getQty()>0) { // 一个型号
				order = orderService.submitBySkuId(form.getSkuId(), oa, form.getRemark(), unionId, tuId, form.isDanbao(), form.getQty());
			} else if (!CollectionUtils.isEmpty(form.getSkuIds())) { // 多个型号
				order = orderService.submitBySkuIds(form.getSkuIds(), oa, form.getRemark(), unionId, tuId, form.isDanbao());
			} else if (!StringUtils.isEmpty(form.getShopId())) { //购物车提交订单
				order = orderService.submitByShop(form.getShopId(), oa, form.getRemark(), unionId, tuId, form.isDanbao());
			} else {
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单提交参数不正确");
			}
			orderVO = orderService.loadVO(order.getId());
		}
		
		// 3. record and then jump to order/{id}/pay
		DeviceUA ua = DeviceUAUtils.buildOrderUserDevice(request);
		OrderUserDevice orderUserDevice = new OrderUserDevice();
		BeanUtils.copyProperties(ua, orderUserDevice);
		orderUserDevice.setOrderId(orderVO.getId());
		orderUserDevice.setUserId(user.getId());
		orderUserDeviceService.save(orderUserDevice);
		
		return "redirect:/order/" + orderVO.getId() + "/pay";
	}
	
	/**
	 * 买家取消订单
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/order/{id}/cancel")
	public String cancel(@PathVariable("id") String orderId, Model model) {
		orderService.cancel(orderId);
		return "redirect:/order/" + orderId;
	}
	
	/**
	 * 订单支付请求
	 * @param orderId
	 * @param model
	 * @param redirectAttrs
	 * @return
	 */
	// @RequestMapping(value = "/order/{id}/pay")
	public String pay(@PathVariable("id") String orderId, Model model, RedirectAttributes redirectAttrs, 
	        @RequestHeader(value="referer", required=false) String referer, Device device){
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
            //放入协议支付的银行
            model.addAttribute("userAgreeBanks", outPayAgreementService.listBankByUserId(getCurrentUser().getId()));
        }
        
        model.addAttribute("shop", shopService.load(order.getShopId()));
        
        /**
         * 判断是否想去的订单
         * @author odin
         */
        User buyer = userService.load(order.getBuyerId());
        
        // buyer 也有返回为空的情况，这个对应的archive是1，返回的
        if ((buyer != null && buyer.getPartner() != null && "xiangqu".equalsIgnoreCase(buyer.getPartner())) 
                || UserPartnerType.XIANGQU.equals(order.getPartnerType())){
            model.addAttribute("xiangquWebSite", xiangquWebSite);
            // return "xiangqu/pay";
            return "xiangqu/orderView";
        } else {
            // return "order/pay";
            return "order/view";
        }
	}
	
	/**
	 * 订单支付请求
	 * @param orderId
	 * @param model
	 * @param redirectAttrs
	 * @return
	 */
	@RequestMapping(value = "/order/pay")
	public String pay(@ModelAttribute OrderPayAgainForm form, Model model, RedirectAttributes redirectAttrs, Device device){
		OrderVO order = orderService.loadVO(form.getOrderId());
		if (order == null) {
		    throw new BizException(GlobalErrorCode.NOT_FOUND, "订单[" + form.getOrderId() + "]不存在");
		}
		
		if (order.getStatus() != OrderStatus.SUBMITTED) {
		    throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单[" + order.getOrderNo() + "]状态错误，无法提交");
		}
		if(order.getTotalFee().add(order.getDiscountFee()).compareTo(BigDecimal.ZERO) != 1){
			log.error("订单总额不能为0  orderNo=[" + order.getOrderNo() + "]");
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单总额不能为0");
		}
		
        if (order.getGoodsFee() != null && order.getGoodsFee().doubleValue() <= 25 && form.getCoupon() != null) {
            List<Coupon> coupons = form.getCoupon();
            for (Coupon coupon : coupons) {
                if("XQ.HONGBAO".equals(coupon.getActivityId())){
                    throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "该红包只能在订单金额大于25元时使用");
                }
            }
        }
		
		model.addAttribute("order", order);
		
		String pNo = null;
		if(StringUtils.isNotBlank(order.getPayNo())){
			pNo = order.getPayNo();
		}else{
			pNo = UniqueNoUtils.next(UniqueNoType.P);
		}
		
		if (order.getId() != null) {
			order.setPayType(form.getPayType());
			Order updateOrder = new Order();
			updateOrder.setId(order.getId());
			updateOrder.setPayType(form.getPayType());
			updateOrder.setPayNo(pNo);
			orderService.update(updateOrder);
		}
		
		String productId = order.getOrderItems().get(0).getProductId();
		String productName = order.getOrderItems().get(0).getProductName();
		UserPartnerType partnerType = order.getPartnerType();
		BigDecimal totalFee = order.getTotalFee();
		String userId = order.getBuyerId();
		
		List<CashierItem> items = null;
        //加入红包
        if(form.getCoupon() != null && form.getCoupon().size() > 0){
        	items = couponService.calcCashier(form.getCoupon(), pNo, totalFee, 
        			userId, productId, productName, partnerType.toString(), order.getOrderNo());
        	for(CashierItem item : items){
        		totalFee = totalFee.subtract(item.getAmount());
        	}
        }
        if(items == null){
        	items = new ArrayList<CashierItem>();
        }
        
        
        //加入支付方式的收银台
        items.addAll( createCashierChannel(form.getPayAgreementId(), pNo, form.getPayType(), 
        		totalFee, form.getCardType(), form.getBankCode(), userId, productId, productName, 
        		partnerType.toString(), order.getOrderNo()) );
        
        cashierService.save(items, pNo, null);
        
        return "redirect:/cashier/"+pNo+"?bizType=order&batchBizNos=" + order.getOrderNo();
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
			item.setAgreementId(StringUtils.isBlank(payAgreementId)?null:payAgreementId);
			item.setPaymentMode(mode);
			item.setBatchBizNos(bizNos);
			item.setStatus(PaymentStatus.PENDING);
			item.setBankName(paymentChannel==null?"":paymentChannel.toString());
//			if(paymentChannel != null)
//				item.setPaymentChannel(paymentChannel);
//			else
				item.setPaymentChannel(PaymentChannel.PLATFORM);
			items.add(item);
		}
        
        return items;
	}
	
	/*
	 * 买家订单删除请求
	 * param orderId
	 * */
	@ResponseBody
	@RequestMapping(value = "/order/delete-buyer")
	public ResponseObject<Boolean> delOrderByBuyer(@RequestParam(value = "orderId")String orderId) {
		Order order = orderService.load(orderId);
		User user = getCurrentUser();
		if (user == null)
			return new ResponseObject<>(new BizException(GlobalErrorCode.NOT_FOUND, "取消订单用户不存在"));
		if (order == null)
			return new ResponseObject<>(new BizException(GlobalErrorCode.NOT_FOUND, "取消订单不存在"));
		if (!order.getBuyerId().equals(user.getId()))
			return new ResponseObject<>(new BizException(GlobalErrorCode.INTERNAL_ERROR, "用户无权限操作"));
		return new ResponseObject<Boolean>(orderService.deleteByBuyer(orderId));
	}
	
	/*
	 * 提醒卖家发货
	 * param orderId
	 * */
	@ResponseBody
	@RequestMapping(value = "/order/remindship")
	public ResponseObject<Boolean> remindship(@RequestParam(value = "orderId")String orderId) {
		try {
			orderService.remindShip(orderId);
		} catch(Exception e) {
			return new ResponseObject<>(new BizException(GlobalErrorCode.UNKNOWN, e.getMessage()));
		}
		return new ResponseObject<Boolean>(true);
	}
	
	/*
	 * 延迟收货
	 * param orderId
	 * */
	@ResponseBody
	@RequestMapping(value = "/order/delay")
	public ResponseObject<Boolean> delayOrder(@RequestParam(value = "orderId")String orderId) {
		Order order = orderService.load(orderId);
		User user = getCurrentUser();
		if (user == null)
			return new ResponseObject<>(new BizException(GlobalErrorCode.NOT_FOUND, "延迟订单收货用户不存在"));
		if (order == null)
			return new ResponseObject<>(new BizException(GlobalErrorCode.NOT_FOUND, "延迟订单不存在"));
		if (!order.getBuyerId().equals(user.getId()))
			return new ResponseObject<>(new BizException(GlobalErrorCode.INTERNAL_ERROR, "用户无权限操作"));
		try {
			orderService.executeBySystem(orderId, OrderActionType.DELAYSIGN, null);
		} catch (Exception e) {
			return new ResponseObject<>(new BizException(GlobalErrorCode.UNKNOWN, "延迟确认收货操作失败"));
		}
		return new ResponseObject<Boolean>(true);
	}
}

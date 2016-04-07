package com.vdlm.openapi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.mobile.device.Device;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.Address;
import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Commission;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.Domain;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderAddress;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.OrderMessage;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.OrderUserDevice;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.type.CommissionType;
import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.vo.CouponInfoVO;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.vo.ShopVO;
import com.vdlm.dal.voex.OrderVOEx;
import com.vdlm.interceptor.Token;
import com.vdlm.openApiService.order.OpenApiOrderService;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.ShopCartItemVo;
import com.vdlm.restapi.order.CouponForm;
import com.vdlm.restapi.order.OrderInitForm;
import com.vdlm.restapi.order.OrderInitVO;
import com.vdlm.restapi.order.OrderMsgVO;
import com.vdlm.restapi.order.OrderSumbitForm;
import com.vdlm.restapi.order.RemarkFrom;
import com.vdlm.restapi.order.SkuForm;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.domain.DomainService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.error.XqProductBuyException;
import com.vdlm.service.order.OrderAddressService;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.order.OrderUserDeviceService;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.outpay.PaymentResponseClientVO;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.outpay.ThirdPartyPaymentFactory;
import com.vdlm.service.pay.PaymentService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.product.ProductSP;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.union.UnionService;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.utils.DeviceUA;
import com.vdlm.utils.DeviceUAUtils;
import com.vdlm.utils.UniqueNoUtils;
import com.vdlm.utils.UniqueNoUtils.UniqueNoType;

@RestController("openapi")
public class OrderController extends OpenApiController {
	
    @Autowired
    private OpenApiOrderService openApiOrderService;
    @Autowired
    private OrderAddressService orderAddressService;
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
	private ShopService shopService;
	@Autowired
	private UnionService unionService;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private AddressService addressService;
	@Autowired
    private OrderUserDeviceService orderUserDeviceService;
    
	@Autowired
    private CouponService couponService;
	
	@Autowired
	private ActivityService activityService;
	
    @Autowired
    private RestOperations restTemplate;
    
    @Autowired
    private ProductService productService;
    
	@Value("${xiangqu.web.site}")
	private String xiangquWebSite;
	
//	@Autowired
//	private ThirdPartyPayment aliPayment;
//	
//	@Autowired
//	private ThirdPartyPayment umPayment;
//	
//	@Autowired
//	private ThirdPartyPayment weiXinPayment;
	
	@Autowired
	private ThirdPartyPaymentFactory thirdPartyPaymentFactory;
	
	@Autowired
	private ZoneService zoneService;
	private  final String ORDERNOTES =  "您的交易金额大于%s，需要支付%s手续费";
	
	@Autowired
	private OrderRefundService orderRefundService;
	
    @Autowired
    private ResourceFacade ResourceFacade;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private DomainService domainService;

    @Autowired
    private OutPayAgreementService outPayAgreementService;
    
	@Value("${order.delaysign.date}")
	 private int defDelayDate;

	@Value("${tech.serviceFee.standard}")
	private String serviceFeethreshold;
	
	/**
	 * 订单总数
	 * @param domain
	 * @param extUid
	 * @param status
	 * @return
	 */
    @RequestMapping(value = "/b/order/count")
    @ResponseBody
    public ResponseObject<Long> count(@RequestHeader("Domain") String domain, @RequestParam("extUid") String extUid ,
    		@RequestParam(value = "status", required = false) OrderStatus status, 
    		@RequestParam(value = "startPaidAt", required = false) Long startPaidAt,
    		@RequestParam(value = "endPaidAt",  required = false) Long endPaidAt) {
        return new ResponseObject<Long>(openApiOrderService.countBuyerOrdersByStatus(domain, extUid, status,
        		startPaidAt != null ? new Date(startPaidAt) : null,
        		endPaidAt != null ?  new Date(endPaidAt) : null));
    }
    
	/**
	 * 支持订单总数
	 * @param domain
	 * @param extUid
	 * @param status
	 * @return
	 */
    @RequestMapping(value = "/b/order/sumFee")
    @ResponseBody
    public ResponseObject<BigDecimal> getOrderPaidFee(@RequestHeader("Domain") String domain, 
    					@RequestParam("extUid")String extUid,
    					@RequestParam(value = "status", required = false) OrderStatus status,
    					@RequestParam(value = "startPaidAt", required = false) Long startPaidAt, 
    					@RequestParam(value = "endPaidAt", required = false) Long endPaidAt) {
    	if (OrderStatus.PAID.equals(status)) {  // maybe query other fee sum
	        return new ResponseObject<BigDecimal>(openApiOrderService.sumBuyerOrdersPaidFee(domain, extUid,
	            startPaidAt != null ? new Date(startPaidAt) : null,
	                endPaidAt != null ?  new Date(endPaidAt) : null));
    	} else {
	        return new ResponseObject<BigDecimal>(new BigDecimal(0));
    	}
    }
    
    /**
	 * 提醒卖家发货
	 */
	@ResponseBody
    @RequestMapping("/b/order/remindShip")
    public ResponseObject<Boolean> remindShip(@RequestParam String  orderId) {
		orderService.remindShip(orderId);
		return new ResponseObject<Boolean>(true);
    }
	
	/**
	 * 订单提交
	 * @param form
	 * @param errors
	 * @param redirectAttrs
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/order/init")
	public  ResponseObject<OrderInitVO> init(@ModelAttribute OrderInitForm form, @CookieValue(value = "union_id", defaultValue = "") String unionId, 
			Errors errors, HttpServletRequest request, HttpServletResponse response) {
		// 数据校验
		ControllerHelper.checkException(errors);
		
		OrderInitVO orderInit = new OrderInitVO();
		if(StringUtils.isNotBlank(form.getAddressId())){
			orderInit.setAddress(addressService.load(form.getAddressId()));
		}
		
		orderInit.setShops(getCartItemVos(form, getCurrentUser()));
		
		return new ResponseObject<OrderInitVO>(orderInit);
	}
	
	private  List<ShopCartItemVo>  getCartItemVos(@ModelAttribute OrderInitForm form,User user){
  	  List<CartItemVO> cartItems = new ArrayList<CartItemVO>();
  	  Set<String> skuIds = new LinkedHashSet<String>();
  	  for(SkuForm sku : form.getSkuIds()){
  		  skuIds.add(sku.getSkuId());
  	  }
  	  
	  if("product".equals(form.getFromBuy())){ // 直接下单,不走购物车流程
		  SkuForm sku = form.getSkuIds().get(0);
		  skuIds.add(sku.getSkuId());
		  cartItems = cartService.checkout(skuIds, sku.getQty(), user);
      } else { // 以sku结算
          cartItems = cartService.checkout(skuIds, user);           
      }
	  
      if (CollectionUtils.isEmpty(cartItems)) {
          throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "请选择要结算的商品");
      }
      
      // 购物车商品按照店铺分组，注意shop做key，需要实现shop的equals方法
      List<ShopCartItemVo> shopCartItems=Lists.newArrayList();
      Map<Shop, List<CartItemVO>> cartItemMap = new HashMap<Shop, List<CartItemVO>>();
      for (CartItemVO item : cartItems) {
    	  @SuppressWarnings("unchecked")
    	  List<CartItemVO> list = (List<CartItemVO>) ObjectUtils.defaultIfNull( cartItemMap.get(item.getShop()), new ArrayList<CartItemVO>() );
    	  item.getProduct().setImgUrl(item.getProduct().getImg());
          list.add(item);
          cartItemMap.put(item.getShop(), list);
      }
      
      for( Shop key: cartItemMap.keySet()){
    	  ShopVO shopVO = new ShopVO(key);
    	  User u = userService.load(shopVO.getOwnerId());
    	  shopVO.setPhone(StringUtils.defaultIfBlank(shopVO.getMobile(), u.getLoginname()));
    	  ShopCartItemVo shopCartItem = new ShopCartItemVo(shopVO, cartItemMap.get(key));
    	  shopCartItems.add(shopCartItem);
      }
      for(ShopCartItemVo itemVO: shopCartItems){
    	  List<CartItemVO> cartItemsVO=itemVO.getCartItems();
    	  for(CartItemVO cartItem:cartItemsVO)
      		cartItem.setShop(null);    // ???
      }
    	return shopCartItems;
    }
	
	private String confirmUnionId(String unionId, String Domain) {
		if(StringUtils.isBlank(unionId)){
			Domain domain = domainService.loadByCode(Domain);
			unionId = domain.getAdminUserId();
		}
		return unionId;
	}
	
	private BigDecimal getCouponTotalFee(List<CashierItem> items) {
		BigDecimal ret = BigDecimal.ZERO;
		for (CashierItem item : items) {
			if (PaymentChannel.PARTNER.equals(item.getPaymentChannel()))
				ret = ret.add(item.getAmount());
		}
		return ret;
	}
	
	private int doRushPurchase(OrderSumbitForm form, User user) {
        int prodSPSum = 0;  //本次特价商品购买数量 总和
        
        for (SkuForm s : form.getSkuIds()) {
        	String productId = null;
        	CartItem cartItem = null;
        	if(s.getQty()>0){
        		Sku sku = productService.loadSku(s.getSkuId());
        	    productId = sku.getProductId(); 
        	} else {
          	    cartItem = cartService.loadBySku(s.getSkuId());
                if(cartItem.getAmount() <= 0)
                    continue;
                productId = cartItem.getProductId();         		
        	}
            
            ProductSP productSP = activityService.checkProductSP(productId, user);
            if(productSP.getFirstActivity() ==null) continue;
            
            if (s.getQty()>0) prodSPSum += s.getQty();
            else prodSPSum += cartItem.getAmount();
            
            if( !productSP.onSale() ){
                throw new XqProductBuyException(GlobalErrorCode.THIRDPLANT_BUZERROR, "哎呀，您来早啦，活动尚未开始");
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
                throw new XqProductBuyException(GlobalErrorCode.THIRDPLANT_BUZERROR, errMsg);
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
	public  ResponseObject<PaymentResponseClientVO> submit(@RequestHeader("Domain") String Domain, 
			@ModelAttribute OrderSumbitForm form, @CookieValue(value = "union_id", defaultValue = "") String unionId, 
			Errors errors, Device device, HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value="referer", required=false) String referer) {
		ControllerHelper.checkException(errors);

		unionId = confirmUnionId(unionId, Domain);
		User user = getCurrentUser();

		List<Order> orders = new ArrayList<Order>();
		Order order = null;
		Boolean noBindPhone = false;
		
		log.info("vdlm-rest-api:vdlm/openapi/order/OrderController /order/submit:" + this.getClass().toString());

		// 1.need rushPurchase (特价商品抢购)
		if (activityService.needRushPurchase()) {
			if (doRushPurchase(form, user) > 0) {
				//noBindPhone = isXqUserNotBindMobile(user.getExtUserId());
		        //if (noBindPhone) throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请先绑定手机号");
			}
		} // end rushPurchase
		
		// 1. submitby... get orders
		if (StringUtils.isNotBlank(form.getOrderId())) { // 多次提交
			log.info("when come here? weak network or what? openapi");
			orders.add(orderService.load(form.getOrderId()));
		} else { // 目前前面都会传入addressId，可考虑简化这段代码
			OrderAddress oa = new OrderAddress();
			if (StringUtils.isNotEmpty(form.getAddressId())) {
				Address address = addressService.load(form.getAddressId());
				BeanUtils.copyProperties(address, oa);
			}
			if(!form.getFromBuy().equals("cart")){  // 单sku提交订单
				SkuForm skuForm = form.getSkuIds().get(0);
				String remark = null;
				if(!CollectionUtils.isEmpty(form.getRemarks())){
					remark = StringUtils.defaultIfBlank(form.getRemark(), form.getRemarks().get(0).getRemark());
				}
				order = orderService.submitBySkuId(skuForm.getSkuId(), oa, remark, unionId, null, true, skuForm.getQty());
				orders.add(order);
			} else if (!CollectionUtils.isEmpty(form.getSkuIds())) {  // 多个型号
				Map<String, String> map = new HashMap<String, String>();
				if(form.getRemarks() != null){
					for(RemarkFrom f : form.getRemarks()){
						map.put(f.getShopId(), f.getRemark());
					}
				}
				List<String> skuids = new ArrayList<String>();
				for(com.vdlm.restapi.order.SkuForm f : form.getSkuIds()){
					skuids.add(f.getSkuId());
				}
				orders.addAll(orderService.submitBySkuIds(skuids, oa, map, unionId, null, true));
			} else if (!StringUtils.isEmpty(form.getShopId())) { //购物车提交订单
				order = orderService.submitByShop(form.getShopId(), oa, form.getRemark(), unionId, null, true);
			} else {
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单提交参数不正确");
			}
		}

		// 2. get PayNo and update into order
		String pNo = UniqueNoUtils.next(UniqueNoType.P);
		List<String> orderNos = new ArrayList<String>();
		BigDecimal totalFee = BigDecimal.ZERO;
		String productId = null, productName = null;
		for (Order o : orders) {
			orderNos.add(o.getOrderNo());
			totalFee = totalFee.add(o.getTotalFee());
			if(productId == null){
				OrderVO vo = orderService.loadByOrderNo(o.getOrderNo());
				productId = vo.getOrderItems().get(0).getProductId();
				productName = vo.getOrderItems().get(0).getProductName();
			}

			Order updateOrder = new Order();
			updateOrder.setId(o.getId());
			updateOrder.setPayType(form.getPayType());
			if(orders.size() == 1)
				updateOrder.setPayNo(pNo);
			orderService.update(updateOrder);
			
			DeviceUA ua = DeviceUAUtils.buildOrderUserDevice(request);
			OrderUserDevice orderUserDevice = new OrderUserDevice();
			BeanUtils.copyProperties(ua, orderUserDevice);
			orderUserDevice.setOrderId(o.getId());
			orderUserDevice.setUserId(user.getId());
			orderUserDeviceService.save(orderUserDevice);
		}
		if(totalFee.compareTo(BigDecimal.ZERO) == 0){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "商品价格须大于0");
		}

		String bizNos = StringUtils.join(orderNos, ",");
		List<CouponForm> couponForms = form.getCoupons();

		List<CashierItem> items = null;
		//加入红包
		if(couponForms != null && couponForms.size() > 0){
			List<Coupon> coupons = new ArrayList<Coupon>();
			for(CouponForm couponForm : couponForms){
//      		Coupon coupon = new Coupon(couponForm.getActivityCode(), couponForm.getCouponCode(), null, null, null, null, null, null);
//        		coupon.setExtCouponId(couponForm.getExtCouponId());
				Coupon coupon = new Coupon();
				coupon.setId(couponForm.getId());
				coupons.add(coupon);
			}
			
			try{
				coupons = this.couponService.validateCoupons(coupons, totalFee, user.getId());
			}catch(BizException e){
				return new ResponseObject<PaymentResponseClientVO>(e.getMessage(),e.getErrorCode());
			}
			items = couponService.calcCashier(coupons, pNo, totalFee,
					user.getId(), productId, productName, user.getPartner(), bizNos);
			cashierService.save(items, pNo, PaymentChannel.PARTNER);
			PaymentMode payment = null;
			BigDecimal discount = getCouponTotalFee(items);
			totalFee = totalFee.subtract(discount);
			if (totalFee.compareTo(BigDecimal.ZERO) < 1) { // 红包就能支付整个订单费用
				items = cashierService.listByBizNo(pNo);  // items需要重新拿一次(入库后需要拿到id)
				for(CashierItem item : items){
					payment = item.getPaymentMode();
					cashierService.startInnerPay(item, "order", payment);
					
					if (cashierService.nextCashierItem(item.getBizNo()) == null) {
						cashierService.doOrderPay(item);
						break;
					}
				} // end for
			}
		}

		PaymentResponseClientVO paymentResponse = null;
		if(form.getPayType() != null){
			paymentResponse = requestPay(request, response, orders, pNo, totalFee, form.getPayType());
		} else {
			log.error("lack payType from parameter..." + JSON.toJSONString(form));
		}

		return new ResponseObject<PaymentResponseClientVO>(paymentResponse);
}
	
	public PaymentResponseClientVO requestPay(HttpServletRequest request, HttpServletResponse response, Order order, String payNo, BigDecimal totalFee, PaymentMode payType, boolean f) {
		List<Order> orders = new ArrayList<Order>();
		orders.add(order);
		return requestPay(request, response, orders, payNo, totalFee, payType);
		
	}
	public PaymentResponseClientVO requestPay(HttpServletRequest req, HttpServletResponse resp, 
				List<Order> orders, String payNo, BigDecimal totalFee, PaymentMode payType) {
		
		totalFee = totalFee.setScale(2, BigDecimal.ROUND_DOWN);
		PaymentRequestVO payRequest = new PaymentRequestVO();
//		payRequest.setSubject("想去网：订单号-"+payNo);
		payRequest.setTotalFee(totalFee);
		payRequest.setTradeNo(payNo);
		
		List<String> orderIds = new ArrayList<String>();
		List<String> orderNos = new ArrayList<String>();
		for(Order order : orders){
			orderIds.add(order.getId());
			orderNos.add(order.getOrderNo());
		}
		payRequest.setOrderIds(orderIds);
		
		PaymentResponseClientVO paymentResponse = null;
		User user = getCurrentUser();
		payRequest.setUserId(user.getId());
		
		String payAgreementId = req.getParameter("payAgreementId");
		payRequest.setPayAgreementId(payAgreementId);
		
		payRequest.setRemoteHost(req.getRemoteHost());
		
		if(totalFee.compareTo(BigDecimal.ZERO) == 0){
			paymentResponse = new PaymentResponseClientVO();
			paymentResponse.setOrderIds(orderIds);
			paymentResponse.setPayStatus("SUCCESS");
		} else {
			ThirdPartyPayment payment = thirdPartyPaymentFactory.getPayment(payType);
			if(payType == PaymentMode.UNION){
				paymentResponse = new PaymentResponseClientVO(payRequest.getTradeNo(), null, payRequest.getTotalFee(), 
						payRequest.getOrderIds(), PaymentMode.UNION);
			}
			else if (payment != null) {
				paymentResponse = payment.sign(payRequest);
			}else{
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "不支持的支付方式 [" + payType + "]");
			}
			
			List<CashierItem> items = new ArrayList<CashierItem>();
			
			items.add(new CashierItem(payNo, PaymentChannel.PLATFORM, payType, totalFee, user.getId(), 
					payAgreementId, user.getPartner(), StringUtils.join(orderNos, ","), null, payNo, PaymentStatus.PENDING));
	        
			cashierService.save(items, payNo, PaymentChannel.PLATFORM);
		}
		
		return paymentResponse;
	}
	
    /**
	 * 订单详情（是否显示退货按钮）
	 * 返回除了订单之外的其他的附属信息
	 * openapi/b/order/view?id=1
	 */
	@ResponseBody
	@RequestMapping("/order/view2")
	public ResponseObject<OrderVOEx> view2(@RequestParam String id) {
		OrderVO order = orderService.loadVO(id);
		if (order == null) {
		    throw new BizException(GlobalErrorCode.NOT_FOUND, "订单[id=" + id + "]不存在，或者没有权限访问");
		}
		
		// 设置物流公司官网
		boolean oldLc=true;
		for (LogisticsCompany lc : LogisticsCompany.values()) {
			if(lc.getName().equals(order.getLogisticsCompany())){
				oldLc = false;
				order.setLogisticsOfficial(lc.getUrl());
				break;
			}
		}
		
		if(oldLc){
			//TODO 兼容老版本处理
			if("顺丰".equals(order.getLogisticsCompany()) || "顺丰快递".equals(order.getLogisticsCompany()) || "SF_EXPRESS".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.sf-express.com/");
			}else if("圆通".equals(order.getLogisticsCompany()) || "圆通快递".equals(order.getLogisticsCompany()) || "YTO".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.yto.net.cn/");
			}else if("申通".equals(order.getLogisticsCompany()) || "STO".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.sto.cn/");
			}else if("中通".equals(order.getLogisticsCompany()) || "ZTO".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.zto.cn/");
			}else if("百世汇通".equals(order.getLogisticsCompany()) || "BESTEX".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.htky365.com/");
			}else if("韵达".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.yundaex.com/");
			}else if("天天".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.ttkdex.com/");
			}else if("全峰".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.qfkd.com.cn/");
			}else if("邮政EMS".equals(order.getLogisticsCompany()) || "中国邮政".equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial("http://www.ems.com.cn/");
			}else{
				order.setLogisticsOfficial("");
			}
		}
		
		// 商店名称
		Shop shop = shopService.load(order.getShopId());
		if (shop != null) {
			order.setShopName(shop.getName());
		}
		
		// 图片信息回填
		String imgUrl = "";
		for (OrderItem item : order.getOrderItems()) {
			imgUrl = item.getProductImg();
			item.setProductImgUrl(imgUrl);
		}
		order.setImgUrl(imgUrl);
		
		// 收货地址回填
		if (order.getOrderAddress() != null) {
	        order.setAddressDetails(zoneService.genFullAddr(order.getOrderAddress().getZoneId(),
	        		order.getOrderAddress().getStreet()));
		}

		// 订单中的商品关联的分佣总金额
		List<Commission> commissions = unionService.listByOrderId(order.getId());
		BigDecimal cmFee = BigDecimal.ZERO; // 分佣总金额
		for(Commission cm : commissions){
			if(CommissionType.KKKD != cm.getType()){
				cmFee = cmFee.add(cm.getFee());
			}
		}
		order.setCommissionFee(cmFee);
		
		// 折扣
		if(order.getDiscountFee()==null){
			order.setDiscountFee(BigDecimal.ZERO );
		}
		
		// 获取交易手续费
		BigDecimal handingFee  = orderService.loadTechServiceFee(order);
		if (!handingFee.equals(BigDecimal.ZERO)) {
			Formatter fmt = new Formatter();
			try {
				fmt.format(ORDERNOTES, serviceFeethreshold, handingFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				order.setNotes(fmt.toString() );
			} finally {
				fmt.close();
			}
		}
		order.setHongbaoAmount(handingFee);
		
		// 订单红包信息
		List<CouponInfoVO> orderCoupons = cashierService.loadCouponInfoByOrderNo(order.getOrderNo());
		order.setOrderCoupons(orderCoupons);

		OrderVOEx orderEx = new OrderVOEx(order, orderService.findOrderFees(order, cmFee));
		orderEx.setDefDelayDate(defDelayDate);
		orderEx.setRefundableFee(orderEx.getTotalFee().subtract(orderEx.getLogisticsFee()));
		
		// 是否显示退货按钮
		orderEx.setShowRefundBtn(false);
		//订单处于交易中的状态，快店IOS审核失败，暂时关闭买家端退款入口
		if(orderEx.getStatus() != OrderStatus.SUBMITTED && orderEx.getStatus() != OrderStatus.SUCCESS && orderEx.getStatus() != OrderStatus.CANCELLED ){
			if(orderEx.getStatus() == OrderStatus.CLOSED){
				List<OrderRefund> refunds = orderRefundService.listByOrderId(order.getId());
				if(refunds.size() > 0){
					orderEx.setShowRefundBtn(true);
				}
			}else{
				orderEx.setShowRefundBtn(true);
			}
		}
		
		return new ResponseObject<OrderVOEx>(orderEx);
	}
	
	/**
     * 买家获取订单信息 by xbw
     * 返回数据：订单，总数，分页信息
     * &status=PAID&page=0
     */
	@RequestMapping("/b/order/list")
 	public ResponseObject<Map<String, Object>> orders(@RequestParam String extUid ,@RequestHeader("Domain") String domain,OrderStatus status, Pageable pageable) {
		List<OrderVO> orders =openApiOrderService.listByStatus4Buyer(domain, extUid, status, pageable);
        for (OrderVO order : orders) {
            //判断是否显示退款按钮
            if(order.getStatus() != OrderStatus.SUBMITTED && order.getStatus() != OrderStatus.SUCCESS && order.getStatus() != OrderStatus.CANCELLED ){
    			if(order.getStatus() == OrderStatus.CLOSED){
    				List<OrderRefund> refunds = orderRefundService.listByOrderId(order.getId());
    				if(refunds.size() > 0){
    					order.setShowRefundBtn(true);
    				}
    			}else{
    				order.setShowRefundBtn(true);
    			}
    		}
        }
        Long count= openApiOrderService.countBuyerOrdersByStatus(domain, extUid, status, null, null);		
        Map<String, Object> result = new HashMap<String, Object>();
        
		result.put("orders", orders);
		result.put("count", count);
		result.put("page", pageable);
		if(status == null){
			Long submittedCount = openApiOrderService.countBuyerOrdersByStatus(domain, extUid,OrderStatus.SUBMITTED, null, null);
			Long paidCount = openApiOrderService.countBuyerOrdersByStatus(domain, extUid,OrderStatus.PAID, null, null);
			Long shippedCount = openApiOrderService.countBuyerOrdersByStatus(domain, extUid,OrderStatus.SHIPPED, null, null);
			result.put("submittedCount", submittedCount);
			result.put("paidCount", paidCount);
			result.put("shippedCount", shippedCount);
		}
		return new ResponseObject<Map<String, Object>>(result);
	}
    
    /**
     * by zzd
     * 只返回订单
     * @param id
     * @param form
     * @param errors
     * @return
     */
    @RequestMapping(value = "/order/view")
    @ResponseBody
    public ResponseObject<OrderVO> view(@RequestParam String id,@RequestHeader("Domain") String Domain) {
        OrderVO order = orderService.loadVO(id);
        List<OrderItem> list = order.getOrderItems();
       	String imgUrl = "";
        for (OrderItem item : list) {
        	item.setProductImgUrl(item.getProductImg());  // 带了默认尺寸
        	imgUrl = item.getProductImg();
        	item.setProductImg(ResourceFacade.resolveUrl2Orig(imgUrl));
        }
        order.setImgUrl(imgUrl);
        return new ResponseObject<OrderVO>(order);
    }
    
    /**
	 * 延迟签收 (推迟账务打款时间, 买家发起) by zhuyin
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/b/order/delaySign")
	public ResponseObject<Boolean> delaySign(@RequestParam String id,@RequestHeader("Domain") String domain,String extUid) {
		openApiOrderService.delaySign(id,domain,extUid);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 订单取消by zzd
	 * @param id
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/b/order/cancel")
    public ResponseObject<Boolean> cancelOrder(@RequestParam String id, @RequestHeader("Domain") String domain, @RequestParam String extUid){
    	openApiOrderService.cancel(id,domain,extUid);
    	return new ResponseObject<Boolean>(true);
    }
    
    /**
	 * 订单签收  by zzd
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/b/order/signed")
	public ResponseObject<Boolean> signed(@RequestParam String id,@RequestHeader("Domain") String domain,String extUid) {
	 	openApiOrderService.sign(id,domain,extUid);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
     * 卖家更新未付款订单的价格by xbw
     * goodsFee; // 订单商品金额
	 * logisticsFee; // 订单物流金额
	 * price // 总价格
     */
    @RequestMapping("/order/update-price")
    @ResponseBody
    public ResponseObject<OrderVO> updateOrderPrice(@RequestParam String orderId, String price, String goodsFee, String logisticsFee, @RequestParam String extUid,@RequestHeader("Domain") String domain) {
    	User user = loadExtUser(domain, extUid);
    	
	   if(StringUtils.isNoneBlank(goodsFee) || StringUtils.isNoneBlank(logisticsFee)){
    		BigDecimal bGoodsFee = NumberUtils.createBigDecimal(goodsFee).setScale(2, BigDecimal.ROUND_DOWN);
    	    logisticsFee = StringUtils.defaultIfBlank(logisticsFee, "0");
    		BigDecimal bLogisticsFee = NumberUtils.createBigDecimal(logisticsFee);
    		//商品价格不能改为0
    		orderService.updatePrice(orderId, bGoodsFee, bLogisticsFee, user.getId());
    		
    	} else { //支持老接口
    		BigDecimal bPrice = new BigDecimal(price).setScale(2, BigDecimal.ROUND_DOWN);
    		orderService.updateTotalPrice(orderId, bPrice,user.getId());
    	}
	   
	    // 回填图片和地址
        OrderVO order = orderService.loadVO(orderId);
        String imgUrl = "";
        for (OrderItem item : order.getOrderItems()) {
            imgUrl = item.getProductImg();
            item.setProductImgUrl(imgUrl);
        }
        order.setImgUrl(imgUrl);
        if (order.getOrderAddress() != null) {
	        order.setAddressDetails(zoneService.genFullAddr(order.getOrderAddress().getZoneId(),
	        		order.getOrderAddress().getStreet()));
        }
        
        return new ResponseObject<OrderVO>(order);
    }
   
    /**
     * 发送 订单留言 openapi/order/send-message?orderId=1r5ez0w9&groupId=1&content=
     * ceshineirong
     */
    @ResponseBody
    @RequestMapping("/order/send-message")
    public ResponseObject<Boolean> sendOrderMessage(
            @ModelAttribute OrderMessage form) {
        if (StringUtils.isEmpty(form.getOrderId())) {
            log.debug("请选择您要留言的订单");
            throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单不存在");
        }

        return new ResponseObject<Boolean>(orderService.saveMessage(form));
    }

    /**
     * 买家 查看所有订单留言 openapi/b/order/list-allMsgs?userId=cay3ci4n
     * 
     * @param orderId
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/b/order/list-allMsgs")
    public ResponseObject<List<OrderMsgVO>> listAllMsgs(String userId,
            String extUid, @RequestHeader("Domain") String domain, Pageable page) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(extUid)) {
            throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
                    "请输入用户信息");
        }

        if (!StringUtils.isBlank(extUid)) {
            User user = loadExtUser(domain, extUid);
            userId = user.getId();
        } else if (!StringUtils.isBlank(userId)) {
            User user = userService.load(userId);
            if (user == null) {
                throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR,
                        "用户不存在");
            }
        }

        // 获取卖家的回复列表
        List<OrderMessage> aList = orderService.viewReplyMsgs(userId, page);
        if (aList.isEmpty()) {
            ResponseObject<List<OrderMsgVO>> aEmptyList = new ResponseObject<List<OrderMsgVO>>();
            aEmptyList.setData((List<OrderMsgVO>) Collections.EMPTY_LIST);
            return aEmptyList;
        }

        // 根据卖家对买家留言的groupId找到是回复的哪条买家留言
        List<OrderMsgVO> aRetList = new ArrayList<OrderMsgVO>();
        for (OrderMessage om : aList) {
            // 获取买家留言
            OrderMessage myMsg = orderService.selectOrderMsgById(om
                    .getGroupId());
            if (myMsg == null) {
                continue;
            }

            OrderMsgVO aVo = new OrderMsgVO();
            aVo.setOrderId(myMsg.getOrderId());
            aVo.setBuyerContent(myMsg.getContent());
            aVo.setSellerContent(om.getContent());
            aVo.setMsgTime(myMsg.getCreatedAt().getTime());

            List<OrderItem> itemList = orderService.listOrderItems(myMsg
                    .getOrderId());
            if (!itemList.isEmpty()) {
                aVo.setProductId(itemList.get(0).getProductId());
                aVo.setTitle(itemList.get(0).getProductName());
                aVo.setImgUrl(itemList.get(0).getProductImg());
                aRetList.add(aVo);
            }

        }
        return new ResponseObject<List<OrderMsgVO>>(aRetList);
    }

    /**
     * 查看某条订单留言 openapi/order/list-Msgs?orderId=1r5ez0w9
     * 
     * @param orderId
     * @return
     */
    @ResponseBody
    @RequestMapping("/order/list-Msgs")
    public ResponseObject<List<OrderMessage>> listOrderMsgs(@RequestParam String orderId) {
        List<OrderMessage> omList = orderService.viewMessages(orderId);
        return new ResponseObject<List<OrderMessage>>(omList);
    }

    /**
     * 保存订单的地址
     */
    @ResponseBody
    @RequestMapping("/order/updateOrderAddress")
    public ResponseObject<OrderAddress> updateOrderAddress(
            @ModelAttribute OrderAddress orderAddress) {
        int i = orderAddressService.update(orderAddress);

        OrderAddress od = null;
        if (i > 0) {
            od = orderAddressService.load(orderAddress.getId());
        }

        return new ResponseObject<OrderAddress>(od);
    }
    
    private Boolean batchPayLastTime(String bizNo) {
    	//List<CashierItem> items1 = cashierService.listByBatchBizNo(batchBizNo, userId, status) // PENDING
    	List<CashierItem> items = cashierService.listByBizNo(bizNo);
    	for (CashierItem i : items) { // if multi pay last time, then we should change the payNo
    		if (i.getBatchBizNos() != null && i.getBatchBizNos().split(",").length > 1) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @ResponseBody
    @RequestMapping("/order/pay")
    public ResponseObject<PaymentResponseClientVO> paymentList(HttpServletRequest request, HttpServletResponse response, @RequestHeader("Domain") String domain, @RequestParam String extUid, String id, PaymentMode payType){
    	OrderVO order = orderService.loadVO(id);
		//调通接口用到
		User user = null;
		try {
			user = getCurrentUser();
		} catch (Exception e) {
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "很抱歉,请重新登录后再来支付"); 
		}
		
    	if(order.getStatus() != OrderStatus.SUBMITTED){
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "订单状态异常，或已支付"); 
    	}
    	BigDecimal totalFee = order.getTotalFee();
    	String pNo = StringUtils.defaultIfBlank(order.getPayNo(), UniqueNoUtils.next(UniqueNoType.P));
    	Boolean newPayNo = batchPayLastTime(order.getPayNo());
    	order.setPayType(payType);
    	Order o = new Order();
		o.setId(id);
		if (StringUtils.isEmpty(order.getPayNo()) || newPayNo) {
			o.setPayNo(pNo);  // 再次支付时如果之前是多笔订单一起支付则要更换payNo
		}
		o.setPayType(payType);
		orderService.update(o);
		
		List<CashierItem> items = couponService.reCalcCouponByOrder(order, newPayNo,pNo);
    	PaymentMode payment = null;
    	for(CashierItem item : items){
//    		totalFee = totalFee.subtract(item.getAmount());   // 换到loadVO里去做减法
    		payment = item.getPaymentMode();
    	}
    	
		if(totalFee.compareTo(BigDecimal.ZERO) < 1) {
			PaymentRequestVO p = new PaymentRequestVO();
			p.setCouponId(items.get(0).getCouponId());
			p.setTradeNo(items.get(0).getBizNo());
			p.setTotalFee(items.get(0).getAmount());
			p.setOutUserId(user != null ? user.getExtUserId() : "");
			p.setProductId(items.get(0).getProductId());
//			p.setSubject(items.get(0).getBizNo());

			ThirdPartyPayment pay = thirdPartyPaymentFactory.getPayment(PaymentMode.XIANGQU);
			pay.payRequest(null, null, p);
			cashierService.paid(pNo, "order", payment);
		}
    	PaymentResponseClientVO paymentRespon = requestPay(request, response, order, pNo, totalFee, payType, true);
		return new ResponseObject<PaymentResponseClientVO>(paymentRespon);
    }
    
    /**
     * 买家删除订单
     * @param domain
     * @param extUid
     * @param orderId
     * @return
     */
    @ResponseBody
    @RequestMapping("/order/delete-buyer")
	public ResponseObject<Boolean> deleteByBuyer(
			@RequestHeader("Domain") String domain,
			@RequestParam String extUid,
			@RequestParam String orderId) {
		return new ResponseObject<Boolean>(openApiOrderService.deleteByBuyer(domain, extUid, orderId));
	}
    
	@ResponseBody
	@RequestMapping("/pay/union/request")
	public ResponseObject<PaymentResponseClientVO> umpay(HttpServletRequest req, HttpServletResponse resp, PaymentRequestVO request) {
		PaymentResponseClientVO paymentResponse = null;
		log.debug(JSON.toJSONString(request));
		try {
			BigDecimal totalFee = null;
			List<CashierItem> items = cashierService.listByBizNo(request.getTradeNo());
			for(CashierItem item : items){
				if(item.getPaymentChannel() == PaymentChannel.PLATFORM){
					totalFee = item.getAmount();
					break;
				}
			}
			List<Order> orders = new ArrayList<Order>();
			String[] orderNos = items.get(0).getBatchBizNos().split(",");
			for(String orderNo : orderNos){
				orders.add(orderService.loadByOrderNo(orderNo));
			}
			
			paymentResponse = requestPay(req, resp, orders, request.getTradeNo(), totalFee, request.getPayType());
		} catch (Exception e) {
			log.error("支付请求失败，tradeNo=" + request.getTradeNo()+","+e.getMessage(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "发起银行卡请求失败");
		}
		return new ResponseObject<PaymentResponseClientVO>(paymentResponse);
	}
	
	
}
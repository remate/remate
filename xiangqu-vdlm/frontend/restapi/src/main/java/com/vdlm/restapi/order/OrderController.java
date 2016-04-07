package com.vdlm.restapi.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.mapper.OrderItemMapper;
import com.vdlm.dal.model.Commission;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.OrderItemComment;
import com.vdlm.dal.model.OrderMessage;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.vo.CouponInfoVO;
import com.vdlm.dal.vo.MsgOrdersVO;
import com.vdlm.dal.vo.MsgProdInfoVO;
import com.vdlm.dal.vo.OrderItemCommentVO;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.voex.OrderVOEx;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.apiVisitorLog.ApiVisitorLogService;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderAddressService;
import com.vdlm.service.order.OrderItemCommentService;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.union.UnionService;
import com.vdlm.service.zone.ZoneService;

@Controller
public class OrderController extends BaseController {
	
	@Autowired
	private OrderItemCommentService orderItemCommentService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderAddressService orderAddressService;
	@Autowired
	private ZoneService zoneService;
	@Autowired
	private ResourceFacade resourceFacade;
	@Autowired
	private ApiVisitorLogService apiVisitorLogService;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private OrderRefundService orderRefundService;
	
	@Autowired
	private UnionService unionService;
	@Autowired
	private CashierService cashierService;
	
	@Autowired
	private ShopService shopService;
	
	//private static final String ORDERNOTES = "{order.handingcharge}";
	private  final String ORDERNOTES = "您的交易金额大于%s，需要支付%s手续费";
	
	@Value("${tech.serviceFee.standard}")
	private String serviceFeethreshold;
	
	 @Value("${order.delaysign.date}")
	 private int defDelayDate;

	/**
	 * 买家获取订单信息
	 * @param status
	 * @param pageable
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/my/orders")
	public ResponseObject<Map<String, Object>> myOrders(String status, Pageable pageable) {
	    Map<String, Object> result = new HashMap<String, Object>();
		OrderStatus os = null;
        try {
            if (StringUtils.isNotEmpty(status)) {
                os = OrderStatus.valueOf(status.toUpperCase());
            }
        } catch (Exception e) {
        	log.error("status emnuaration error:" + e);
        }
		List<OrderVO> orders = orderService.listByStatus4Buyer(os, pageable);
		for (OrderVO order : orders) {
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
		Long count = orderService.getCountByStatus4Buyer(os);
		
		result.put("orders", orders);
		result.put("count", count);
		if(status == null){
			Long submittedCount = orderService.countBuyerOrdersByStatus(OrderStatus.SUBMITTED);
			Long paidCount = orderService.countBuyerOrdersByStatus(OrderStatus.PAID);
			Long shippedCount = orderService.countBuyerOrdersByStatus(OrderStatus.SHIPPED);
			result.put("submittedCount", submittedCount);
			result.put("paidCount", paidCount);
			result.put("shippedCount", shippedCount);
		}
		return new ResponseObject<Map<String, Object>>(result);
	}

	/**
	 * 卖家获取订单信息
	 * @param status
	 * @param pageable
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/order/list")
    public ResponseObject<List<OrderVO>> search(String status, Pageable pageable) {
        OrderStatus os = null;
        try {
            if (StringUtils.isNotEmpty(status)) {
                os = OrderStatus.valueOf(status.toUpperCase());
            }
        } catch (Exception e) {
            // return new ResponseObject<List<OrderVO>>();
        }
        
        List<OrderVO> result = orderService.listByStatus4Seller(os, pageable);
        if (result.size() > 0 && OrderStatus.PAID.equals(os)) {  // ???
            OrderVO vo = result.get(0);
            vo.setSeq(orderService.selectOrderSeqByShopId(vo.getShopId()));
        }
        
        return new ResponseObject<List<OrderVO>>(result);
    }
	
	@ResponseBody
	@RequestMapping("/order/list/{status}")
	public ResponseObject<List<OrderVO>> listOrder(@PathVariable String status, Pageable pageable) {
		if (status == null)
			return new ResponseObject<List<OrderVO>>();
		OrderStatus os;
		try {
			os = OrderStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			return new ResponseObject<List<OrderVO>>();
		}
		List<OrderVO> result = new ArrayList<OrderVO>();

		result = orderService.listByStatus4Seller(os, pageable);
		for (OrderVO order : result) {
			if (order.getOrderAddress() != null) {
				order.setAddressDetails(zoneService.genFullAddr(order.getOrderAddress().getZoneId(),
	        		order.getOrderAddress().getStreet()));
			}
		}
		
		if (result.size() > 0 && OrderStatus.PAID.equals(os)) {
			OrderVO vo = result.get(0);
			vo.setSeq(orderService.selectOrderSeqByShopId(vo.getShopId()));
		}
		
		apiVisitorLogService.visit(orderService.getCurrentUser().getId(), "/order/list/{status}");
		
		return new ResponseObject<List<OrderVO>>(result);
	}
	
	/**
	 * 已关闭订单状态合并搜索接口特殊处理
	 * @param key
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unused")
	@ResponseBody
	@RequestMapping("/order/listByClosed/{status}")
	public ResponseObject<List<OrderVO>> searchOrderByKey(@RequestParam("key") String key, @PathVariable String status) {
		List<OrderVO> result =new ArrayList<OrderVO>();
		if (status == null || key == null || key.isEmpty()) {
			return new ResponseObject<List<OrderVO>>(result);
		}
		
		OrderStatus os;
		try {
			os = OrderStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			return new ResponseObject<List<OrderVO>>(result);
		}
		
		result = orderService.listByStatusKey4Seller(OrderStatus.CLOSED, key);
		
		for (OrderVO order : result) {
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
		}
		return new ResponseObject<List<OrderVO>>(result);
	}
	
	@ResponseBody
	@RequestMapping("/order/listByKey/{status}")
	public ResponseObject<List<OrderVO>> listOrderByKey(String key, @PathVariable String status) {
		if (status == null)
			return new ResponseObject<List<OrderVO>>();
		
		OrderStatus os;
		try {
			os = OrderStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			return new ResponseObject<List<OrderVO>>();
		}
		
		List<OrderVO> result = orderService.listByStatusKey4Seller(os, key);
		for (OrderVO order : result) {
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
		}
		return new ResponseObject<List<OrderVO>>(result);
	}

	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/{id}")
	public ResponseObject<OrderVOEx> view(@PathVariable String id) {
		OrderVO order = orderService.loadVO(id);
		if (order == null) {
		    throw new BizException(GlobalErrorCode.NOT_FOUND, "订单[id=" + id + "]不存在，或者没有权限访问");
		}
		
		// 设置物流公司官网
		for (LogisticsCompany logisticsCompany : LogisticsCompany.values()) {
			if(logisticsCompany.getName().equals(order.getLogisticsCompany())){
				order.setLogisticsOfficial(logisticsCompany.getUrl());
			}
		}
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
		
		Shop shop = shopService.load(order.getShopId());
		if (shop != null) {
			order.setShopName(shop.getName());
		}
		
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

		// 分佣
		List<Commission> commissions = unionService.listByOrderId(order.getId());
		BigDecimal cmFee = BigDecimal.ZERO;
		for(Commission cm : commissions){
			cmFee = cmFee.add(cm.getFee());
		}
		order.setCommissionFee(cmFee);
		if(order.getDiscountFee()==null){
			order.setDiscountFee(BigDecimal.ZERO );
		}
		BigDecimal handingFee  = orderService.loadTechServiceFee(order);
		if (  ! handingFee.equals(BigDecimal.ZERO)) {
			Formatter fmt = new Formatter();
			try {
				fmt.format(ORDERNOTES, serviceFeethreshold, handingFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				order.setNotes(fmt.toString() );
			} finally {
				fmt.close();
			}
		}
		
		// TODO ?
		order.setHongbaoAmount(handingFee);
		
		List<CouponInfoVO> orderCoupons = cashierService.loadCouponInfoByOrderNo(order.getOrderNo());
		order.setOrderCoupons(orderCoupons);

		OrderVOEx orderEx = new OrderVOEx(order, orderService.findOrderFees(order));
		BigDecimal discountFee = ObjectUtils.defaultIfNull(orderEx.getDiscountFee(), BigDecimal.ZERO);
//		BigDecimal paidFee = ObjectUtils.defaultIfNull(cashierService.loadPaidFee(order.getOrderNo()), BigDecimal.ZERO);
		orderEx.setDiscountFee(discountFee.add(handingFee));
		orderEx.setDefDelayDate(defDelayDate);
		orderEx.setRefundableFee(orderEx.getTotalFee().subtract(orderEx.getLogisticsFee()));
		
		orderEx.setShowRefundBtn(false);
		//快店IOS审核失败，暂时关闭买家端退款入口
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
     * 订单详情
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/order/detail")
    public ResponseObject<OrderVOEx> view2(@RequestParam String id) {
        return this.view(id);
    }
    
	/**
     * 订单删除
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/order/delete")
    public ResponseObject<Boolean> delete(@RequestParam String orderId) {
        return new ResponseObject<Boolean>(orderService.delete(orderId) == 1);
    }
	
	/**
	 * 卖家更新未付款订单的价格
	 * @param id
	 * @param price
	 * @return
	 */
	@RequestMapping("/order/{id}/update-price")
    @ResponseBody
    public ResponseObject<OrderVO> updatePrice(@PathVariable String id, BigDecimal price) {
        orderService.updateTotalPrice(id, price);
        
        OrderVO order = orderService.loadVO(id);
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
     * 卖家更新未付款订单的价格
     * @param id
     * @param price
     * @return
     */
    @RequestMapping("/order/update-price")
    @ResponseBody
    public ResponseObject<OrderVO> updateOrderPrice(String id, String price, String goodsFee, String logisticsFee) {
    	if(StringUtils.isNoneBlank(goodsFee) || StringUtils.isNoneBlank(logisticsFee)){
    		BigDecimal bGoodsFee = null, bLogisticsFee = null;
    		if(StringUtils.isNotBlank(goodsFee)) {
    			bGoodsFee = NumberUtils.createBigDecimal(goodsFee).setScale(2, BigDecimal.ROUND_DOWN);
	    		if(bGoodsFee.compareTo(BigDecimal.ZERO) == 0){
	    			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,"商品价格不能为0");
	    		}
    		}
    		
    		if(StringUtils.isNoneBlank(logisticsFee))
    			bLogisticsFee = NumberUtils.createBigDecimal(logisticsFee).setScale(2, BigDecimal.ROUND_DOWN);
//    		logisticsFee = StringUtils.defaultIfBlank(logisticsFee, "0");
//    		BigDecimal bLogisticsFee = NumberUtils.createBigDecimal(logisticsFee);
    		//商品价格不能改为0
    		orderService.updatePrice(id, bGoodsFee, bLogisticsFee);
    		
    	} else { //支持老接口
    		BigDecimal bPrice = new BigDecimal(price).setScale(2, BigDecimal.ROUND_DOWN);
    		orderService.updateTotalPrice(id, bPrice);
    	}
        
        OrderVO order = orderService.loadVO(id);
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
    
    @ResponseBody
    @RequestMapping("/api/order/cancel")
    public ResponseObject<Boolean> cancelOrder(@RequestParam String orderId){
    	orderService.cancel(orderId);
    	return new ResponseObject<Boolean>(true);
    }
	
    /**
     * 卖家订单发货
     */
	@ResponseBody
	@RequestMapping("/order/shipped")
	public ResponseObject<Boolean> shipped(@Valid @ModelAttribute OrderShippedForm form, Errors errors) {
		ControllerHelper.checkException(errors);
		String logisticComp = "";
		try {
            logisticComp = LogisticsCompany.valueOf(form.getLogisticsCompany()).toString();
		} catch (Exception ex) {
			logisticComp = form.getLogisticsCompany();
		}
		return new ResponseObject<Boolean>(orderService.ship(form.getOrderId(), logisticComp, form.getLogisticsOrderNo()));
	}

	/**
	 * 卖家订单退款
	 * @param orderId
	 * @param refundment  //老接口调用，141230版本后不需要
	 * @param goodsFee
	 * @param logisticsFee
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/refund")
	public ResponseObject<Boolean> refund(@RequestParam String orderId, String refundment) {
		BigDecimal refundFee = new BigDecimal(refundment).setScale(2, BigDecimal.ROUND_DOWN);
		
	    if (refundment != null) {
	        orderService.refund(orderId, refundFee);
	    } else {
	        orderService.refund(orderId);
	    }
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 退款新接口141230版本更新
	 * @param orderId
	 * @param goodsFee  
	 * @param logisticsFee
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/refundFee")
	public ResponseObject<Boolean> refundFee(String orderId, String goodsFee, String logisticsFee) {
		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "暂时不支持卖家直接退款功能");
		
		//BigDecimal bGoodsFee = new BigDecimal(goodsFee).setScale(2, BigDecimal.ROUND_DOWN);
		//BigDecimal bLogisticsFee = new BigDecimal(logisticsFee).setScale(2, BigDecimal.ROUND_DOWN);
		//orderService.refundFee(orderId, bGoodsFee, bLogisticsFee);
		//return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 订单签收 
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/signed")
	public ResponseObject<Boolean> signed(@RequestParam String orderId) {
		orderService.sign(orderId);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 延迟签收 (推迟账务打款时间, 买家发起)
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/delaySign")
	public ResponseObject<Boolean> delaySign(@RequestParam String orderId) {
		orderService.delaySign(orderId);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 提醒卖家发货
	 */
	@ResponseBody
    @RequestMapping("/order/remindShip")
    public ResponseObject<Boolean> remindShip(@RequestParam("orderId") String  orderId) {
		orderService.remindShip(orderId);
		return new ResponseObject<Boolean>(true);
    }
	
	/**
	 * 订单留言
	 * @param orderId
	 * @param content
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/order/send-message")
    public ResponseObject<Boolean> sendOrderMessage(@Valid @ModelAttribute OrderMessage form) {
		if (StringUtils.isEmpty(form.getOrderId())) 
			return new ResponseObject<Boolean>(false);
		return new ResponseObject<Boolean>(orderService.saveMessage(form));
    }

	/**
	 * 快店查看有留言订单
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/order/list-msgorders")
    public ResponseObject<List<MsgOrdersVO>> listMessageOrders(Pageable page) {
		List<MsgOrdersVO> messages = orderService.listMsgOrders(page);
		if (messages != null && messages.size() != 0) {
			for (MsgOrdersVO aVo : messages) {
				aVo.setProductImg(aVo.getProductImg());
			}
			return new ResponseObject<List<MsgOrdersVO>> (messages);
		} else {
			ResponseObject<List<MsgOrdersVO>> aEmptyList =  new ResponseObject<List<MsgOrdersVO>>();
			aEmptyList.setData((List<MsgOrdersVO>)Collections.EMPTY_LIST);
			return aEmptyList;
		}
    }
	
	private List<OrderMessage> filterBuyerCmt(List<OrderMessage> msgList) {
		if (msgList == null || msgList.size() == 0) return null;
		
		List<OrderMessage> retList = new ArrayList<OrderMessage>();
		for (OrderMessage om : msgList) {
			if (om.getGroupId().equals("0")) {
				retList.add(om);
			}
		}
		Collections.reverse(retList);
		return retList;
	}
	
	private List<OrderMessage> filterSellerReps(String groupId, List<OrderMessage> msgList) {
		if (msgList == null || msgList.size() == 0) return null;
		List<OrderMessage> retList = new ArrayList<OrderMessage>();
		
		for (OrderMessage om : msgList) {
			if (om.getGroupId().equals(groupId)) {
				retList.add(om);
			}
		}
		Collections.reverse(retList);
		return retList;
	}
	
	/**
	 * 快店查看某条订单留言与回复
	 * @param orderId
	 * @param page
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/order/list-messages")
    public ResponseObject<Map<String, Object>> listOrderMessages(@RequestParam String orderId) {
		// 检查该订单是否属于当前用户
		final Map<String, Object> result = new HashMap<String, Object>();
		Order aOrder = orderService.load(orderId);
		if (orderId == null || aOrder == null || !aOrder.getSellerId().equals(getCurrentUser().getId())) 
			throw new BizException(GlobalErrorCode.UNAUTHORIZED, "参数不合法");
		List<OrderMessage> messages = orderService.viewMessages(orderId);
		if (messages == null) {
			ResponseObject<Map<String, Object>> aEmptyList =  new ResponseObject<Map<String, Object>>();
			aEmptyList.setData((Map<String, Object>)Collections.EMPTY_MAP);
			return aEmptyList;
		}
		
		List<OrderMessageVO> retList = new ArrayList<OrderMessageVO>();
		List<OrderMessage> bCmtList = filterBuyerCmt(messages);
		if (bCmtList != null && bCmtList.size() != 0) {
			for (OrderMessage om  : bCmtList) {
				filterSellerReps(om.getId(), messages);
				OrderMessageVO aVo = new OrderMessageVO();
				aVo.setLeaderMsg(om); // 一条买家留言
				aVo.setRepsMsg(filterSellerReps(om.getId(), messages));    // 一条或多条卖家回复
				retList.add(aVo);
			}
		} // else 垃圾数据 一条订单的留言以买家发起, 不会只有卖家自己的回复
		
		MsgProdInfoVO aVo = orderService.selectProdInfoByOrderId(orderId);
		if (aVo != null) {
			result.put("productImg",  resourceFacade.resolveUrl(aVo.getProdImg()));
			result.put("productName", aVo.getProdName());
			result.put("skuDesc", aVo.getSkuDesc());
			result.put("paidAt", aVo.getPaidAt());
			result.put("paidFee", aVo.getPaidFee());
			result.put("orderId", orderId);
			result.put("avatar", null);
		} else {
			ResponseObject<Map<String, Object>> aEmptyList =  new ResponseObject<Map<String, Object>>();
			aEmptyList.setData((Map<String, Object>)Collections.EMPTY_MAP);
			return aEmptyList;
		}
		User aBuyerUser = userService.load(messages.get(0).getBuyerId());
		if (aBuyerUser != null) { result.put("buyerNick", aBuyerUser.getName()); }
		User aSellerUser = userService.load(messages.get(0).getSellerId());
		if (aSellerUser != null) { result.put("sellerNick", aSellerUser.getLoginname()); }
		result.put("msgList", retList);
		
		orderService.setMsgRead(orderId);
		
		return new ResponseObject<Map<String, Object>>(result);
    }
	
	/**
	 * 想去查看当前用户所有订单留言
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/order/list-allMsgs")
    public ResponseObject<List<OrderMsgVO>> listAllMsgs(Pageable page) { // 分页
		// 获取卖家对买家的回复列表
		List<OrderMessage> aList = orderService.viewReplyMsgs(page);
		if (aList == null || aList.size() == 0) {
			ResponseObject<List<OrderMsgVO>> aEmptyList =  new ResponseObject<List<OrderMsgVO>>();
			aEmptyList.setData((List<OrderMsgVO>)Collections.EMPTY_LIST);
			return aEmptyList;
		}
		
		// 根据卖家对买家留言的groupId找到是回复的哪条买家留言
		List<OrderMsgVO> aRetList = new ArrayList<OrderMsgVO>();
		for (OrderMessage om : aList) {
			OrderMessage myMsg = orderService.selectOrderMsgById(om.getGroupId());
			if (myMsg != null) {
				OrderMsgVO aVo = new OrderMsgVO();
				aVo.setOrderId(myMsg.getOrderId());
				aVo.setBuyerContent(myMsg.getContent());
				aVo.setSellerContent(om.getContent());
				aVo.setMsgTime(myMsg.getCreatedAt().getTime());
				List<OrderItem> itemList = orderItemMapper.selectByOrderId(myMsg.getOrderId());
				if (itemList != null) {
					aVo.setProductId(itemList.get(0).getProductId());
					aVo.setTitle(itemList.get(0).getProductName() );
					aVo.setImgUrl(  itemList.get(0).getProductImg());
					aRetList.add(aVo);
				}
			}
		}
		return new ResponseObject<List<OrderMsgVO>> (aRetList);
    }
	
	/**
	 * 想去查看某条订单留言
	 * @param orderId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("/order/list-Msgs")
    public ResponseObject<List<OrderMessage>> listOrderMsgs(@RequestParam String orderId) {
		List<OrderMessage> omList =orderService.viewMessages(orderId);
		if (omList != null) {
			return new ResponseObject<List<OrderMessage>>(omList);
		} else {
			ResponseObject<List<OrderMessage>> aEmptyList =  new ResponseObject<List<OrderMessage>>();
			aEmptyList.setData((List<OrderMessage>)Collections.EMPTY_LIST);
			return aEmptyList;
		}
    }
	
	/**
	 * 获取物流公司列表
	 */
	@ResponseBody
    @RequestMapping("/order/listLogisticsInfo")
    public ResponseObject<List<LogisticsInfoVO>> listLogistics() {
		List<LogisticsInfoVO> aList = new ArrayList<LogisticsInfoVO>();
		for (LogisticsCompany aLC : LogisticsCompany.values())  {
			LogisticsInfoVO avo = new LogisticsInfoVO();
			if ( ! aLC.equals(LogisticsCompany.OTHER)) {
				avo.setCompany(aLC.toString());
				aList.add(avo);
			}
		}
		return new ResponseObject<List<LogisticsInfoVO>>(aList);
    }
	
	
	/**
	 * 获取评论列表
	 * @param buyerId
	 * @param pager
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping("/comment/list")
	 public ResponseObject<List<OrderItemCommentVO>> loadCommentsBySeller(OrderItemComment orderItemComment, Pageable pager){
	  if(StringUtils.isBlank(orderItemComment.getOrderId())){
		  throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请输入要查询评论的订单id");
	  }
	  List<OrderItemComment> beans = orderItemCommentService.selectByEntityWithPage(orderItemComment, pager);
	  List<OrderItemCommentVO> result = new ArrayList<OrderItemCommentVO>();
	  for (int i = 0; i < beans.size(); i++) {
	   OrderItemCommentVO vo = new OrderItemCommentVO();
	   BeanUtils.copyProperties(beans.get(i), vo);
	   trans4Show(vo);
	   result.add(vo);
	  }
	  return new ResponseObject<List<OrderItemCommentVO>>(result);
	 }
	 

	private void trans4Show(OrderItemCommentVO vo){
	  OrderItem item = orderItemMapper.selectByPrimaryKey(vo.getOrderItemId());
	  vo.setPrice(item.getPrice());
	  vo.setAmount(item.getAmount());
	  vo.setProductImgUrl(item.getProductImg());
	  String name = item.getSkuStr();
	  if(name.contains("无")){
		  name=item.getProductName();
	  }
	  vo.setSkuStr(name);
	 }

	/**
	 * 回复评论
	 * @param form
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping("/comment/reply")
	 public ResponseObject<Boolean> replyComment(CommentReplyForm form){
	 if(StringUtils.isBlank(form.getId()) || StringUtils.isBlank(form.getReply())){
		 throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "回复评论时id 和回复不为空");
	 }
	 
	  OrderItemComment bean = orderItemCommentService.load(form.getId());
	  if(bean==null){
		  throw new BizException(GlobalErrorCode.NOT_FOUND, "您要回复的评论不存在");
	  }
	  String userId = getCurrentUser().getId();
	  if(!userId.equals(bean.getSellerId())){
	    throw new BizException(GlobalErrorCode.UNAUTHORIZED, "您没有权限回复评论");
	  }
	  
	  int rc = orderItemCommentService.sellerReply(form.getId(), form.getReply());
	  return new ResponseObject<Boolean>(rc==1);
	 }
	 
	 /**
	  * 删除评论
	  */
	 @ResponseBody
	 @RequestMapping("/comment/removeReplay")
	 public ResponseObject<Boolean> removeReplayComment(String id){
	 if(StringUtils.isBlank(id)){
		 throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "删除评论时，id不能为空");
	 }
	 
	  int rc = orderItemCommentService.emptyReply(id);
	  return new ResponseObject<Boolean>(rc==1);
	 }
}
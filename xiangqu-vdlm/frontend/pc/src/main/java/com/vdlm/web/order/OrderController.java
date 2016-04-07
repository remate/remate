package com.vdlm.web.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.Commission;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.OrderMessage;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.vo.CouponInfoVO;
import com.vdlm.dal.vo.OrderFeeVO;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.voex.OrderVOEx;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.excel.ExcelService;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.union.UnionService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.utils.ExcelUtils;
import com.vdlm.web.BaseController;

@Controller
public class OrderController extends BaseController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private ResourceFacade resourceFacade;
	@Autowired
	private OrderRefundService orderRefundService;
	@Autowired
	private ZoneService zoneService;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private UnionService unionService;

	@Autowired
	private ExcelService excelService;

	private final String ORDERNOTES = "您的交易金额大于%s，需要支付%s手续费";
	@Value("${tech.serviceFee.standard}")
	private String serviceFeethreshold;

	@Value("${order.delaysign.date}")
	private int defDelayDate;
	
	@Value("${export.excel.tempDir}")
	private String exportTemp;

	// 登录
	@RequestMapping(value = "/pc/login.html")
	public String pcLogin() {
		return "/login";
	}

	// 订单列表
	@RequestMapping(value = "/orders/list")
	public String ordersList(Model model, OrderStatus status, Integer size,
			Integer page, Boolean isDesc, String orderName, String key) {
		addUserAndShop(model);
		// 初始化分页信息 page
		Pageable pageable = initPage(page, size, orderName, isDesc);
		model.addAttribute("page", pageable);

		// 模糊查询匹配
		List<OrderVO> orders = null;
		Long total = 0L;
		if (!StringUtils.isBlank(key)) {
			key = key.trim();
			// 买家姓名 微信号 电话
			orders = orderService.listByStatus4SellerWithLike(status, pageable,
					key);
			total = orderService.countSellerOrdersByStatusWithLike(status, key);

		} else {
			orders = orderService.listByStatus4Seller(status, pageable);
			total = orderService.countSellerOrdersByStatus(status);
		}
		model.addAttribute("total", total);

		// 回填图片和地区
		setImgAndZones(orders.toArray(new OrderVO[orders.size()]));

		// 查询的状态是已付款的，则获取成功的订单总数目
		if (OrderStatus.PAID.equals(status) && orders.size() > 0) {
			OrderVO vo = orders.get(0);
			vo.setSeq(orderService.selectOrderSeqByShopId(vo.getShopId()));
		}

		model.addAttribute("orders", orders);

		model.addAttribute("status", status);
		OrderStatus[] list = OrderStatus.values();
		model.addAttribute("orderStatus",list);
		return "orders/order-list";
	}

	/*
	 * 封装page对象 sort结构示例 Iterator<org.springframework.data.domain.Sort.Order>
	 * orders = page.getSort().iterator(); if(orders.hasNext()){
	 * orders.next().getDirection(); orders.next().getProperty() }
	 */
	private Pageable initPage(Integer page, Integer size, String orderName,
			Boolean isDesc) {
		if (size == null) {
			size = 10;
		}

		if (page == null) {
			page = 0;
		}
		if (isDesc != null || !StringUtils.isBlank(orderName)) {
			Direction orderType = Direction.DESC;
			if (isDesc != null && !isDesc) {
				orderType = Direction.ASC;
			}

			return new PageRequest(page, size, orderType, orderName);
		}
		return new PageRequest(page, size);
	}

	// 订单详情
	@RequestMapping(value = "/orders/details")
	public String ordersDetails(@RequestParam String orderId, Model model,
			HttpServletRequest req) {
		OrderVO order = orderService.loadVO(orderId);
		if (order == null) {
			throw new BizException(GlobalErrorCode.NOT_FOUND,
					new RequestContext(req).getMessage("order.not.found"));
		}
		setLogisticsInfo(order);

		Shop shop = shopService.load(order.getShopId());
		if (shop != null) {
			order.setShopName(shop.getName());
		}
		// 设置图片和地区
		setImgAndZones(order);
		// 分佣
		setCommissionInfo(order);

		List<CouponInfoVO> orderCoupons = cashierService
				.loadCouponInfoByOrderNo(order.getOrderNo());
		order.setOrderCoupons(orderCoupons);

		List<OrderFeeVO> list = orderService.findOrderFees(order);
		OrderVOEx orderEx = new OrderVOEx(order, list);

		BigDecimal discountFee = orderEx.getDiscountFee();
		if (discountFee == null) {
			discountFee = BigDecimal.ZERO;
		}

		orderEx.setDiscountFee(discountFee.add(order.getHongbaoAmount()));
		orderEx.setDefDelayDate(defDelayDate);
		orderEx.setRefundableFee(orderEx.getTotalFee().subtract(
				orderEx.getLogisticsFee()));
		orderEx.setShowRefundBtn(false);

		// 快店IOS审核失败，暂时关闭买家端退款入口
		if (orderEx.getStatus() != OrderStatus.SUBMITTED
				&& orderEx.getStatus() != OrderStatus.SUCCESS
				&& orderEx.getStatus() != OrderStatus.CANCELLED) {
			if (orderEx.getStatus() == OrderStatus.CLOSED) {
				List<OrderRefund> refunds = orderRefundService
						.listByOrderId(order.getId());
				if (refunds.size() > 0) {
					orderEx.setShowRefundBtn(true);
				}
			} else {
				orderEx.setShowRefundBtn(true);
			}
		}

		model.addAttribute("order", orderEx);
		addUserAndShop(model);
		List<OrderMessage> messages = orderService
				.viewMessages(orderEx.getId());
		model.addAttribute("messages", messages);
		return "orders/order-detail";
	}

	/**
	 * 取消订单
	 */
	@ResponseBody
	@RequestMapping("/order/cancel")
	public Boolean cancelOrder(@RequestParam String orderId) {
		return orderService.cancel(orderId, getCurrentUser().getId());
	}

	/**
	 * 发货
	 */
	@ResponseBody
	@RequestMapping("/order/shipped")
	public Boolean shipped(@RequestParam String logisticsCompany,
			@RequestParam String logisticsOrderNo, @RequestParam String orderId) {
		orderService.ship(orderId, logisticsCompany, logisticsOrderNo);
		return true;
	}

	/**
	 * 导入订单,批量发货
	 */
	@ResponseBody
	@RequestMapping("/order/import")
	public String shipped(@RequestParam("excelFile") MultipartFile file)
			throws IOException {
		if (!file.isEmpty()) {			
			try {	
				String[][] excelData = ExcelUtils.excelImport(file.getInputStream());
				if (excelData == null || excelData.length == 0) {
					log.error("excelData is null");
					return "500";
				}	
				int succCount=1;
				int count = excelData.length;
				for (int i = 1; i < excelData.length; i++) {
					String orderNo = excelData[i][0].replace("\"", "");
					if (StringUtils.isBlank(orderNo)){
						count--;
						continue;
					}
					String logisticsCompany = excelData[i][1];
					String logisticsOrderNo = excelData[i][2];					
					try{
						OrderVO order = orderService.loadByOrderNo(orderNo);
						if (order != null && orderService.ship(order.getId(), logisticsCompany,logisticsOrderNo)){					 
							succCount++;
						}	
						else{
							succCount--;
						}
					}
					catch(Exception e){
						succCount--;
					}					
				}
				if(succCount==0||count==1)
					return "207";
				if(succCount!=count)
					return "206";				
				else
					return "200";
			} catch (Exception e) {
				log.error(e.getMessage());
				return "500";
			}
		} else
			return "500";
	}

	/**
	 * 订单导出
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/order/exportExcel")
	public void export2Excel(OrderSearchForm form, HttpServletResponse resp) {
		Map<String, Object> params = transForm2Map(form);
		List<OrderVO> orders = orderService.listOrders4Export(params);
		String sheetStr = "订单详情";
		String filePrefix = "orderList";
		String[] secondTitle = new String[] { "订单号", "销售渠道", "交易类型", "支付类型",
				"订单状态", "付款状态", "创建时间" };
		String[] strBody = new String[] { "getOrderNo", "getPartnerType",
				"getTypeStr", "getPayTypeStr", "getStatusStr", "getPaidStatus",
				"getCreatedAtStr" };
		excelService.export(filePrefix, orders, OrderVO.class, sheetStr,
				transParams2Title(params), secondTitle, strBody, resp);
	}

	private Map<String, Object> transForm2Map(OrderSearchForm form) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(form.getShopId_kwd())) {
			params.put("shopId", form.getShopId_kwd());
		}
		if(form.getStatus_kwd() != null) {
			params.put("status", form.getStatus_kwd());
		}
		
		return params;
	}
	
	private String transParams2Title(Map<String, Object> params){
		String result = "";
		if(params!=null){
			Iterator<String> it = params.keySet().iterator();
			int i=0;
			while(it.hasNext()){
				String key = it.next();
				Object value = params.get(key);
				if(value!=null&&!value.equals("")){
					if(i>0){
						result += ";";
					}
					result += "{"+key+"="+value.toString()+"}";
					i++;
				}
			}
		}
		return result;
	}

	/**
	 * 改价
	 */
	@RequestMapping("/order/update-price")
	@ResponseBody
	public Order updateOrderPrice(@RequestParam String orderId,
			String goodsFee, String logisticsFee) {
		if (StringUtils.isBlank(goodsFee) && StringUtils.isBlank(logisticsFee)) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					"邮费和商品费用不能都为0");
		}

		BigDecimal bGoodsFee = NumberUtils.createBigDecimal(goodsFee).setScale(
				2, BigDecimal.ROUND_DOWN);
		logisticsFee = StringUtils.defaultIfBlank(logisticsFee, "0");
		BigDecimal bLogisticsFee = NumberUtils.createBigDecimal(logisticsFee);
		// 商品价格不能改为0
		orderService.updatePrice(orderId, bGoodsFee, bLogisticsFee,
				getCurrentUser().getId());
		return orderService.load(orderId);
	}

	/**
	 * 修改邮费
	 */
	@RequestMapping(value = "/postAge")
	public String index(Principal principal, Model model) {
		addUserAndShop(model);
		return "settings/set-postage";
	}

	@RequestMapping(value = "/setShop")
	public String setShop(Model model) {
		addUserAndShop(model);
		return "settings/set-shop";
	}

	@RequestMapping(value = "/taobao-move")
	public String taobaoMove(Model model) {
		addUserAndShop(model);
		return "taobao-move";
	}

	// ===============================私有方法====================
	private void setImgAndZones(OrderVO... orders) {
		for (OrderVO order : orders) {
			// 订单图片
			String imgUrl = "";
			for (OrderItem item : order.getOrderItems()) {
				imgUrl = item.getProductImg();
				if (StringUtils.isBlank(imgUrl)) {
					continue;
				}

				imgUrl = resourceFacade.resolveUrl(imgUrl);
				// 解析图片
				item.setProductImgUrl(imgUrl);
			}
			order.setImgUrl(imgUrl);

			// 收货地址
			List<Zone> zoneList = zoneService.listParents(order
					.getOrderAddress().getZoneId());
			String addressDetails = "";
			for (Zone zone : zoneList) {
				addressDetails += zone.getName();
			}
			addressDetails += order.getOrderAddress().getStreet();
			order.setAddressDetails(addressDetails);
		}
	}

	// 回填分佣金信息
	private void setCommissionInfo(OrderVO order) {
		List<Commission> commissions = unionService
				.listByOrderId(order.getId());
		BigDecimal cmFee = BigDecimal.ZERO;
		for (Commission cm : commissions) {
			cmFee = cmFee.add(cm.getFee());
		}
		order.setCommissionFee(cmFee);
		if (order.getDiscountFee() == null) {
			order.setDiscountFee(BigDecimal.ZERO);
		}
		BigDecimal handingFee = orderService.loadTechServiceFee(order);
		if (!handingFee.equals(BigDecimal.ZERO)) {
			Formatter fmt = new Formatter();
			try {
				fmt.format(ORDERNOTES, serviceFeethreshold,
						handingFee.setScale(2, BigDecimal.ROUND_HALF_UP));
				order.setNotes(fmt.toString());
			} finally {
				fmt.close();
			}
		}

		order.setHongbaoAmount(handingFee);

	}

	/**
	 * 回填物流信息
	 * 
	 * @param order
	 */
	private void setLogisticsInfo(OrderVO order) {
		// 设置物流公司官网
		for (LogisticsCompany logisticsCompany : LogisticsCompany.values()) {
			if (logisticsCompany.getName().equals(order.getLogisticsCompany())) {
				order.setLogisticsOfficial(logisticsCompany.getUrl());
			}
		}

		// TODO 兼容老版本处理
		if ("顺丰".equals(order.getLogisticsCompany())
				|| "顺丰快递".equals(order.getLogisticsCompany())
				|| "SF_EXPRESS".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.sf-express.com/");
		} else if ("圆通".equals(order.getLogisticsCompany())
				|| "圆通快递".equals(order.getLogisticsCompany())
				|| "YTO".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.yto.net.cn/");
		} else if ("申通".equals(order.getLogisticsCompany())
				|| "STO".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.sto.cn/");
		} else if ("中通".equals(order.getLogisticsCompany())
				|| "ZTO".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.zto.cn/");
		} else if ("百世汇通".equals(order.getLogisticsCompany())
				|| "BESTEX".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.htky365.com/");
		} else if ("韵达".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.yundaex.com/");
		} else if ("天天".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.ttkdex.com/");
		} else if ("全峰".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.qfkd.com.cn/");
		} else if ("邮政EMS".equals(order.getLogisticsCompany())
				|| "中国邮政".equals(order.getLogisticsCompany())) {
			order.setLogisticsOfficial("http://www.ems.com.cn/");
		} else {
			order.setLogisticsOfficial("");
		}
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


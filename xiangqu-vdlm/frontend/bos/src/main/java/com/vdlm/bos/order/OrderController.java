package com.vdlm.bos.order;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.mapper.OrderRefundMapper;
import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.status.OrderRefundStatus;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.vo.OrderExportVO;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.excel.ExcelService;
import com.vdlm.service.order.OrderAddressService;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.zone.ZoneService;

@Controller
public class OrderController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRefundService orderRefundService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private OrderAddressService orderAddressService;
	
	@Autowired
	private OrderRefundMapper orderRefundMapper;
	
	@Autowired
	private CashierService cashierService;
	
	@Value("${site.web.host.name}")
	String siteHost;
	
	@RequestMapping(value = "order")
	public String list(Model model, HttpServletRequest req) {
		model.addAttribute("siteHost", siteHost);
		return "order/orders";
	}
	
	@ResponseBody
	@RequestMapping(value = "order/list")
	public Map<String, Object> list(OrderSearchForm form, Pageable pageable) {
		
		Map<String, Object> params = transForm2Map(form);	
		List<OrderVO> orders = orderService.listByAdmin(params, pageable);
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> countMap = orderService.countMapByAdmin(params);
		data.put("total", countMap.get("totalCount"));
		data.put("rows", orders);
		
		BigDecimal pageTotalFee = new BigDecimal(0);
		BigDecimal pagePaidFee = new BigDecimal(0);
		for (int i = 0; i < orders.size(); i++) {
			OrderVO vo = orders.get(i);
			if(vo.getTotalFee() != null)
				pageTotalFee = pageTotalFee.add(vo.getTotalFee());
			if(vo.getPaidFee() != null)
				pagePaidFee = pagePaidFee.add(vo.getPaidFee());
		}
		
		List<OrderVO> footer = new ArrayList<OrderVO>();
		OrderVO pageSum = new OrderVO();
		pageSum.setOrderNo("当前页金额");
		pageSum.setTotalFee(pageTotalFee);
		pageSum.setPaidFee(pagePaidFee);
		footer.add(pageSum);
		
		String totalAmount_TotalFee = countMap.get("totalAmount_totalfee") == null ? "0"
				: countMap.get("totalAmount_totalfee").toString();
		String totalAmount_PaidFee = countMap.get("totalAmount_paidfee") == null ? "0"
				: countMap.get("totalAmount_paidfee").toString();
		OrderVO totalSum = new OrderVO();
		totalSum.setOrderNo("总计金额");
		totalSum.setTotalFee(new BigDecimal(totalAmount_TotalFee));
		totalSum.setPaidFee(new BigDecimal(totalAmount_PaidFee));
		footer.add(totalSum);
		
		data.put("footer", footer);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "order/list/exportExcel")
	public void export2Excel(OrderSearchForm form, HttpServletResponse resp){
		Map<String, Object> params = transForm2Map(form);
		List<OrderVO> orders = orderService.listByAdmin4Export(params, null);
		Map<String,String> addressMap = zoneService.listAllAddressExceptStreet();
		List<OrderExportVO> exports = new LinkedList<OrderExportVO>();
		OrderExportVO exportVO = null;
		int i;
		for(OrderVO vo : orders){
			if(vo.getOrderAddress() != null){
				vo.setAddressDetails(addressMap.get(vo.getOrderAddress().getZoneId()) + vo.getOrderAddress().getStreet());
			}
			i=0;
			for(OrderItem item : vo.getOrderItems()){
				if(i==0){
					exportVO = new OrderExportVO(vo);
				}else{
					exportVO = new OrderExportVO();
					exportVO.setIdLong(vo.getIdLong());
					exportVO.setOrderNo(vo.getOrderNo());
				}
				exportVO.setProductName(item.getProductName());
				exportVO.setProductPrice(item.getPrice());
				exportVO.setProductAmount(item.getAmount());
				exportVO.setSkuStr(item.getSkuStr());
				exports.add(exportVO);
				i++;
			}
		}
		String sheetStr = "用户明细";
		String filePrefix = "orderList";
		String[] secondTitle = new String[]{"订单id","订单号","付款单号","销售渠道","外部交易号","交易类型","支付类型","订单状态","付款状态","店铺","买家","卖家","订单总额","商品总额","物流金额","折扣金额","付款总额","付款时间","物流公司","物流单号","创建时间","更新时间","对账时间","订单备注", "退款金额(买家)","平台退款(红包)","退款状态","申请退款时间","处理退款时间","收货地址","收货人姓名","商品名称","商品单价","商品数量","商品型号"};
		String[] strBody = new String[]{"getIdLong","getOrderNo","getPayNo","getPartnerType","getOutTradeNo","getTypeStr","getPayTypeStr","getStatusStr","getPaidStatus","getShopName","getBuyerPhone","getSellerPhone","getTotalFee","getGoodsFee","getLogisticsFee","getDiscountFee","getPaidFee","getPaidAtStr","getLogisticsCompany","getLogisticsOrderNo","getCreatedAtStr","getUpdatedAtStr","getCheckingAtStr", "getRemarkStr","getRefundFee","getRefundPlatformFee","getRefundType","getRefundAtStr","getDoRefundAtStr","getAddressDetails","getConsignee","getProductName","getProductPrice","getProductAmount","getSkuStr"};
		excelService.export(filePrefix, exports, OrderExportVO.class, sheetStr, transParams2Title(params), secondTitle, strBody, resp);
	}
	
	private Map<String, Object> transForm2Map(OrderSearchForm form){
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(form.getOrderNo_kwd())){
			params.put("orderNo", "%" + form.getOrderNo_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getBuyerPhone_kwd())){
			params.put("buyerPhone", "%" + form.getBuyerPhone_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getSellerPhone_kwd())){
			params.put("sellerPhone", "%" + form.getSellerPhone_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getShopName_kwd())){
			params.put("shopName", "%" + form.getShopName_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getLogisticsOrderNo_kwd())){
			params.put("logisticsOrderNo", "%" + form.getLogisticsOrderNo_kwd() + "%" );
		}
		params.put("type", form.getType_kwd());
		params.put("payType", form.getPayType_kwd());
		params.put("paidStatus", form.getPaidStatus_kwd());
		params.put("refundType", form.getRefundType_kwd());
		//订单状态多选
		if(form.getStatus_kwd()!=null&&form.getStatus_kwd().length>0){
			params.put("status", form.getStatus_kwd());
		}
		params.put("partner", StringUtils.defaultIfEmpty(form.getPartner_kwd(), null));
		
		params.put("dateType", form.getDateTypeKwd());
		
		if (StringUtils.isNotBlank(form.getStartDateKwd())) {
			params.put("startDate", form.getStartDateKwd());
		}
		if (StringUtils.isNotBlank(form.getEndDateKwd())) {
			 try {
				Date date = DateUtils.addDays(DateUtils.parseDate(form.getEndDateKwd(), "yyyy-MM-dd"), 1);
				params.put("endDate", date);
			} catch (ParseException e) {
			
			}
		}
		if (form.getMinimum_fee() != null && form.getMinimum_fee().compareTo(BigDecimal.ZERO) >= 0 
				&& StringUtils.isNotBlank(form.getFee_operator())) {
			params.put("feeOperator", form.getFee_operator());
			params.put("minimumFee", form.getMinimum_fee());
		}
		if (form.getRefund_fee() != null && form.getRefund_fee().compareTo(BigDecimal.ZERO) >= 0 
				&& StringUtils.isNotBlank(form.getRefundFee_operator())) {
			params.put("refundFeeOperator", form.getRefundFee_operator());
			params.put("refundFee", form.getRefund_fee());
		}
		if(StringUtils.isNotBlank(form.getShipped_status())){
			params.put("shippedStatus", form.getShipped_status());
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
	 * 卖家发起退款，由运营代为退款操作
	 * @param orderId
	 * @param refundment
	 * @param logistics 运费
	 * @return
	 */
	@ResponseBody
	@RequestMapping("order/refund/seller")
	public Json refund(String orderId, String refundment, String logistics) {
		Json json = new Json();
		BigDecimal refundFee = new BigDecimal(refundment).setScale(2, BigDecimal.ROUND_DOWN);
		BigDecimal logisticsFee = new BigDecimal(logistics).setScale(2, BigDecimal.ROUND_DOWN);
		
		try{
		    //orderService.refund(orderId, refundFee, logisticsFee);
		    orderService.refundFee(orderId, refundFee, logisticsFee);
		    json.setMsg("操作成功");
	    }catch(Exception e){
	    	json.setRc(Json.RC_FAILURE);
	    	json.setMsg(e.getMessage());
	    	log.error("运营代为退款操作出错",e);
	    }
		return json;
	}
	
	/**
	 * 查看订单详细信息
	 * @param orderId
	 * @param refundment
	 * @return
	 */
	@ResponseBody
	@RequestMapping("order/{orderNo}")
	public Json view(@PathVariable("orderNo")String orderNo) {
		Json json = new Json();
		try{
		    OrderVO order = orderService.loadByOrderNo(orderNo);
		    //TODO 支付渠道
		    String paymentChannel = orderService.obtainPaymentChannel(orderNo);
		    order.setPaymentChannel(paymentChannel);

			String addressDetails = "";
			if(order.getOrderAddress() != null) {
				addressDetails = zoneService.genFullAddr(order.getOrderAddress().getZoneId(), order.getOrderAddress().getStreet());
			}
		    order.setAddressDetails(addressDetails);
		    json.setObj(order);
	    }catch(Exception e){
	    	json.setRc(Json.RC_FAILURE);
	    	json.setMsg("订单号不存在或者获取订单信息时发生错误。" +e.getMessage());
	    }
		return json;
	}
	
	/**
	 * 卖家发起退款，由运营代为退款操作
	 * @param orderId
	 * @param refundment
	 * @return
	 */
	@ResponseBody
	@RequestMapping("order/payDetail/{orderNo}")
	public Json viewPay(@PathVariable("orderNo")String orderNo) {
		Json json = new Json();
		try{
		    OrderVO order = orderService.loadByOrderNo(orderNo);
		    
		    List<CashierItem> cashierItems = cashierService.listByBizNo(order.getPayNo());
		    
		    json.setObj(cashierItems);
	    }catch(Exception e){
	    	json.setRc(Json.RC_FAILURE);
	    	json.setMsg("订单号不存在或者获取订单信息时发生错误。" +e.getMessage());
	    }
		return json;
	}
	
	/**
	 * 运营退款成功
	 * @param orderId
	 * @return
	 */
	/*@ResponseBody
	@RequestMapping(value = "order/refund/accept")
	public Boolean accept(String orderId) {
		System.out.println(orderId+"orderId");
		List<String> orderIds=new ArrayList<String>();
		if(orderId.indexOf(",")>-1){
		  String[] orderIdArr=orderId.split(",");
			for(String id:orderIdArr){
				orderIds.add(id);
			}
		}else orderIds.add(orderId);
		orderService.updateOrderRefundByAdmin(orderIds);
		log.info(super.getCurrentUser().getId() + "accept refund orderId=["+orderId + "]");
		return true;
	}*/
	
	@ResponseBody
	@RequestMapping(value = "order/refund/accept")
	public Boolean accept(String refundId) {
		OrderRefund orderRefund =orderRefundService.load(refundId);
		if(OrderRefundStatus.SUCCESS !=orderRefund.getStatus()){
			return false;
		}
		orderService.endOrderRefundByAdmin(refundId);		
		log.info(super.getCurrentUser().getId() + "accept refund refundId=["+refundId + "]");
		return true;
	}
	
	/**
	 * 管理端编辑订单备注
	 */
	@ResponseBody
	@RequestMapping(value = "order/updRemark")
	public Json updRemark(String id, String remark) {
		Json json = new Json();
		try {
			if(orderService.updRemarkByAdmin(id, remark) == 1)
				json.setMsg("保存成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("保存失败");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("保存失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + "订单id=[" + id + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
	
	
	    @ResponseBody
	    @RequestMapping("/order/cancel")
	    public  Json cancelOrder(@RequestParam String orderNo){
		    Json json=new Json();	 
		   try{
			Order order=orderService.loadByOrderNo(orderNo);
			   if(order==null) throw new Exception("订单为找到：orderNo:"+orderNo);
		    	orderService.sysCancel(order.getId());
		    	json.setRc(Json.RC_SUCCESS);
		    	json.setMsg("操作成功");
		   }catch(Exception e){
			  json.setRc(Json.RC_FAILURE);
			  json.setMsg("操作失败;" + e.getMessage());
		   }
	    	return json;
	    }
}

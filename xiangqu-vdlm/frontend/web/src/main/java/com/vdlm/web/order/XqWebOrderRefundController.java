package com.vdlm.web.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.OrderRefundStatus;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.type.OrderRefundActionType;
import com.vdlm.dal.vo.OrderRefundVO;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.web.BaseController;
import com.vdlm.web.order.vo.LogisticsInfoVO;
import com.vdlm.web.vo.Json;

@Controller
@RequestMapping("/xiangqu/web")
public class XqWebOrderRefundController extends BaseController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Autowired
	private OrderRefundService orderRefundService;
	
	@Value("${xiangqu.web.site}")
	private String xiangquWebSite;
	
	@Value("${xiangqu.web.head.url}")
    private String xiangquWebHeadUrl;

    @Value("${xiangqu.web.foot.url}")
    private String xiangquWebFootUrl;
	
	@RequestMapping(value = "/order/refund/process")
	public String process() {
		return "xiangqu/web/order/refund/process";
	}
	
	@RequestMapping(value = "/order/refund/toChoose")
	public String toChoose(String orderId, String id, Model model) {
		OrderRefund orderRefund = null;
		OrderStatus orderStatus = null;
		if(StringUtils.isNotBlank(id)){
			orderRefund = orderRefundService.load(id);
			orderId = orderRefund.getOrderId();
			orderStatus = orderRefund.getOrderStatus();
		}
		OrderVO order = orderRefundService.loadOrderVO(orderId, orderRefund==null?"":orderRefund.getId());
		List<OrderItem> items = orderService.listOrderItems(orderId);
		order.setOrderItems(items);
		order.setStatus(orderStatus==null?order.getStatus():orderStatus);
        String imgUrl = "";
        for (OrderItem item : order.getOrderItems()) {
            imgUrl = item.getProductImg();
            item.setProductImgUrl(resourceFacade.resolveUrl(imgUrl));
        }
		model.addAttribute("order", order);
		model.addAttribute("orderRefund", orderRefund);
		model.addAttribute("refundReasons", orderRefundService.loadRefundReasonDict(order.getStatus()));
		model.addAttribute("xiangquWebSite", xiangquWebSite);
		model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
		model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
		return "xiangqu/web/order/refund/choose";
	}
	
	@RequestMapping(value = "/order/refund/toRequest")
	public String toRequest(String orderId, String id, Integer buyerRequire, Model model) {
		OrderRefund orderRefund = null;
		OrderStatus orderStatus = null;
		if(StringUtils.isNotBlank(id)){
			orderRefund = orderRefundService.load(id);
			orderId = orderRefund.getOrderId();
			orderStatus = orderRefund.getOrderStatus();
		}else{
			orderRefund = new OrderRefund();
		}
		orderRefund.setBuyerRequire(buyerRequire);
		OrderVO order = orderRefundService.loadOrderVO(orderId, orderRefund==null?"":orderRefund.getId());
		order.setStatus(orderStatus==null?order.getStatus():orderStatus);
		model.addAttribute("order", order);
		model.addAttribute("orderRefund", orderRefund);
		model.addAttribute("refundReasons", orderRefundService.loadRefundReasonDict(order.getStatus()));
		model.addAttribute("xiangquWebSite", xiangquWebSite);
		model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
		model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
		return "xiangqu/web/order/refund/request";
	}
	
	@RequestMapping(value = "/order/refund/toShip/{id}")
	public String toShip(@PathVariable("id") String id, Model model) {
		OrderRefund orderRefund = orderRefundService.load(id);
		
		List<LogisticsInfoVO> aList = new ArrayList<LogisticsInfoVO>();
		for (LogisticsCompany aLC : LogisticsCompany.values())  {
			LogisticsInfoVO avo = new LogisticsInfoVO();
			if ( ! aLC.equals(LogisticsCompany.OTHER)) {
				avo.setCompany(aLC.toString());
				avo.setCompanyName(aLC.getName());
				aList.add(avo);
			}
		}
		
		model.addAttribute("logisticsCompanys", aList);
		model.addAttribute("orderRefund", orderRefund);
		model.addAttribute("xiangquWebSite", xiangquWebSite);
		model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
		model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
		return "xiangqu/web/order/refund/ship";
	}
	
	@ResponseBody
	@RequestMapping(value = "/order/refund/ship")
	public Json ship(OrderRefundShipForm form, Model model) {
		Json json = new Json();
		try{
			OrderRefund orderRefund = new OrderRefund();
			if(StringUtils.isBlank(form.getLogisticsCompany()) || StringUtils.isBlank(form.getLogisticsNo())){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请填写完整的物流信息！");
			}
			
			if(form.getLogisticsNo().length() > 30){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "运单编号不能大于30个字！");
			}
			
			BeanUtils.copyProperties(form, orderRefund);
//			orderRefund.setOrderId(form.getOrderId());
			orderRefundService.execute(OrderRefundActionType.SHIP, orderRefund, null);
			json.setMsg("操作成功");
		}catch(Exception e){
			json.setRc(Json.RC_FAILURE);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/order/refund/cancel")
	public Json cancel(String id, Model model) {
		Json json = new Json();
		try{
			OrderRefund orderRefund = orderRefundService.load(id);
			orderRefundService.execute(OrderRefundActionType.CANCEL, orderRefund, null);
			json.setMsg("操作成功");
		}catch(Exception e){
			json.setRc(Json.RC_FAILURE);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "/order/refund/request")
	public Json request(OrderRefundRequestForm form, Model model) {
		Json json = new Json();
		try{
			OrderVO order = orderRefundService.loadOrderVO(form.getOrderId(), form.getId());
			if(StringUtils.isBlank(form.getRefundMemo()) || StringUtils.isBlank(form.getRefundFee())
					|| StringUtils.isBlank(form.getRefundReason())){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款原因、退款金额、退款说明不能为空！");
			}
			
			if(form.getRefundMemo().length() > 100){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款说明的字数不能大于100个字！");
			}
			
			if(order.getStatus() == OrderStatus.PAID){
				form.setBuyerRequire(1);
			}else if(form.getBuyerRequire() == null){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "买家申请退货服务类别不能为空！");
			}else if(form.getBuyerRequire()==2 && form.getBuyerReceived() == null){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "买家申请退货服务、是否收到货不能为空！");
			}
			
			OrderRefund orderRefund = new OrderRefund();
			BeanUtils.copyProperties(form, orderRefund);
			try{
				BigDecimal refundFee = new BigDecimal(form.getRefundFee()).setScale(2, BigDecimal.ROUND_DOWN);
				orderRefund.setRefundFee(refundFee);
			}catch(Exception e){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款金额格式不正确！");
			}
			if(BigDecimal.ZERO.compareTo(orderRefund.getRefundFee())>-1){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款金额请大于零(仅保留小数点后两位)！");
			}
			orderRefundService.submit(orderRefund);
			json.setMsg("提交成功！");
			json.setRc(Json.RC_SUCCESS);
			json.setObj(orderRefund.getId());
		}catch(Exception e){
			json.setRc(Json.RC_FAILURE);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value = "/order/refund/{id}")
	public String view(@PathVariable("id") String orderId,Long continueRefund, Model model) {
		Order order = orderService.load(orderId);
		User user = getCurrentUser();
		if(!order.getBuyerId().equals(getCurrentUser().getId()) &&
				!order.getSellerId().equals(getCurrentUser().getId())	){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "无权限查看该订单" + user.getId());
		}
		
		List<OrderRefund> refunds = orderRefundService.listByOrderId(orderId);
		
		if(null != refunds &&refunds.size() == 1){
			//Add By Anmukai:获取之前已经退款的订单
			Long canRefund = orderRefundService.countCanRefundById(refunds.get(0).getId());
			if(continueRefund!=null&&continueRefund.longValue()>0&&canRefund.longValue()>0){
				return toChoose(order.getId(), null, model);
			}
			List<OrderRefund> refundeds = orderRefundService.listRefundedByOrderId(orderId);
		
			if(null != refundeds && refundeds.size()>0){
				List<OrderRefundVO> orderRefundVOs =new ArrayList<OrderRefundVO>();
				for(OrderRefund orderRefund:refundeds){
					OrderRefundVO orderRefundVO = new OrderRefundVO();
					BeanUtils.copyProperties(orderRefund, orderRefundVO);
					orderRefundVO.setOpDetails(orderRefundService.initOpDetail(orderRefundVO, "2"));
					orderRefundVOs.add(orderRefundVO);
				}
				model.addAttribute("orderRefunded", orderRefundVOs);
			}
			//判断此订单是否还可以继续退款
			
			if(canRefund.longValue()>0){
				model.addAttribute("continueRefund", canRefund);
			}
			
			OrderRefundVO orderRefund = orderRefundService.loadVO(refunds.get(0).getId());
			orderRefund.setOpDetails(orderRefundService.initOpDetail(orderRefund, "2"));
			model.addAttribute("orderRefund", orderRefund);
			model.addAttribute("order", order);
			model.addAttribute("xiangquWebSite", xiangquWebSite);
			model.addAttribute("xiangquWebHeadUrl", xiangquWebHeadUrl);
			model.addAttribute("xiangquWebFootUrl", xiangquWebFootUrl);
//			return "redirect:/order/" + orderId;
			if(orderRefund.getStatus() == OrderRefundStatus.AGREE_RETURN
					|| orderRefund.getStatus() == OrderRefundStatus.RETURN_ING){
				//同意退货、退货中
				return "redirect:/xiangqu/web/order/refund/toShip/"+orderRefund.getId();
			}else if(orderRefund.getStatus() == OrderRefundStatus.REJECT_RETURN 
					|| orderRefund.getStatus() == OrderRefundStatus.REJECT_REFUND
					|| orderRefund.getStatus() == OrderRefundStatus.RETURN_DONE
					|| orderRefund.getStatus() == OrderRefundStatus.SUCCESS
					|| orderRefund.getStatus() == OrderRefundStatus.END
					|| orderRefund.getStatus() == OrderRefundStatus.COMPLETE
					|| orderRefund.getStatus() == OrderRefundStatus.CLOSE){
				//other
				return "xiangqu/web/order/refund/process";
			}else{
				//submitted
				model.addAttribute("orderId", order.getId());
				model.addAttribute("id", orderRefund.getId());
				model.addAttribute("buyerRequire", orderRefund.getBuyerRequire());
				return "redirect:/xiangqu/web/order/refund/toRequest";
			}
		}
		
		return toChoose(order.getId(), null, model);
	}
	
	
	
}

package com.vdlm.bos.activityOrder;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.msg.PushMessageService;
import com.vdlm.bos.BaseController;
import com.vdlm.dal.model.ActivityOrder;
import com.vdlm.dal.model.Message;
import com.vdlm.dal.model.PushMessage;
import com.vdlm.dal.model.User;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.type.PushMsgId;
import com.vdlm.dal.type.PushMsgType;
import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.vo.ThirdExtPushData;
import com.vdlm.service.activityOrder.ActivityOrderService;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.msg.MessageService;
import com.vdlm.service.order.OrderMessageService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.user.UserService;

@Controller
public class ActivityOrderController extends BaseController{
	
	private static final String activityId ="141111";
	
	@Autowired
	private ActivityOrderService activityOrderService;
	
	@Autowired
	private PushMessageService pushMessageService; 
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderMessageService orderMessageService;
	
	@Autowired
	private CashierService cashierService;
	
	@Value("${xiangqu.web.site}")
	private String xqDomain;
	
	@RequestMapping(value = "activityOrder")
	public String list(Model model, HttpServletRequest req) {
		return "activityOrder/activityOrders";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/list")
	public Map<String, Object> list(ActivityOrderSearchForm form, Pageable pageable){
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(form.getOrder_no_kwd())){
			params.put("orderNo", "%" + form.getOrder_no_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getSeller_phone_kwd())){
			params.put("sellerPhone", form.getSeller_phone_kwd());
		}
		if(StringUtils.isNotBlank(form.getBuyer_phone_kwd())){
			params.put("buyerPhone", form.getBuyer_phone_kwd());
		}
		params.put("status", form.getStatus_kwd());
		List<ActivityOrder> list = null;
		int count = activityOrderService.countByAdmin(params);
		if(count>0){
			list = activityOrderService.listByAdmin(params, pageable);
		}else{
			list = new ArrayList<ActivityOrder>();
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", count);
		data.put("rows", list);
		
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/insertRandom")
	public boolean insertRandom(){
		int rec = activityOrderService.insertRandom(activityId, UserPartnerType.XIANGQU.toString()); // TODO: 不要写死
		return rec==1;
	}
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/insertDirectByOrderNo")
	public boolean insertDirectByOrderNo(String orderNo){
		OrderVO aVo = orderService.loadByOrderNo(orderNo);
		if (aVo!=null && aVo.getPartnerType().equals(UserPartnerType.XIANGQU)) {
			int rec = activityOrderService.insertDirectByOrderNo(activityId, orderNo);
			return rec==1;
		}
		return false;
	}
	
	private PushMessage createMsg(ActivityOrder aAO) {
		Message message = messageService.loadMessage(IdTypeHandler.encode(PushMsgId.FreeOrder_XQ.getId()));
		if (message != null) {
			PushMessage msg = new PushMessage();
			Formatter fmt = new Formatter();
			try {
				fmt.format(message.getContent(), cashierService.loadPaidFee(aAO.getOrderNo()).toString());
				message.setContent(fmt.toString());
			} finally {
				fmt.close();
			}
			User user = userService.load(IdTypeHandler.encode(aAO.getBuyerId()));
			if (user != null && user.getPartner() != null && user.getPartner().equalsIgnoreCase("xiangqu") && StringUtils.isNotEmpty(user.getExtUserId()) )  {
				msg.setApp("hotshop");
				msg.setBaiduTagName(user.getExtUserId());
				msg.setUserId(user.getExtUserId());
				msg.setTitle(message.getTitle());
				msg.setDesc(message.getContent());
				msg.setMsgType(PushMsgType.MSG_ACTIVITY_FREE_B.getValue());
				msg.setDetailUrl(xqDomain + "/m/1111/rules");
				ThirdExtPushData data = new ThirdExtPushData();
				data.setActiveId(aAO.getActivityId());
				data.setPrice(aAO.getTotalFee());
				msg.setExtData(data);
				return msg;
			}
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/comfirmed")
	public boolean comfirmed(String id){
		int rec = activityOrderService.updateStatusConfirmed(id);
		ActivityOrder aAO = activityOrderService.load(id);
		if (aAO != null) {
			PushMessage msg = createMsg(aAO);
			if (msg != null) {
				pushMessageService.pushToThirdServ(msg);
			}
			OrderVO orderVO = orderService.loadByOrderNo(aAO.getOrderNo());
			if (orderVO.getStatus().equals(OrderStatus.PAID) ||
				orderVO.getStatus().equals(OrderStatus.SHIPPED) ||
				orderVO.getStatus().equals(OrderStatus.SUCCESS)) {
				// TODO: 先写死'想去', 后面做可配置化
				orderMessageService.sendBuyerSMSOrderFree(orderVO, UserPartnerType.XIANGQU);
			} else {
				return false;
			}
		}
		return rec==1;
	}
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/finish")
	public boolean finish(String id){
		int rec = activityOrderService.updateStatusFinish(id);
		ActivityOrder aAO = activityOrderService.load(id);
		if (aAO != null) {
			OrderVO orderVO = orderService.loadByOrderNo(aAO.getOrderNo());
			// TODO: 先写死'想去', 后面做可配置化
			orderMessageService.sendBuyerSMSOrderFreeFinish(orderVO, UserPartnerType.XIANGQU);
		}
		return rec==1;
	}
	
	@ResponseBody
	@RequestMapping(value = "activityOrder/cancel")
	public boolean cancel(String id){
		int rec = activityOrderService.updateStatusCancel(id);
		return rec==1;
	}
}

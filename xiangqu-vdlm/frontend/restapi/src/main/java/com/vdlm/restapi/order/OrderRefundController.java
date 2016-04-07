package com.vdlm.restapi.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.status.OrderRefundStatus;
import com.vdlm.dal.type.OrderRefundActionType;
import com.vdlm.dal.vo.OrderRefundVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.utils.MD5Util;

@Controller
public class OrderRefundController extends BaseController {

	@Autowired
	private OrderRefundService orderRefundService;
	
	/**
	 * 我的退货请求列表
	 * @param pageable
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/order/refund/list")
	public ResponseObject<List<OrderRefundVO>> list(Pageable pageable) {
		List<OrderRefundVO> vos = orderRefundService.list(getCurrentUser().getId(), pageable);
		return new ResponseObject<List<OrderRefundVO>>(vos);
	}
	
	/**
	 * 检查退货申请是否是最新
	 * @param requestData
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/order/refund/checkRequestData")
	public ResponseObject<Boolean> checkRequestData(String requestData, String id) {
		return new ResponseObject<Boolean>(validation(requestData, id));
	}
	
	/**
	 * 同意退货退款
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/order/refund/confirm")
	public ResponseObject<OrderRefundVO> confirm(OrderRefundConfirmForm form) {
		log.info("begin /order/refund/confirm");
		
		if(!validation(form.getRequestData(), form.getId())){
			log.debug("申请退货退款的申请[ "+form.getId()+" ]已经被修改");
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT_2, "买家已经修改了退款申请，请返回查看");
		}
		
		// 获取订单退货信息
		OrderRefundVO refundInfo = orderRefundService.loadVO(form.getId());
		if(OrderRefundStatus.SUBMITTED != refundInfo.getStatus()){
			log.debug("申请退货退款的申请[ "+form.getId()+" ]还未提交 refund status=[" + refundInfo.getStatus() +  "]");
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款申请买家未提交");
		}
		
		
		Map<String, Object> params =null;
		OrderRefundActionType action=null;
		if("1".equals(form.getAgree())){
			// 同意订单申请
			if (refundInfo.getBuyerRequire().intValue() == 2) { 
				// 买家要求是 退货并退款
				if(StringUtils.isBlank(form.getReturnAddress()) || StringUtils.isBlank(form.getReturnName())
						|| StringUtils.isBlank(form.getReturnPhone())){
					log.debug("申请退货退款的申请[ "+form.getId()+" ]的收货人信息不完整");
					throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "收货人姓名、联系方式、退货地址不能为空");
				}
			}
			
			refundInfo.setReturnAddress(form.getReturnAddress());
			refundInfo.setReturnName(form.getReturnName());
			refundInfo.setReturnPhone(form.getReturnPhone());
			refundInfo.setReturnMemo(form.getReturnMemo());
			action=OrderRefundActionType.CONFIRM;
		} else {
			// 拒绝申请
			if(StringUtils.isBlank(form.getRefuseReason()) || StringUtils.isBlank(form.getRefuseDetail())
					|| form.getRefuseEvidences() == null){
				log.debug("申请退货退款的申请[ "+form.getId()+" ]的拒绝申请的理由不完整");
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "拒绝原因、拒绝说明、凭证都不能为空");
			}
			
			refundInfo.setStatus(OrderRefundStatus.REJECT_REFUND);
			refundInfo.setRefuseReason(form.getRefuseReason());
			refundInfo.setRefuseDetail(form.getRefuseDetail());
			refundInfo.setRefuseEvidences(form.getRefuseEvidences());
			action = OrderRefundActionType.REJECT;
			
			params = new HashMap<String, Object>();
			params.put("attachs", form.getRefuseEvidences());
		}
		
		orderRefundService.execute(action, refundInfo, params);
		
		refundInfo = orderRefundService.loadVO(refundInfo.getId());
		refundInfo.setOpDetails(orderRefundService.initOpDetail(refundInfo, "3"));
		return new ResponseObject<OrderRefundVO>(refundInfo);
	}
	
	/**
	 * 
	 * 被拒绝的退货申请标记成功
	 * @param form
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/order/refund/confirmSign")
	public ResponseObject<Boolean> confirmSign(OrderRefundConfirmForm form) {
		// 获取申请
		OrderRefundVO refundVO = orderRefundService.loadVO(form.getId());
		
		// 申请未被拒绝
		if(refundVO.getStatus() != OrderRefundStatus.RETURN_ING){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "退款状态有误");
		}
		
		OrderRefundActionType action=null;
		if(form.getAgree().equals("1"))
			 action=OrderRefundActionType.SIGN;
		else
			action=OrderRefundActionType.REJECT_SIGN;
		
		orderRefundService.execute(action, refundVO, null);
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 退货订单详情
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/order/refund/detail")
	public ResponseObject<OrderRefundVOEx> detail(@RequestParam("id") String id) { 
		OrderRefundVO refundVO = orderRefundService.loadVO(id);
		OrderRefundVOEx voEx = new OrderRefundVOEx(refundVO);
		voEx.setRequestData(MD5Util.MD5Encode(refundVO.toString(), "UTF-8"));
		voEx.setOpDetails(orderRefundService.initOpDetail(refundVO, "3"));
		return new ResponseObject<OrderRefundVOEx>(voEx);
	}
	
	
	//============私有方法===================
	/**
	 * 验证退货信息是否与数据库中信息一致，是否是最新的退款申请
	 * @param requestData
	 * @param id
	 * @return
	 */
	private boolean validation(String requestData, String id){
		// 获取申请退货订单信息
		OrderRefund orderRefund = orderRefundService.load(id);
		String validationData = MD5Util.MD5Encode(orderRefund.toString(), "UTF-8");
		return validationData.equals(requestData);
	}
}

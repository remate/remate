package com.vdlm.bos.payBatch;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.vdlm.dal.mapper.OrderRefundMapper;
import com.vdlm.dal.mapper.PaymentMerchantMapper;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.model.OrderRefund;
import com.vdlm.dal.model.OutPay;
import com.vdlm.dal.model.PayRequest;
import com.vdlm.dal.model.SubAccount;
import com.vdlm.dal.model.UserAlipay;
import com.vdlm.dal.model.WithdrawApply;
import com.vdlm.dal.status.OrderRefundStatus;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.status.WithdrawApplyStatus;
import com.vdlm.dal.type.AccountType;
import com.vdlm.dal.type.PayRequestBizType;
import com.vdlm.dal.type.PayRequestPayType;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.vo.OrderRefundVO;
import com.vdlm.service.account.AccountApiService;
import com.vdlm.service.alipay.UserAlipayService;
import com.vdlm.service.bank.WithdrawApplyService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderRefundService;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.outpay.OutPayLogs;
import com.vdlm.service.outpay.PaymentRefundRequestVO;
import com.vdlm.service.outpay.ReceiverDetailVO;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.outpay.impl.AliPaymentImpl;
import com.vdlm.service.pay.OutPayService;
import com.vdlm.service.pay.PayRequestApiService;
import com.vdlm.service.pay.PayRequestService;
import com.vdlm.service.user.UserService;
import com.vdlm.utils.UniqueNoUtils;
import com.vdlm.utils.UniqueNoUtils.UniqueNoType;

@Controller
public class PaymentBatchController {

	Logger log = LoggerFactory.getLogger(PaymentBatchController.class);
	private Logger alipayLogger = OutPayLogs.alipayLogger;

	@Autowired
	private UserAlipayService userAlipayService;
	
	@Autowired
	private UserService userService;
	
//	// 签名方式，选择项：0001(RSA)、MD5
//	@Value("${payment.merchant.sign_type.alipay}")
//	String sign_type;
//	
//	@Value("${payment.merchant.secret.alipay}")
//	String secret;
//	
//	@Value("${payment.merchant.id.alipay}")
//	String partner;
//	@Value("${payment.merchant.mail.alipay}")
//	String partnerMail;
//	@Value("${payment.merchant.userName.alipay}")
//	String partnerUserName;
	
	@Autowired
	private ThirdPartyPayment aliPayment;

	@Autowired
	private ThirdPartyPayment tenPayment;

	@Autowired
	private ThirdPartyPayment umPayment;

	@Autowired
	private WithdrawApplyService withdrawApplyService;

	@Autowired
	private AccountApiService accountApiService;

	@Autowired
	private PayRequestApiService payRequestApiService;
	
	@Autowired
	private OutPayService outPayService;

	@Autowired
	PayRequestService payRequestService;
	
	@Autowired
	private PaymentMerchantMapper paymentMerchantMapper;
	
	@Autowired
	private OrderRefundMapper orderRefundMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRefundService orderRefundService;
	
	private static List<String> decryptKey = new ArrayList<String>();
	
	static{
		decryptKey.add("notify_time");
		decryptKey.add("notify_type");
		decryptKey.add("notify_id");
		decryptKey.add("sign_type");
		decryptKey.add("sign");
		decryptKey.add("batch_no");
		decryptKey.add("pay_user_id");
		decryptKey.add("pay_user_name");
		decryptKey.add("pay_account_no");
		decryptKey.add("success_details");
		decryptKey.add("fail_details");
	}
	
	/*@RequestMapping("/refundBatch/alipay")
	public void alipayRefundBatch(HttpServletRequest req, HttpServletResponse resp, String orderId){
		Order order = orderService.load(orderId);
		String orderNo = order.getOrderNo();
		String payNo = order.getPayNo();
		log.info("/refundBatch/alipay orderNo=[{}]", orderNo );
		try {
			//194537
			OutPay oldOutPay = outPayService.findByOrderNo4Refund(payNo, "ALIPAY");
			ReceiverDetailVO bean = new ReceiverDetailVO();
			bean.setSwiftNum(oldOutPay.getTradeNo());
			bean.setReceiverFee(order.getRefundFee());
			bean.setReceiverRemark("协商退款,orderNo="+orderNo);
			bean.setOrderNo(orderNo);
			
			List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
			list.add(bean);
			
			PaymentRefundRequestVO request = new PaymentRefundRequestVO();
			request.setPaymentMerchantId(oldOutPay.getPaymentMerchantId());
			request.setReceiverDetail(list);
			
			aliPayment.refundBatchRequest(request, resp);
			log.info("/refundBatch/alipay success orderNo=[{}]", orderNo );
		} catch (Exception e) {
			log.info("refundBatchRequest failed orderNo=[{}] e[{}]", orderNo, e );
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR,
					"即时到账批量退款，请求失败",e );
		}
	}*/
	
	@RequestMapping("/refundBatch/alipay/v2")
	public void alipayRefundBatchV2(HttpServletRequest req, HttpServletResponse resp, String refundId){
		String[] refundIds = refundId.split(",");
		List<OrderRefundVO> refunds = orderRefundMapper.listVoByIds(refundIds);
		Map<String,Integer> checkMap = new HashMap<>();
		for(OrderRefundVO refund:refunds){
			if(refund.getStatus()!=OrderRefundStatus.SUCCESS){
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR,"存在不是success状态的退款请求，orderNo=["+refund.getOrderNo()+"]" );
			}
			if(refund.getPayType()!=PaymentMode.ALIPAY&&refund.getPayType()!=PaymentMode.ALIPAY_PC){
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR,"存在不是支付宝支付的退款请求，orderNo=["+refund.getOrderNo()+"]" );
			}
			if(checkMap.containsKey(refund.getPayNo())){
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR,"一次批量退款操作中不能对一笔交易操作两次，PayNo=["+refund.getPayNo()+"]" );
			}
			checkMap.put(refund.getPayNo(), 1);
		}
		List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
		for(OrderRefundVO refund:refunds){
			OutPay oldOutPay = outPayService.findByOrderNo4Refund(refund.getPayNo(), "ALIPAY");
			ReceiverDetailVO bean = new ReceiverDetailVO();
			bean.setSwiftNum(oldOutPay.getTradeNo());
			bean.setReceiverFee(refund.getRefundFee());
			bean.setReceiverRemark("协商退款,OrderNo="+refund.getOrderNo()+",RefundNo="+refund.getApplyNo());
			bean.setOrderNo(refund.getOrderNo());
			bean.setRefundId(refund.getId());
			list.add(bean);
		}
		PaymentRefundRequestVO request = new PaymentRefundRequestVO();
		request.setReceiverDetail(list);
		
		try {
			aliPayment.refundBatchRequest(request, resp);
		} catch (Exception e) {
			log.info("/refundBatch/alipay failed list={} e[{}]", JSON.toJSONString(list), e );
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR,
					"即时到账批量退款，请求失败",e );
		}
	}
	
	@RequestMapping("/refundBatch/alipay")
	public void alipayRefundBatch(HttpServletRequest req, HttpServletResponse resp, String refundId){
		OrderRefund orderRefund =orderRefundService.load(refundId);
		if(orderRefund.getStatus()!=OrderRefundStatus.SUCCESS){
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR,
					"即时到账批量退款，请求失败" );
		}
		Order order = orderService.load(orderRefund.getOrderId());
		String orderNo = order.getOrderNo();
		String payNo = order.getPayNo();
		log.info("/refundBatch/alipay start orderNo=[{}]", orderNo );
		try {
			//194537
			OutPay oldOutPay = outPayService.findByOrderNo4Refund(payNo, "ALIPAY");
			ReceiverDetailVO bean = new ReceiverDetailVO();
			bean.setSwiftNum(oldOutPay.getTradeNo());
			//bean.setReceiverFee(order.getRefundFee());
			bean.setReceiverFee(orderRefund.getRefundFee());
			bean.setReceiverRemark("协商退款,orderNo="+orderNo);
			bean.setOrderNo(orderNo);
			bean.setRefundId(refundId);
			
			
			List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
			list.add(bean);
			
			PaymentRefundRequestVO request = new PaymentRefundRequestVO();
			request.setRefundId(refundId);;
			request.setPaymentMerchantId(oldOutPay.getPaymentMerchantId());
			request.setReceiverDetail(list);
			
			aliPayment.refundBatchRequest(request, resp);
			log.info("/refundBatch/alipay request success orderNo=[{}]", orderNo );
		} catch (Exception e) {
			log.info("/refundBatch/alipay failed orderNo=[{}] e[{}]", orderNo, e );
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR,
					"即时到账批量退款，请求失败",e );
		}
	}
	
	@RequestMapping(AliPaymentImpl.refundBatch_notify_url)
	public void alipayRefundBatchNotify(HttpServletRequest req,HttpServletResponse resp){
		aliPayment.doBatchRefund(req, resp);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(AliPaymentImpl.refundBatch_notify_url+"/{refundId}")
	public void alipayRefundBatchNotify(@PathVariable("refundId") String refundId, HttpServletRequest req,
			HttpServletResponse resp) {
		try {
			log.info("/refundBatch/alipay/notify start refundId=[{}]", refundId);
			
			Map<String,String> nitofyMsg = aliPayment.refundBatchNotify(req, resp);
			if(nitofyMsg == null){
				log.error("/refundBatch/alipay/notify failed refunfId=[{}]", refundId);
				return;
			}
			
			Map<String, Object> map = transDrawBackNotifyResult(nitofyMsg);
			log.info("/refundBatch/alipay/notify valid [{}], map=[{}]", refundId, map);
			//String transNo = map.get("batchNo").toString();
			//String successNum = map.get("successNum").toString();
			List<ReceiverDetailVO> list = (List)map.get("details");
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					ReceiverDetailVO bean = list.get(i);
					String tradeNo = bean.getSwiftNum();
//					OutPay outpay = outPayService.findOutPayByOldTradeNo(tradeNo, "ALIPAY");
//					String orderNo = outpay.getBillNo();
//					PayRequest payRequest = payRequestService.queryRefundByOutPayId(outpay.getId());
					
					if(bean.getStatus().equals("SUCCESS")){
						OutPay oldOutPay = outPayService.findOutPayByTradeNo(tradeNo, "ALIPAY");
						OrderRefund orderRefund = orderRefundService.load(refundId);
						Order order = orderService.load(orderRefund.getOrderId());
						
						PayRequest payRequest = trans2PayRequest4Refund(oldOutPay, order);

						//新的outpay
						OutPay newOutPay = trans2OutPay4Refund(oldOutPay, payRequest);
						newOutPay.setRequestId("0");
						newOutPay.setOutStatus("SUCCESS");
						newOutPay.setStatus(PaymentStatus.SUCCESS);
						newOutPay.setTradeNo(bean.getSwiftNum());
						newOutPay.setDetail(nitofyMsg.toString().getBytes());
						//该操作无外部交易号
						outPayService.insert(newOutPay);
						log.info("/refundBatch/alipay/,start2 end refundId=[{}]", refundId);
						if(orderRefundMapper.end(refundId)>0){
							log.info("/refundBatch/alipay/,success2 end refundId=[{}]", refundId);
						}else{
							log.error("/refundBatch/alipay/,failed end refundId=[{}]", refundId);
						}
						//orderService.updateOrderRefundByAdmin(order.getId());
						log.info("outpay success, refund swiftNum:"+bean.getSwiftNum());
//						}
					}
				}
			}
			resp.getWriter().print("success");
		} catch (Exception e) {
			 log.error("支付宝回调信息处理失败，Params:" + JSON.toJSONString(req.getParameterMap()), e);
		}
	}
	
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(AliPaymentImpl.refundBatch_notify_url+"/{orderNo}")
	public void alipayRefundBatchNotify(@PathVariable("orderNo") String orderNo, HttpServletRequest req,
			HttpServletResponse resp) {
//		log.info("alipay refund notify url is called " + request);
		try {
			log.info("/refundBatch/alipay/notify start orderNo=[{}]", orderNo);
			
			Map<String,String> nitofyMsg = aliPayment.refundBatchNotify(req, resp);
			if(nitofyMsg == null){
				log.error("/refundBatch/alipay/notify failed orderNo=[{}]", orderNo);
				return;
			}
			
			Map<String, Object> map = transDrawBackNotifyResult(nitofyMsg);
			log.info("/refundBatch/alipay/notify valid [{}], map=[{}]", orderNo, map);
			//String transNo = map.get("batchNo").toString();
			//String successNum = map.get("successNum").toString();
			List<ReceiverDetailVO> list = (List)map.get("details");
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					ReceiverDetailVO bean = list.get(i);
					String tradeNo = bean.getSwiftNum();
//					OutPay outpay = outPayService.findOutPayByOldTradeNo(tradeNo, "ALIPAY");
//					String orderNo = outpay.getBillNo();
//					PayRequest payRequest = payRequestService.queryRefundByOutPayId(outpay.getId());
					
					if(bean.getStatus().equals("SUCCESS")){
						OutPay oldOutPay = outPayService.findOutPayByTradeNo(tradeNo, "ALIPAY");
						Order order = orderService.loadByOrderNo(orderNo);
						
						PayRequest payRequest = trans2PayRequest4Refund(oldOutPay, order);

						//新的outpay
						OutPay newOutPay = trans2OutPay4Refund(oldOutPay, payRequest);
						newOutPay.setRequestId("0");
						newOutPay.setOutStatus("SUCCESS");
						newOutPay.setStatus(PaymentStatus.SUCCESS);
						newOutPay.setTradeNo(bean.getSwiftNum());
						newOutPay.setDetail(nitofyMsg.toString().getBytes());
						//该操作无外部交易号
						outPayService.insert(newOutPay);
						orderRefundMapper.end(bean.getRefundId());
						
						//orderService.updateOrderRefundByAdmin(order.getId());
//						}
					}
				}
			}
			resp.getWriter().print("success");
		} catch (Exception e) {
			 log.error("支付宝回调信息处理失败 [{}]", e);
		}
	}*/
	
	@RequestMapping("/paymentBatch/alipay")
	public void alipayBatch(HttpServletRequest req, HttpServletResponse resp,
			String withdrawApplyIds, String withdrawconfirmMoneys) {
		
		try {
			log.info("/paymentBatch/alipay start  withDrawIds:[{}], withDrawMoney[{}]", withdrawApplyIds, withdrawconfirmMoneys);
			String[] ids = withdrawApplyIds.split(",");
			String[] confirmMoneys = withdrawconfirmMoneys.split(",");
			if (ids.length != confirmMoneys.length) {
				log.error("/paymentBatch/alipay danger error [applyds:{}, confirmMoneys:{}]", ids.length, confirmMoneys.length);
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "提现申请数和申请金额数不匹配");
			}
			List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
			
//			String paymentMerchantId = null;
			PaymentRefundRequestVO request = new PaymentRefundRequestVO();
			String batchPayNo = UniqueNoUtils.next(UniqueNoType.P);
			for(int i = 0 ; i < ids.length; i++){
				WithdrawApply withdrawApply = withdrawApplyService.load(ids[i]);
				
				if(withdrawApply.getStatus() != WithdrawApplyStatus.NEW 
						&& withdrawApply.getStatus() != WithdrawApplyStatus.FAILED){
					log.error("withdrawApply status error [{}]", withdrawApply);
					throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "提现状态错误，只能为NEW或FAILED");
				}
				
//				PaymentMerchant paymentMerchant = paymentMerchantMapper.selectByPrimaryKey(withdrawApply.getCheckingChannel());
//				if(paymentMerchantId == null){
//					paymentMerchantId = paymentMerchant.getId();
//				}
//				
//				if(!paymentMerchantId.equals(paymentMerchant.getId())){
//					throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "不同的商户号");
//				}
				
				withdrawApply.setConfirmMoney(NumberUtils.createBigDecimal(confirmMoneys[i]));
				
				if(withdrawApply.getApplyMoney().compareTo(withdrawApply.getConfirmMoney()) < 0){
					throw new BizException(GlobalErrorCode.UNKNOWN, withdrawApply.getAccountName() + " 核准金额="+withdrawApply.getConfirmMoney()+" 不能大于申请金额：" + withdrawApply.getApplyMoney());
				}
				
				ReceiverDetailVO bean = new ReceiverDetailVO();
				bean.setSwiftNum(withdrawApply.getApplyNo());
				UserAlipay userAlipay = userAlipayService.loadByUserId(withdrawApply.getUserId());
				if(userAlipay == null){
					throw new BizException(GlobalErrorCode.INTERNAL_ERROR,"当前打款用户无支付宝信息");
				}
				bean.setReceiverAccount(withdrawApply.getAccountNumber());
				bean.setReceiverName(withdrawApply.getAccountName());
				bean.setReceiverFee(withdrawApply.getConfirmMoney());
				bean.setReceiverRemark("想去君给你打钱啦~：提现号-"+withdrawApply.getApplyNo());
				withdrawApplyService.withdrawPayReqeust(withdrawApply.getApplyNo(), batchPayNo);
				
				list.add(bean);
			}
			
//			request.setPaymentMerchantId(paymentMerchantId);
			request.setReceiverDetail(list);
			request.setBatchPayNo(batchPayNo);;
			
			aliPayment.payBatchRequest(request, resp);
			log.info("/paymentBatch/alipay request success by ids:[{}]", withdrawApplyIds);
		} catch (Exception e) {
			 log.error("支付请求失败 [{}]", e);
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR,
					"批量付款到支付宝用户，请求失败", e);
		}
	}

	@RequestMapping(AliPaymentImpl.paymentBatch_notify_url)
	public void alipayBatchNotify(HttpServletRequest req,
			HttpServletResponse resp) {
		try {
			log.info(AliPaymentImpl.paymentBatch_notify_url);
			Map<String,String> notifyMsg = aliPayment.payBatchNotify(req, resp);
			if(notifyMsg == null){
				log.error("alipayBatchNotify 解析支付宝notify信息出错");
				return;
			}
			List<ReceiverDetailVO> list = transPayRequestNotifyResult(notifyMsg);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					ReceiverDetailVO bean = list.get(i);
					WithdrawApply withdrawApply = withdrawApplyService.loadByApplyNo(bean.getSwiftNum());
					if(withdrawApply.getStatus()==WithdrawApplyStatus.SUCCESS){
						continue;
					}
					if(bean.getStatus().equals("S")){
						PayRequest request = trans2PayRequest4PayBatch(withdrawApply);
						request.setOutpayType(PaymentMode.ALIPAY.toString());
						UserAlipay userAlipay = userAlipayService.loadByUserId(withdrawApply.getUserId());
						request.setOutpayInfo(userAlipay.getAccount());
						if(payRequestService.onCreate(request)){
							payRequestService.onPay(request);
							payRequestService.onSuccess(request);
							log.info("pay request success, withdraw applyNo:"+bean.getSwiftNum());
							OutPay snapshot = trans2OutPay4PayBatch(request);
							snapshot.setOutStatus("SUCCESS");
							snapshot.setRequestId(request.getId());
							snapshot.setStatus(PaymentStatus.SUCCESS);
							snapshot.setTradeNo(bean.getAlipaySwiftNum());
							snapshot.setDetail(notifyMsg.toString().getBytes());
							outPayService.insert(snapshot);
							log.info("outpay success, withdraw applyNo:"+bean.getSwiftNum());
							withdrawApplyService.withdrawSuccessByNo(bean.getSwiftNum(), bean.getReceiverFee());
						}
					}else{
						log.error("支付宝打款失败：ApplyNO="+withdrawApply.getApplyNo()+" msg:"+bean.getMsg());
						withdrawApplyService.withdrawFailedByApplyNo(withdrawApply.getApplyNo());
					}
				}
			}
			resp.getWriter().print("success");
		} catch (Exception e) {
			 log.error("支付宝回调信息处理失败", e);
		}
	}
	
	//慎用修数据的时候用
	@RequestMapping(AliPaymentImpl.paymentBatch_notify_url+"/admin")
	public void alipayBatchNotifyForAdmin(HttpServletRequest req,
			HttpServletResponse resp) {
		String pwd = req.getParameter("pwd");
		if(StringUtils.isBlank(pwd)||!pwd.equals("whosyourdaddy"))
			return ;
		try {
			log.info(AliPaymentImpl.paymentBatch_notify_url);
			Map<String,String> notifyMsg = aliPayment.payBatchNotify2(req, resp);
			if(notifyMsg == null){
				log.error("alipayBatchNotify 解析支付宝notify信息出错");
				return;
			}
			List<ReceiverDetailVO> list = transPayRequestNotifyResult(notifyMsg);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					ReceiverDetailVO bean = list.get(i);
					WithdrawApply withdrawApply = withdrawApplyService.loadByApplyNo(bean.getSwiftNum());
					if(withdrawApply.getStatus()==WithdrawApplyStatus.SUCCESS){
						continue;
					}
					if(bean.getStatus().equals("S")){
						PayRequest request = trans2PayRequest4PayBatch(withdrawApply);
						request.setOutpayType(PaymentMode.ALIPAY.toString());
						UserAlipay userAlipay = userAlipayService.loadByUserId(withdrawApply.getUserId());
						request.setOutpayInfo(userAlipay.getAccount());
						if(payRequestService.onCreate(request)){
							payRequestService.onPay(request);
							payRequestService.onSuccess(request);
							log.info("pay request success, withdraw applyNo:"+bean.getSwiftNum());
							OutPay snapshot = trans2OutPay4PayBatch(request);
							snapshot.setOutStatus("SUCCESS");
							snapshot.setRequestId(request.getId());
							snapshot.setStatus(PaymentStatus.SUCCESS);
							snapshot.setTradeNo(bean.getAlipaySwiftNum());
							snapshot.setDetail(notifyMsg.toString().getBytes());
							outPayService.insert(snapshot);
							log.info("outpay success, withdraw applyNo:"+bean.getSwiftNum());
							withdrawApplyService.withdrawSuccessByNo(bean.getSwiftNum(), bean.getReceiverFee());
						}
					}else{
						log.error("支付宝打款失败：ApplyNO="+withdrawApply.getApplyNo()+" msg:"+bean.getMsg());
						withdrawApplyService.withdrawFailedByApplyNo(withdrawApply.getApplyNo());
					}
				}
			}
			resp.getWriter().print("success");
		} catch (Exception e) {
			 log.error("支付宝回调信息处理失败", e);
		}
	}
	
	private List<ReceiverDetailVO> transPayRequestNotifyResult(Map<String,String> nitofyMsg){
		List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
		//根据回传的值，得到每一笔支付的处理结果
		try {
//			if (sign_type.equals("0001")){
//				nitofyMsg = AlipayNotify.decryptByKey(nitofyMsg, secret, decryptKey);
//			}
			String successDetails = nitofyMsg.get("success_details");
			String failDetails = nitofyMsg.get("fail_details");
			list.addAll(splitPayRequestBatchDetails(successDetails));
			list.addAll(splitPayRequestBatchDetails(failDetails));
		} catch (Exception e) {
			log.error("paymentBatch verify ok, but parse data failed.", e);
		}
		return list;
	}
	
	private Map<String,Object> transDrawBackNotifyResult(Map<String,String> nitofyMsg){
		Map<String, Object> result = new HashMap<String, Object>();
		//根据回传的值，得到每一笔支付的处理结果
		try {
//			if (sign_type.equals("0001")){
//				nitofyMsg = AlipayNotify.decryptByKey(nitofyMsg, secret, decryptKey);
//			}
			result.put("batchNo", nitofyMsg.get("batch_no"));
			result.put("notifyTime", nitofyMsg.get("notify_time"));
			result.put("successNum", nitofyMsg.get("success_num"));
			String details = nitofyMsg.get("result_details");
			List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
			list.addAll(splitDrawBackBatchDetails(details));
			result.put("details", list);
		} catch (Exception e) {
			log.error("paymentBatch verify ok, but parse data failed.", e);
		}
		return result;
	}
	
	private List<ReceiverDetailVO> splitPayRequestBatchDetails(String detailStr){
		List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
		if(detailStr!=null&&detailStr.length()>0){
			String[] details = detailStr.split("\\|");
			for(int i=0;i<details.length;i++){
				String[] temp = details[i].split("\\^");
				ReceiverDetailVO bean = new ReceiverDetailVO();
				bean.setSwiftNum(temp[0]);
				bean.setReceiverAccount(temp[1]);
				bean.setReceiverName(temp[2]);
				bean.setReceiverFee(new BigDecimal(temp[3]));
				bean.setStatus(temp[4]);
				bean.setMsg(temp[5]);
				bean.setAlipaySwiftNum(temp[6]);
				bean.setPayTime(temp[7]);
				list.add(bean);
			}
		}
		return list;
	}
	
	private List<ReceiverDetailVO> splitDrawBackBatchDetails(String detailStr){
		List<ReceiverDetailVO> list = new ArrayList<ReceiverDetailVO>();
		if(detailStr!=null&&detailStr.length()>0){
			String[] details = detailStr.split("\\#");
			for(int i=0;i<details.length;i++){
				String[] temp = details[i].split("\\^");
				ReceiverDetailVO bean = new ReceiverDetailVO();
				bean.setSwiftNum(temp[0]);	//原交易号
				bean.setReceiverFee(new BigDecimal(temp[1]));	//退款金额
				bean.setStatus(temp[2]);//处理结果
				list.add(bean);
			}
		}
		return list;
	}
	
	private PayRequest trans2PayRequest4Refund(OutPay outpay, Order order) {
		String payNo = payRequestApiService.generatePayNo();
		SubAccount toAccount = accountApiService.findSubAccountByUserId(order.getBuyerId(), AccountType.AVAILABLEB);
		SubAccount fromAccount = accountApiService.findSubAccountByUserId(order.getSellerId(), AccountType.AVAILABLE);
		PayRequest request = new PayRequest(payNo, outpay.getBillNo(),
				PayRequestBizType.REFUND, PayRequestPayType.REFUND,
				outpay.getAmount(), fromAccount.getId(),
				toAccount.getId(), null);
		return request;
	}
	
	private OutPay trans2OutPay4Refund(OutPay oldOutPay, PayRequest payRequest){
		OutPay aOp = new OutPay();
		aOp.setUserId(oldOutPay.getUserId());
		aOp.setOutId(oldOutPay.getOutId()); // WEIXIN微信支付  UNION银联支付 TENPAY财付通支付 ALIPAY支付宝
		aOp.setpOutpayId("0");				// 父亲支付ID，用于批量打款和批量退款中的子支付记录
		aOp.setRequestId(payRequest.getId());					// 
		aOp.setForOutpayId(oldOutPay.getId());				// 仅用于原路退回的操作，关联原始支付的OutPayId
		
		aOp.setOutAccountId(oldOutPay.getOutAccountId());				// 三方支付帐号编号
		aOp.setOutAccountName(oldOutPay.getOutAccountName());	// 支付帐号名称
		aOp.setOutStatus("SUBMITTED");									//  支付请求的状态，这个状态不是必须的，对于支付宝就是一个字符串描述
		aOp.setOutstatusex("");											//  外部交易扩展状态
		aOp.setDetail("".getBytes());   // byte[]);
		aOp.setBillNo(payRequest.getBizId());    		//  商户订单号
		aOp.setTradeNo("");  		// 第三方交易号
		
		aOp.setStatus(PaymentStatus.PENDING); 			//系统内部的支付状态，SUBMITTED提交  FAILED支付失败  SUCCESS支付完成 CANCEL取消
		aOp.setOutpayType(PayRequestBizType.REFUND.toString());
		aOp.setOutpayTypeEx("ADMIN_REFUND"); // 支付类型，USER_PAY用户即时到账,ADMIN_REFUND运营退款，ADMIN_WITHDRAW运营打款，信用还款CREDIT_REPAYMENT，CREDIT_REFUND_AFTER_REPAYMENT还款后的退款
		aOp.setAmount(oldOutPay.getAmount());
		aOp.setUpdatedAt(new Date());
		aOp.setCreatedAt(new Date());
		return aOp;
	}

	private PayRequest trans2PayRequest4PayBatch(WithdrawApply withdrawApply) {
		String payNo = payRequestApiService.generatePayNo();
		SubAccount fromAccount = accountApiService.findSubAccountByUserId(withdrawApply.getUserId(), AccountType.WITHDRAW);
		SubAccount toAccount = accountApiService.findSubAccountByUserId(userService.loadKkkdUserId(), AccountType.WITHDRAW);
		PayRequest request = new PayRequest(payNo, withdrawApply.getApplyNo(),
				PayRequestBizType.WITHDRAW, PayRequestPayType.WITHDRAW,
				withdrawApply.getApplyMoney(), fromAccount.getId(),
				toAccount.getId(), null);
		return request;
	}
	
	private OutPay trans2OutPay4PayBatch(PayRequest payRequest){
		OutPay aOp = new OutPay();
		aOp.setUserId(payRequest.getToSubAccountId());
		aOp.setOutId(payRequest.getOutpayType()); // WEIXIN微信支付  UNION银联支付 TENPAY财付通支付 ALIPAY支付宝
		aOp.setpOutpayId("0");				// 父亲支付ID，用于批量打款和批量退款中的子支付记录
		aOp.setRequestId(payRequest.getId());					// 
		aOp.setForOutpayId("0");				// 仅用于原路退回的操作，关联原始支付的OutPayId
		
		aOp.setOutAccountId(payRequest.getFromSubAccountId());				// 三方支付帐号编号
		aOp.setOutAccountName("");	// 支付帐号名称
		aOp.setOutStatus("SUBMITTED");									//  支付请求的状态，这个状态不是必须的，对于支付宝就是一个字符串描述
		aOp.setOutstatusex("");											//  外部交易扩展状态
		aOp.setDetail("".getBytes());   // byte[]);
		aOp.setBillNo("");    		//  商户订单号
		aOp.setTradeNo("");  		// 第三方交易号
		
		aOp.setStatus(PaymentStatus.PENDING); 			//系统内部的支付状态，SUBMITTED提交  FAILED支付失败  SUCCESS支付完成 CANCEL取消
		aOp.setOutpayType(PayRequestBizType.WITHDRAW.toString());
		aOp.setOutpayTypeEx("ADMIN_WITHDRAW"); // 支付类型，USER_PAY用户即时到账,ADMIN_REFUND运营退款，ADMIN_WITHDRAW运营打款，信用还款CREDIT_REPAYMENT，CREDIT_REFUND_AFTER_REPAYMENT还款后的退款
		aOp.setAmount(payRequest.getAmount());
		aOp.setUpdatedAt(new Date());
		aOp.setCreatedAt(new Date());
		return aOp;
	}
}

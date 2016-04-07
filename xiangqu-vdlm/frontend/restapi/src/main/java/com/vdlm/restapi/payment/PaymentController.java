package com.vdlm.restapi.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.dal.model.UserPayBankVO;
import com.vdlm.dal.type.DeviceType;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.dal.vo.PaymentVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.outpay.OutPayAgreementService;
import com.vdlm.service.outpay.PaymentRequestVO;
import com.vdlm.service.outpay.PaymentResponseVO;
import com.vdlm.service.outpay.ThirdPartyPayment;
import com.vdlm.service.pay.PaymentService;

@Controller
public class PaymentController extends BaseController {

	@Autowired
	private ThirdPartyPayment aliPayment;

	@Autowired
	private ThirdPartyPayment tenPayment;
	
	@Autowired
	private ThirdPartyPayment umPayment;
	
	@Autowired
	private OutPayAgreementService outPayAgreementService;
	
	@Autowired
	private PaymentService paymentService;
	
    /**
	 * 输入短信后的确认页面，类似callback操作
	 * 协议支付 第二步
	 * @param req
	 * @param resp
	 * @param request
	 * @param model
	 */
	@ResponseBody
	@RequestMapping("/openapi/pay/umpay/confirm")
	public ResponseObject<Boolean> umpayConfirm(HttpServletRequest req, HttpServletResponse resp, String tradeNo, String payAgreementId, String verifyCode) {
		PaymentResponseVO paymentVO = null;
		try {
		    User user = getCurrentUser();
			
			PaymentRequestVO request = new PaymentRequestVO();
			request.setUserId(user.getId()); 
			request.setTradeNo(tradeNo);
			request.setPayAgreementId(payAgreementId);
			request.setVerifyCode(verifyCode);
			request.setOutTradeNo(req.getParameter("outTradeNo"));
			BigDecimal totalFee = BigDecimal.valueOf(0.01);
			totalFee = totalFee.setScale(2, BigDecimal.ROUND_DOWN); 
			request.setTotalFee(totalFee);
			
			paymentVO = umPayment.payConfirm(req, resp, request);
			
		}catch (BizException e) {
			log.error("请求失败", e.getMessage());
			throw e;
		}
		catch (Exception e) {
			log.error("请求失败", e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请求失败");
		}
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 协议支付再次发送短信
	 * 
	 * @param req
	 * @param resp
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/openapi/pay/umpay/sendSmsAgain")
	public ResponseObject<Boolean> reSendSms(HttpServletRequest req, HttpServletResponse resp, PaymentRequestVO request) {
		try {
			request.setUserId(getCurrentUser().getId()); 
			umPayment.sendSmsAgain(request);
		} catch (Exception e) {
			log.error("支付请求再次发送短信失败，tradeNo=" + request.getTradeNo()+","+e.getMessage(), e);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "再次发送接口失败");
		}
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 订单取消by zzd
	 * @param id
	 * @return
	 */
    @ResponseBody
    @RequestMapping("/openapi/payment/list")
    public ResponseObject<List<PaymentVO>> paymentList(@RequestHeader("Domain") String domain, String extUid, DeviceType devicePlat){
    	List<PaymentVO> paymentList = new ArrayList<PaymentVO>();
    	PaymentVO payment = null;
    	
    	if(StringUtils.isNotBlank(extUid)){
    		List<UserPayBankVO> banks = outPayAgreementService.listBankByUserId(getCurrentUser().getId());
    		for(UserPayBankVO vo : banks){
    			payment = new PaymentVO();
    			payment.setId(vo.getAggreeId());
    			payment.setCode(PaymentMode.UMPAY.toString());
    			payment.setPayAgreementId(vo.getAggreeId());
    			payment.setIcon(vo.getBankImg());
    			payment.setName(vo.getBankName() + " " + vo.getCardTypeName() + "****" + vo.getAccountNum());
    			payment.setDescription("推荐" + vo.getBankName() + "使用");
    			payment.setDescriptionExt(vo.getMediaNo());
    			paymentList.add(payment);
    		}
    	}
    	
    	paymentList.addAll(paymentService.list(devicePlat));
    	return new ResponseObject<List<PaymentVO>>(paymentList);
    }
}

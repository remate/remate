package com.vdlm.restapi.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.verify.VerificationFacade;
import com.vdlm.biz.verify.impl.VerificationFacadeImpl.SmsType;
import com.vdlm.dal.model.PayBank;
import com.vdlm.dal.model.UserAlipay;
import com.vdlm.dal.model.UserBank;
import com.vdlm.dal.status.WithdrawApplyStatus;
import com.vdlm.dal.type.AccountType;
import com.vdlm.dal.vo.DealHistoryVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.alipay.UserAliPayVO;
import com.vdlm.service.account.AccountService;
import com.vdlm.service.account.SubAccountService;
import com.vdlm.service.alipay.UserAlipayService;
import com.vdlm.service.bank.UserBankService;
import com.vdlm.service.bank.WithdrawApplyService;
import com.vdlm.service.deal.DealService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.payBank.PayBankService;
import com.vdlm.service.user.UserService;
import com.vdlm.utils.IdCardUtils;

@Controller
public class UserBankController extends BaseController {
	
	@Autowired
	UserBankService userBankService;
	
	@Autowired
	UserAlipayService userAlipayService;
	
	@Autowired
	WithdrawApplyService withdrawApplyService;
	
	@Autowired
	private VerificationFacade veriFacade;
	
	@Autowired
	DealService dealService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	SubAccountService subAccountService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	PayBankService paybankService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	private Boolean inValiIdCarkNum(String idCard) {
//		Boolean ret = false;
//		if (idCard == null || idCard.length() < 10 || idCard.length() > 18) return ret;
//		Pattern p = Pattern.compile("^(\\d{14}|\\d{17})(\\d|[xX])$");
//		Matcher m = p.matcher(idCard);
//		return m.matches();
		return !IdCardUtils.isIDCard(idCard);
	}
	
	@ResponseBody
	@RequestMapping("/userBank/save")
	public ResponseObject<UserBank> save(@Valid @ModelAttribute UserBankForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		RequestContext requestContext = new RequestContext(req);
		if (inValiIdCarkNum(form.getIdCardNum())) {
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("userbank.idCardNum.invalid"));
		}
		if (IdCardUtils.checkFullWidth(form.getAccountNumber())) { // 账号全半角
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("user.account.sbc"));
		}
		UserBank userBank = new UserBank();
		BeanUtils.copyProperties(form, userBank);
		userBank.setUserId(userBankService.getCurrentUser().getId());
		int rc = 0;
		if(StringUtils.isBlank(form.getId())){
			rc = userBankService.insert(userBank);
		} else {
			if(StringUtils.isBlank(form.getSmsCode())){
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("userbank.smscode.not.null"));
			}
			
			boolean valid = veriFacade.verifyCode(userBankService.getCurrentUser().getPhone(), form.getSmsCode(), SmsType.MODIFY_BANK);
			if (!valid) {
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("userbank.smscode.not.valid"));
			}
			rc = userBankService.update(userBank);
		}
		if(rc == 0){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.bank.error.message"));
		}
		rc = userService.updateNameAndIdCardNumByPrimaryKey(userBank.getUserId(), userBank.getAccountName(), userBank.getIdCardNum());
		if(rc == 0){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.bank.error.message"));
		}
		return new ResponseObject<UserBank>(userBank);
	}
	
	@ResponseBody
	@RequestMapping("/userBank/update")
	public ResponseObject<UserBank> update(@Valid @ModelAttribute UserBankUpdateForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		UserBank userBank = new UserBank();
		BeanUtils.copyProperties(form, userBank);
		userBank.setUserId(userBankService.getCurrentUser().getId());
		int rc = 0;
		RequestContext requestContext = new RequestContext(req);
		if (IdCardUtils.checkFullWidth(form.getAccountNumber())) { // 账号全半角
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("user.account.sbc"));
		}
		
		if(StringUtils.isBlank(form.getId())){
			rc = userBankService.insert(userBank);
		} else {
			if(StringUtils.isBlank(form.getSmsCode())){
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("userbank.smscode.not.null"));
			}
			
			boolean valid = veriFacade.verifyCode(userBankService.getCurrentUser().getPhone(), form.getSmsCode(), SmsType.MODIFY_BANK);
			if (!valid) {
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("userbank.smscode.not.valid"));
			}
			rc = userBankService.update(userBank);
		}
		if(rc == 0){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.bank.error.message"));
		}
		return new ResponseObject<UserBank>(userBank);
	}
	
	/**
	 * 注册发送验证码
	 * 
	 * @param form
	 *            with mobile
	 * @return 是否已发送sms
	 */
	@ResponseBody
	@RequestMapping("/userBank/send-sms-code")
	public ResponseObject<Boolean> sendSmsCode() {
		if(StringUtils.isBlank(userBankService.getCurrentUser().getPhone()))
			return new ResponseObject<Boolean>(Boolean.FALSE);	
		veriFacade.generateCode(userBankService.getCurrentUser().getPhone(), SmsType.MODIFY_BANK);
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}
	
	/**
	 * 我的银行卡
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/userBank/mine")
	public ResponseObject<UserBankVO> mine() {
		List<UserBank> banks = userBankService.mine();
		UserBankVO bank = null; 
		if(banks.size() > 0){
			bank = new UserBankVO();
			BeanUtils.copyProperties(banks.get(0), bank);
		}
		return new ResponseObject<UserBankVO>(bank);
	}
	
	/**
	 * 我的收入汇总
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/userBank/mineAccount")
	public ResponseObject<Map<String, Object>> mineIncome() {
		String userId = userBankService.getCurrentUser().getId();
		Map<AccountType, BigDecimal> map = subAccountService.selectBalanceByUser(userId);
		
		BigDecimal balance = map.get(AccountType.AVAILABLE);
		balance = ObjectUtils.defaultIfNull(balance, BigDecimal.ZERO);
		
		BigDecimal warrant = map.get(AccountType.DANBAO);
		warrant = ObjectUtils.defaultIfNull(warrant, BigDecimal.ZERO);
		
//		BigDecimal frozen = map.get(AccountType.FROZEN);
//		frozen = ObjectUtils.defaultIfNull(frozen, BigDecimal.ZERO);
		
		BigDecimal withdraw = map.get(AccountType.WITHDRAW);
		withdraw = ObjectUtils.defaultIfNull(withdraw, BigDecimal.ZERO);
		
		BigDecimal withdrawAll = withdrawApplyService.totalWithdrawApplyByStatus(userId, WithdrawApplyStatus.SUCCESS);
		withdrawAll = ObjectUtils.defaultIfNull(withdrawAll, BigDecimal.ZERO);
		
		List<UserBank> banks = userBankService.mine();
		//当前版本只返回一个
		UserBankVO bank = null;
		if(banks.size() > 0){
			bank = new UserBankVO();
			BeanUtils.copyProperties(banks.get(0), bank);
			bank.setAccountNumber(getAccountNumberForShow(bank.getAccountNumber()));
		}
		
		UserAliPayVO alipayVO = null;
		UserAlipay alipay = userAlipayService.loadByUserId(userId);
		if(alipay != null){
			alipayVO = new UserAliPayVO();
			BeanUtils.copyProperties(alipay, alipayVO);
		}
		
		Map<String, Object> mineIncome = new HashMap<String, Object>();
		mineIncome.put("balance", balance); //未提现收入
		mineIncome.put("withdraw", withdraw); //提现中
		mineIncome.put("warrant", warrant);	//担保交易
		mineIncome.put("addUpIncome", balance.add(withdrawAll).add(withdraw)); //累计收入
		mineIncome.put("type", userService.load(userId).getWithdrawType()); //我的绑定类型
		mineIncome.put("mineBank", bank); //我的银行卡
		mineIncome.put("mineAlipay", alipayVO);//我的支付宝信息
		return new ResponseObject<Map<String, Object>>(mineIncome);
	}
	
	@ResponseBody
	@RequestMapping("/user/incomeAccount/change")
	public ResponseObject<Boolean> incomeAccount(int type){
		String userId = userService.getCurrentUser().getId();
		userService.saveWithDrawType(userId, type);
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}
	
	/**
     * 获取银行卡显示
     * 
     * @return
     */
    private String getAccountNumberForShow(String accountNumber) {
        if (StringUtils.isBlank(accountNumber))
        {
            return "";
        }
        try {
            String temp = accountNumber;
            int len = temp.length();
            String start = temp.substring(0, 6);
            String end = temp.substring(len - 4);
            String center = "****";
            return start + center + end;
        } catch (Exception e) {
        }
        return "";
    }

	
	/**
	 * 担保交易详细
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/userBank/mineWarrant")
	public ResponseObject<List<IncomeDetailVO>> warrant() {
		return new ResponseObject<List<IncomeDetailVO>>(new ArrayList<IncomeDetailVO>());
	}
	
	/**
	 * 全部收入详细
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/userBank/mineDealHistory")
	public ResponseObject<List<DealHistoryVO>> allIncome(HttpServletRequest req, Pageable page) {
		RequestContext requestContext = new RequestContext(req);
		List<DealHistoryVO> vos = accountService.listDeal(page);
		String title = null, memo = null;
		for(DealHistoryVO vo : vos){
			try{
			title = requestContext.getMessage("order.status."+vo.getOrderStatus().toString().toLowerCase());
			}catch(Exception e){
				title = "未知";
			}
			memo = vo.getFeeFrom() + requestContext.getMessage("order.status.paid");
			vo.setTitle(title);
			vo.setMemo(memo);
		}
		
		return new ResponseObject<List<DealHistoryVO>>(new ArrayList<DealHistoryVO>(vos));
	}
	
	/**
	 * 全部收入详细
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/userBank/mineDealHistory")
//	public ResponseObject<List<DealHistoryVO>> allIncome(HttpServletRequest req, Pageable page) {
//		RequestContext requestContext = new RequestContext(req);
//		List<DealHistoryVO> vos = dealService.listDealByUserId( dealService.getCurrentUser().getId(), page );
//		String title = null, memo = null;
//		for(DealHistoryVO vo : vos){
//			if(vo.getDealType() == DealType.WITHDRAW){
//				title = requestContext.getMessage("withdraw.success");
//				memo = requestContext.getMessage("withdraw.describe", new Object[]{vo.getWithdrawBank()});
//				vo.setFee(new BigDecimal(-vo.getFee().doubleValue())); 
//			}else if(vo.getDealType() == DealType.DIRECT_PAY){
//				title = requestContext.getMessage("order.status."+vo.getOrderStatus().toString().toLowerCase());
//				memo = vo.getFeeFrom() + requestContext.getMessage("deal.type." + vo.getDealType().toString().toLowerCase());
//			}else if(vo.getDealType() == DealType.TRANSFER){
//				title = requestContext.getMessage("order.status."+vo.getOrderStatus().toString().toLowerCase());
//				memo = vo.getFeeFrom() + requestContext.getMessage("deal.type." + vo.getDealType().toString().toLowerCase());
//			}else if(vo.getDealType() == DealType.DEPOSIT){
//				title = requestContext.getMessage("order.status."+vo.getOrderStatus().toString().toLowerCase());
//				memo = vo.getFeeFrom() + requestContext.getMessage("deal.type." + vo.getDealType().toString().toLowerCase());
//			}
//			else{
//				continue;
//			}
//			vo.setTitle(title);
//			vo.setMemo(memo);
//		}
//		
//		return new ResponseObject<List<DealHistoryVO>>(new ArrayList<DealHistoryVO>(vos));
//	}
	
	/**
	 * 获取常用银行列表
	 * */
	@ResponseBody
	@RequestMapping("/userBank/bankList")
	public ResponseObject<List<PayBank>> getCommonlyBankList(){
		List<PayBank> list = paybankService.obtainCommonlyBankList();
		for(int i=0;i<list.size();i++){
			list.get(i).setImg(resourceFacade.resolveUrl(list.get(i).getImg()));
		}
		return new ResponseObject<List<PayBank>>(list);
	}
	
}

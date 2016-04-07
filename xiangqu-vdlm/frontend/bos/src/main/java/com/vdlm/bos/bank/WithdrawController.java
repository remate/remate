package com.vdlm.bos.bank;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.fabric.xmlrpc.base.Array;
import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.status.WithdrawApplyStatus;
import com.vdlm.dal.vo.WithdrawAdmin;
import com.vdlm.service.alipay.UserAlipayService;
import com.vdlm.service.bank.UserBankService;
import com.vdlm.service.bank.WithdrawApplyService;
import com.vdlm.service.excel.ExcelService;

@Controller
public class WithdrawController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WithdrawApplyService withdrawApplyService;
	
	@Autowired
	private UserBankService userBankService;
	
	@Autowired
	private UserAlipayService userAlipayService;
	
	@Autowired
	private ExcelService excelService;
	
	@RequestMapping(value = "withdraw")
	public String list(Model model, HttpServletRequest req) {
		return "bank/withdraws";
	}
	
	@ResponseBody
	@RequestMapping(value = "withdraw/list")
	public Map<String, Object> list(WithdrawSearchForm form, Pageable pageable) {
		
		Map<String, Object> params = mergeParam(form);	
		List<WithdrawAdmin> withdraws = withdrawApplyService.listWithdrawApply(params, pageable);
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> countMap = withdrawApplyService.countWithdrawApply(params);
		data.put("total", countMap.get("totalCount"));
		data.put("rows", withdraws);
		
		BigDecimal pageAmount = new BigDecimal(0);
		for (int i = 0; i < withdraws.size(); i++) {
			WithdrawAdmin vo = withdraws.get(i);
			pageAmount = pageAmount.add(vo.getApplyMoney());
		}
		
		List<WithdrawAdmin> footer = new ArrayList<WithdrawAdmin>();
		WithdrawAdmin pageSum = new WithdrawAdmin();
		pageSum.setPhone("当前页金额");
		pageSum.setApplyMoney(pageAmount);
		footer.add(pageSum);
		
		String totalAmount = countMap.get("totalAmount") == null ? "0"
				: countMap.get("totalAmount").toString();
		WithdrawAdmin totalSum = new WithdrawAdmin();
		totalSum.setPhone("总计金额");
		totalSum.setApplyMoney(new BigDecimal(totalAmount));
		footer.add(totalSum);
		
		data.put("footer", footer);
		return data;
	}
	
	@RequestMapping(value = "withdraw/exportExcel")
	@ResponseBody
	public void export2Excel(
			WithdrawSearchForm form, HttpServletResponse resp) {
		Map<String, Object> params = mergeParam(form);
		List<WithdrawAdmin> transDetails = withdrawApplyService.listWithdrawApply(params,null);
		String sheetStr = "用户明细";
		String filePrefix = "withdrawApply";
		String[] secondTitle = new String[]{"提现id","卖家","提现方式","提现渠道","账户名","帐号","金额","状态","备注","创建时间","修改时间"};
		String[] strBody = new String[]{"getIdLong","getPhone","getTypeStr","getOpeningBank","getAccountName","getAccountNumber","getApplyMoney","getStatus","getOpRemark","getCreatedAtStr","getUpdatedAtStr"};
		excelService.export(filePrefix, transDetails, WithdrawAdmin.class, sheetStr, transParams2Title(params), secondTitle, strBody, resp);
	}
	
	private Map<String, Object> mergeParam(WithdrawSearchForm form){
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("archive", ObjectUtils.defaultIfNull(form.getArchive_kwd(), Boolean.FALSE));
		
		if(form.getLock_kwd() != -1){
			params.put("lock", form.getLock_kwd());
		}
		
		if(StringUtils.isNotBlank(form.getPhone_kwd())){
			params.put("phone", "%" + form.getPhone_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getSeller_name_kwd())){
			params.put("sellerName", "%" + form.getSeller_name_kwd() + "%" );
		}
		
		if(StringUtils.isNotBlank(form.getApply_no_kwd())){
			params.put("applyNo",  form.getApply_no_kwd() );
		}
		if(StringUtils.isNotBlank(form.getBatch_pay_no_kwd())){
			params.put("batchPayNo",  form.getBatch_pay_no_kwd() );
		}
		
		if(StringUtils.isNotBlank(form.getType_kwd())){
			params.put("type", Integer.valueOf(form.getType_kwd()));
		}
		params.put("status", form.getWithdraw_status_kwd());
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
		if (StringUtils.isNotBlank(form.getPay1_date_kwd())) {
			params.put("payDateFrom", form.getPay1_date_kwd());
		}
		if (StringUtils.isNotBlank(form.getCheckingChannel_kwd())) {
			params.put("checkingChannel", form.getCheckingChannel_kwd());
		}
		if (StringUtils.isNotBlank(form.getPay2_date_kwd())) {
			 try {
				Date date = DateUtils.addDays(DateUtils.parseDate(form.getPay2_date_kwd(), "yyyy-MM-dd"), 1);
				params.put("payDateTo", date);
			} catch (ParseException e) {
			
			}
		}
		return params;
	}
	
	@ResponseBody
	@RequestMapping(value = "withdraw/pay")
	public Json pay(String ids, String confirmMoneys) {
		Json json = new Json();
		try {
			withdrawApplyService.batchPay(ids, confirmMoneys);
			json.setMsg("打款成功");
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("打款失败;" + e.getMessage());
			log.error("打款失败,ids:[{}]",ids,e);
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "withdraw/opLock")
	public Json opLock(String userId, String remark, String amount, int op) {
		Json json = new Json();
		if (userId == null) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("关闭失败，该笔提现找不到用户");
			return json;
		}
		
		try {
			if(withdrawApplyService.opLock(userId, remark, op == 0 ? null : new BigDecimal(amount), op))
				json.setMsg("关闭成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("关闭失败，该笔提现已关闭或已锁定");
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("锁定失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getPhone() + "锁定提现userId=[" + userId + "] op[" + op + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "withdraw/cancel")
	public Json cancel(String withdrawId, String opRemark) {
		Json json = new Json();
		try {
			if(withdrawApplyService.cancel(withdrawId, opRemark))
				json.setMsg("关闭成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("关闭失败，该笔提现已关闭或已提现");
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("关闭失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getPhone() + "关闭提现withdrawId=[" + withdrawId + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
	private String transParams2Title(Map<String, Object> params){
		String result = "";
		if(params!=null){
			Iterator<String> it = params.keySet().iterator();
			int i=0;
			while(it.hasNext()){
				if(i>0){
					result += ";";
				}
				String key = it.next();
				Object value = params.get(key);
				if(value!=null){
					result += "{"+key+"="+value.toString()+"}";
					i++;
				}
			}
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "withdraw/reSetStatus")
	public Json reSetStatus(String[] ids) {
		Json json = new Json();
		// TODO: 过滤用户  bos账号划分角色
		try {
			int cnt = withdrawApplyService.reSetStsByIds(WithdrawApplyStatus.NEW, ids);
			if(cnt >= 0)
				json.setMsg("重新打开:[" + cnt + "]条记录");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("重新打开失败");
			}
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("重新打开异常;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getPhone() + " 重新打开withdrawIds=[" + Arrays.toString(ids) + "] rc=[" + json.getRc() + "]");
		return json;
	}
	
	
}

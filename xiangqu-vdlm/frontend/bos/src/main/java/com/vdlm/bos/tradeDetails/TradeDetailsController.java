package com.vdlm.bos.tradeDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.User;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.vo.TradeDetailsVO;
import com.vdlm.service.account.SubAccountLogService;
import com.vdlm.service.excel.ExcelService;
import com.vdlm.service.user.UserService;

@Controller
public class TradeDetailsController {

	@Autowired
	private ExcelService excelService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private SubAccountLogService subAccountLogService;
	
	@RequestMapping(value = "tradeDetails/{userId}")
	public String list(@PathVariable("userId") String userId, Model model) {
		model.addAttribute("userId", userId);
		return "bank/tradeDetails";
	}

	@ResponseBody
	@RequestMapping(value = "tradeDetails/list")
	public Map<String, Object> querytradeDetailsList(TradeDetailsSearchForm form,
			Pageable pageable) {
		Map<String, Object> params = mergeQueryDetailsParams(form);
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> countMap = subAccountLogService
				.countTradeDetailsByUserId(params);
		User user = userService.loadByAdmin(form.getUserId());
		data.put("total", countMap.get("totalCount"));
		List<TradeDetailsVO> transDetails = subAccountLogService
				.queryTradeDetailsByUserId(params, pageable);
		data.put("rows", transDetails);

		BigDecimal pageAmount = new BigDecimal(0);
		for (int i = 0; i < transDetails.size(); i++) {
			TradeDetailsVO vo = transDetails.get(i);
			vo.setUserPhone(user.getPhone());
			pageAmount = pageAmount.add(vo.getAmount());
		}

		List<TradeDetailsVO> footer = new ArrayList<TradeDetailsVO>();
		TradeDetailsVO pageSum = new TradeDetailsVO();
		pageSum.setId("当前页金额");
		pageSum.setAmount(pageAmount);
		footer.add(pageSum);

		String totalAmount = countMap.get("totalAmount") == null ? "0"
				: countMap.get("totalAmount").toString();
		TradeDetailsVO totalSum = new TradeDetailsVO();
		totalSum.setId("总计金额");
		totalSum.setAmount(new BigDecimal(totalAmount));
		footer.add(totalSum);
		data.put("footer", footer);
		return data;
	}
	
	

	@RequestMapping(value = "tradeDetails/exportExcel")
	@ResponseBody
	public void export2Excel(
			TradeDetailsSearchForm form, HttpServletResponse resp) {
		Map<String, Object> params = mergeQueryDetailsParams(form);
		List<TradeDetailsVO> transDetails = subAccountLogService
				.queryTradeDetailsByUserId(params,null);
		String sheetStr = "用户明细";
		String filePrefix = "tradeDetails";
		String[] secondTitle = new String[]{"明细id","账户类型","资金操作类型","金额","交易单号","交易类型","支付渠道","支付状态","创建时间"};
		String[] strBody = new String[]{"getIdLong","getRemarkAccountType","getRemarkOpType","getAmount","getBizId","getRemarkPayType","getOutId","getOutStatus","getCreatedAtStr"};
		excelService.export(filePrefix, transDetails, TradeDetailsVO.class, sheetStr, transParams2Title(params), secondTitle, strBody, resp);
	}

	private Map<String, Object> mergeQueryDetailsParams(TradeDetailsSearchForm form) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(form.getUserId())) {
			params.put("userId", form.getUserId());
		}
		if (StringUtils.isNotBlank(form.getBizId())) {
			params.put("bizId", "%" + form.getBizId() + "%");
		}
		if (form.getAccountType() != null) {
			if (StringUtils.isNotBlank(form.getAccountType().toString())) {
				params.put("accountType", form.getAccountType());
			}
		}
		if (form.getPayType() != null) {
			if (StringUtils.isNotBlank(form.getPayType().toString())) {
				params.put("payType", form.getPayType());
			}
		}
		if (StringUtils.isNotBlank(form.getStartTime())) {
			params.put("startTime", form.getStartTime());
		}
		if (StringUtils.isNotBlank(form.getEndTime())) {
			params.put("endTime", form.getEndTime());
		}
		return params;
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
				if(key.equals("userId")){
					User user = userService.load(params.get("userId").toString());
					value = IdTypeHandler.decode(value.toString());
					if(user!=null){
						result += "{userId="+value+",userName="+user.getName()+",phone="+user.getPhone()+"}";
					}else{
						result += "{"+key+"="+value.toString()+"}";
					}
				}else{
					result += "{"+key+"="+value.toString()+"}";
				}
				i++;
			}
		}
		return result;
	}
}

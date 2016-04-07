package com.vdlm.bos.coupon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.CouponActivity;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.status.CouponStatus;
import com.vdlm.dal.type.CouponGrantRule;
import com.vdlm.dal.type.CouponPlatform;
import com.vdlm.dal.type.CouponType;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.excel.ExcelService;
import com.vdlm.service.pricing.CouponService;

@Controller
public class CouponController extends BaseController {

	@Autowired
	private CouponService couponService;

	@Autowired
	private ExcelService excelService;

	@RequestMapping("coupon")
	public String list(Model model, HttpServletRequest req) {
		return "coupon/coupon";
	}

	@ResponseBody
	@RequestMapping(value = "coupon/list")
	public Map<String, Object> list(CouponSearchForm form, Pageable pageable) {
//		Map<String, Object> params = new HashMap<String, Object>();
//		if (StringUtils.isNotBlank(form.getCode_kwd())) {
//			params.put("code", "%" + form.getCode_kwd() + "%");
//		}
//		if (StringUtils.isNotBlank(form.getActivity_title())) {
//			params.put("activityid", form.getActivity_title());
//		}
//		if (StringUtils.isNotBlank(form.getValid1_kwd())) {
//			params.put("validfrom", form.getValid1_kwd());
//		}
//		if (StringUtils.isNotBlank(form.getValid2_kwd())) {
//			try {
//				Date date = DateUtils.addDays(DateUtils.parseDate(form.getValid2_kwd(), "yyyy-MM-dd"), 1);
//				params.put("validto", date);
//			} catch (ParseException e) {
//			
//			}
//		}
//		if (StringUtils.isNotBlank(form.getStatus_kwd())) {
//			params.put("status", form.getStatus_kwd());
//		}
//		if (StringUtils.isNotBlank(form.getCashieritem1_kwd())) {
//			params.put("cashieritemfrom", form.getCashieritem1_kwd());
//		}
//		if (StringUtils.isNotBlank(form.getCashieritem2_kwd())) {
//			try {
//				Date date = DateUtils.addDays(DateUtils.parseDate(form.getCashieritem2_kwd(), "yyyy-MM-dd"), 1);
//				params.put("cashieritemto", date);
//			} catch (ParseException e) {
//			
//			}
//		}
//
//		List<Coupon> coupons = couponService.listCouponsByAdmin(params,
//				pageable);
//
		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("total", couponService.countCouponsByAdmin(params));
//		data.put("rows", coupons);

		return data;
	}

	@ResponseBody
	@RequestMapping(value = "coupon/list/exportExcel")
	public void export2Excel(CouponSearchForm form, HttpServletResponse resp) {
		Map<String, Object> params = transForm2Map(form);
		List<Coupon> coupons = couponService.listByAdmin(params, null);
		String sheetStr = "优惠码";
		String filePrefix = "couponList";
		String[] secondTitle = new String[] { "优惠码", "优惠金额", "有效开始时间",
				"有效结束时间", "状态", "创建时间", "付款单号", "付款时间" };
		String[] strBody = new String[] { "getCode", "getDiscount",
				"getValidFromStr", "getValidToStr", "getStatusVal",
				"getCreatedAtStr", "getBizNo",
				"getCashieritemCreatedAtStr" };
		excelService.export(filePrefix, coupons, Coupon.class, sheetStr,
				transParams2Title(params), secondTitle, strBody, resp);
	}

	private Map<String, Object> transForm2Map(CouponSearchForm form) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(form.getCode_kwd())) {
			params.put("code", "%" + form.getCode_kwd() + "%");
		}
		if (StringUtils.isNotBlank(form.getActivity_title())) {
			params.put("activityid", form.getActivity_title());
		}
		if (StringUtils.isNotBlank(form.getValid1_kwd())) {
			params.put("validfrom", form.getValid1_kwd());
		}
		if (StringUtils.isNotBlank(form.getValid2_kwd())) {
			params.put("validto", form.getValid2_kwd());
		}
		if (StringUtils.isNotBlank(form.getStatus_kwd())) {
			params.put("status", form.getStatus_kwd());
		}
		if (StringUtils.isNotBlank(form.getCashieritem1_kwd())) {
			params.put("cashieritemfrom", form.getCashieritem1_kwd());
		}
		if (StringUtils.isNotBlank(form.getCashieritem2_kwd())) {
			params.put("cashieritemto", form.getCashieritem2_kwd());
		}
		return params;
	}

	private String transParams2Title(Map<String, Object> params) {
		String result = "";
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				Object value = params.get(key);
				if (value != null && !value.equals("")) {
					if (i > 0) {
						result += ";";
					}
					result += "{" + key + "=" + value.toString() + "}";
					i++;
				}
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "coupon/create")
	public Json create(CouponCreateForm form, String count) {
		List<String> listCodes = new ArrayList<String>(Integer.parseInt(count));
		for (int i = 0; i < Integer.parseInt(count); i++) {
			// listCodes.add(UUID.randomUUID().toString()); //TODO 生成方式
			listCodes.add(change(IdTypeHandler.encode((System
					.currentTimeMillis() + 100 + i))));
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("discount", form.getDiscount());
		params.put("activityid", couponService.loadByActCode("XQ.COUPONCODE")
				.getId());
		params.put("validfrom", form.getValidFrom() + " 00:00:00");
		params.put("validto", form.getValidTo() + " 23:59:59");
		params.put("status", CouponStatus.VALID);
		params.put("createdat", DateFormatUtils.format(Calendar.getInstance(),
				"yyyy-MM-dd HH:mm:ss"));
		Json json = new Json();
		try {
			int ret = 0;
			ret = couponService.create(listCodes, params);
			if (ret > 0) {
				json.setMsg("成功生成 " + ret + " 条优惠码");
			} else {
				json.setRc(Json.RC_FAILURE);
				json.setMsg("生成失败");
			}
		} catch (Exception e) {
			log.error("生成失败:e=[{}]",e);
			json.setRc(Json.RC_FAILURE);
			json.setMsg("生成失败;" + e.getMessage());
		}
		log.info(super.getCurrentUser().getId() + " rc=[" + json.getRc() + "]");
		return json;
	}

	@ResponseBody
	@RequestMapping(value = "coupon/delete")
	public Json delete(String[] ids) {
		Json json = new Json();
		try {
			if (couponService.delete(ids))
				json.setMsg("删除成功");
			else {
				json.setRc(Json.RC_FAILURE);
				json.setMsg("删除失败");
			}

		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("删除失败;" + e.getMessage());
		}
		log.info(super.getCurrentUser().getId() + "优惠码id=[" + ids + "] rc=["
				+ json.getRc() + "]");
		return json;
	}

	@ResponseBody
	@RequestMapping(value = "coupon/activity")
	public Map<String, Object> listCouponActivity() {
		Map<String, Object> data = new HashMap<String, Object>();

//		List<CouponActivity> list = new ArrayList<CouponActivity>();
//		list = couponService.listCouponActivityByAdmin();
//		data.put("rows", list);

		return data;
	}

	/*
	 * 奇数字符随机替换成0-9数字/A-Z字符偶数字符替换成后2位的字符
	 */
	public static String change(String str) {
		char[] chr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		char letters[] = new char[str.length()];
		Random random = new Random();
		for (int i = 0; i < str.length(); i++) {
			char letter = str.charAt(i);
			// 奇偶数替换
			if (i % 2 == 0) {
				if (i < str.length() - 2)
					letter = str.charAt(i + 2);
				// 替换大小写
				if (letter >= 'a' && letter <= 'z')
					letter = (char) (letter - 32);
				else if (letter >= 'A' && letter <= 'Z')
					letter = (char) (letter + 32);
			} else {
				letter = chr[random.nextInt(36)];
			}
			letters[i] = letter;
		}
		return new String(letters);
	}
	
	@RequestMapping(value = "couponActivity")
	public String couponActivityList(Model model, HttpServletRequest req) {
		return "couponActivity/couponActivity";
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/list")
	public Map<String, Object> getCouponActivityList(
			CouponActivity couponActivity, Pageable page, String statusKwd) {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		if (!StringUtils.isBlank(couponActivity.getTitle())) {
			params.put("title", couponActivity.getTitle());
		}
		if (!StringUtils.isBlank(couponActivity.getId())) {
			params.put("id", couponActivity.getId());
		}
		if (couponActivity.getCouponType() != null) {
			params.put("couponType", couponActivity.getCouponType());
		}
		if (couponActivity.getTypeId() != null) {
			params.put("typeId", couponActivity.getTypeId());
		}
		if (couponActivity.getValidFrom() != null) {
			params.put("validFrom", couponActivity.getValidFrom());
		}
		if (couponActivity.getValidTo() != null) {
			params.put("validTo", couponActivity.getValidTo());
		}
		if (!StringUtils.isBlank(statusKwd)) {
			params.put("valid", statusKwd);
		}
		
		if (couponActivity.getValidFrom() != null && couponActivity.getValidTo() != null) {
			if (couponActivity.getValidFrom().after(couponActivity.getValidTo())) {
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "有效日期填写有误");
			}
		}
		try {
			List<CouponActivity> array = this.couponService.getCouponActivityArray(params, page, true);
			data.put("rows", array);
			data.put("total", this.couponService.countCouponActivityArray(params));
		} catch(Exception e) {
			throw e;
		}
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/distinctAttribute/list")
	public Map<String, Object> getCouponActivityTitleArray() {
		Map<String, Object> data = new HashMap<String, Object>();
		List<CouponActivity> array = this.couponService.getCouponActivityArray(new HashMap<String, Object>(), null, false);
		Set<String> titles = new HashSet<String>();
		Set<CouponType> couponTypes = new HashSet<CouponType>();
		Set<Integer> typeIds = new HashSet<Integer>();
		if (array != null && array.size() > 0) {
			for (int i = 0, size = array.size(); i < size; i++) {
				if (!StringUtils.isBlank(array.get(i).getTitle())) {
					titles.add(array.get(i).getTitle());
				}
				if (array.get(i).getCouponType() != null) {
					couponTypes.add(array.get(i).getCouponType());
				}
				if (array.get(i).getTypeId() != null) {
					typeIds.add(array.get(i).getTypeId());
				}
			}
		}
		data.put("titles", titles);
		data.put("couponTypes", couponTypes);
		data.put("typeIds", typeIds);
		return data;
	}
	
	@RequestMapping(value = "couponActivity/add/couponActivity")
	public String addCouponActivity() {
		return "couponActivity/addCouponActivity";
	}
	
	@RequestMapping(value = "couponActivity/edit/couponActivity")
	public String editCouponActivity(Integer id, Model model) {
		CouponActivity couponActivity = this.couponService.getCouponActivityById(id);
		model.addAttribute("couponActivity", couponActivity);
		if (!StringUtils.isBlank(couponActivity.getUseConstraint())) {
			String useConstraint = couponActivity.getUseConstraint();
			if (useConstraint.contains("<")) {
				String unType = useConstraint.substring(useConstraint.indexOf("<") + 1, useConstraint.lastIndexOf(">"));
				unType = unType.replaceAll(",", "#");
				model.addAttribute("unType", unType);
			} 
			if (!useConstraint.contains("<") && useConstraint.contains("{")) {
				String unId = useConstraint.substring(useConstraint.indexOf("{") + 1, useConstraint.lastIndexOf("}"));
				unId = unId.replaceAll(",", "#");
				model.addAttribute("unId", unId);
			}
			if (useConstraint.contains("(")) {
				String typeMax = useConstraint.substring(useConstraint.indexOf("(") + 1, useConstraint.lastIndexOf(")"));
				model.addAttribute("typeMax", typeMax);
			}
			if (useConstraint.contains("[")) {
				String idMax = useConstraint.substring(useConstraint.indexOf("[") + 1, useConstraint.lastIndexOf("]"));
				model.addAttribute("idMax", idMax);
			}  
		}
		if (couponActivity.getTimeLimit() != null) {
			double timeLimit = couponActivity.getTimeLimit() / (3600 * 1000 * 1.0);
			model.addAttribute("timeLimit", timeLimit);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (couponActivity.getValidFrom() != null) {
			model.addAttribute("validFromText", format.format(couponActivity.getValidFrom()));
		}
		if (couponActivity.getValidTo() != null) {
			model.addAttribute("validToText", format.format(couponActivity.getValidTo()));
		}
		return "couponActivity/editCouponActivity";
	}
	
	//TODO  临时处理 
	private LinkedHashMap<String, String> staticChannels = new LinkedHashMap<String, String>(){{
		put("mengxiang1", "梦象1");
		put("mengxiang2", "梦象2");
		put("mengxiang3", "梦象3");
		put("mengxiang4", "梦象4");
		put("360sjzs", "360手机助手");
		put("anzhisc", "anzhisc");
		put("baidusjzs", "百度手机助手");
		put("huaweiyysc", "华为应用市场");
		put("kckj", "kckj");
		put("leshangd", "乐商店");
		put("meizu", "魅族");
		put("nearme", "nearme");
		put("sina", "新浪");
		put("UC", "UC");
		put("wandj", "豌豆荚");
		put("xiaomiyysd", "小米应用商店");
		put("yingyb", "应用宝");
		put("andj_tag", "andj_tg");
		put("getui", "getui");
		put("HaoSheJi", "HaoSheJi");
		put("liebao1", "liebao1");
		put("liebao2", "liebao2");
		put("liebao3", "liebao3");
		put("xushushu", "xushushu");
		put("yyb_cpd", "yyb_cpd");
		put("puluigin", "puluigin");
		put("quewo", "quewo");
		put("hlhz", "hlhz");
		put("chuizi", "锤子");

	}};
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/add/optionInfo/list")
	public Map<String, Object> getCouponActivityAddOptionInfoArray() {
		return getCouponActivityOptinInfoArray();
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/edit/optionInfo/list") 
	public Map<String, Object> getCouponActivityEditOptionInfoArray() {
		return getCouponActivityOptinInfoArray();
	}
	
	private Map<String, Object> getCouponActivityOptinInfoArray() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("couponTypes", CouponType.values());
		data.put("grantRules", CouponGrantRule.values());
		data.put("platforms", CouponPlatform.values());
		data.put("channels", staticChannels);
		return data;
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/add/doAddCouponActivity")
	public Json doAddCouponActivity(
			CouponActivity couponActivity, String validFromText, String validToText,
			String unType, String unId, String typeMax, String idMax) {
		Json json = new Json();
		try {
			if (couponActivity.getTimeLimit() != null && couponActivity.getTimeLimit() > 0) {
				couponActivity.setTimeLimit(couponActivity.getTimeLimit() * 3600 * 1000);
			}
			this.couponService.addCouponActivity(couponActivity, validFromText, validToText, unType, unId, typeMax, idMax);
			json.setMsg("创建红包成功");
		} catch(Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("创建红包失败：" + e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/edit/doEditCouponActivity")
	public Json doEditCouponActivity(
			CouponActivity couponActivity, String timeLimitStr, String validFromText, String validToText,
			String unType, String unId, String typeMax, String idMax) {
		Json json = new Json();
		try {
			if (!StringUtils.isBlank(timeLimitStr)) {
				double timeLimit = Double.parseDouble(timeLimitStr);
				couponActivity.setTimeLimit((long) (timeLimit * 3600 * 1000));
			}
			this.couponService.updateCouponActivity(couponActivity, validFromText, validToText, unType, unId, typeMax, idMax);
			json.setMsg("更新红包成功");
		} catch(Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("更新红包失败：" + e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value ="couponActivity/delete")
	public Json delCouponActivity(String ids) {
		Json json = new Json();
		try {
			this.couponService.delCouponActivity(ids.split("#"));
			json.setMsg("删除红包成功");
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("删除红包失败：" + e.getMessage());
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "couponActivity/close") 
	public Json closeCouponActivity(Integer id) {
		Json json = new Json();
		try {
			this.couponService.closeCouponActivity(id);
			json.setMsg("关闭红包成功");
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("关闭红包失败：" + e.getMessage());
		}
		return json;
	}
}

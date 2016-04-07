package com.vdlm.bos.activity;

import io.netty.util.internal.ObjectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.vdlm.ResponseObject;
import com.vdlm.bos.BaseController;
import com.vdlm.dal.model.ActivityTicket;
import com.vdlm.dal.model.CampaignProduct;
import com.vdlm.dal.model.PreferentialType;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.ActivityTicketAuditStatus;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.activity.ActivityVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.utils.excel.ExcelReader;

@Controller
public class ActivityController extends BaseController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ActivityService activityService;

	/**
	 * 跳转页面 活动页
	 */
	@RequestMapping(value = "activity")
	public String list(Model model, HttpServletRequest req) {
		return "activity/activity";
	}

	@RequestMapping(value = "toAddProduct")
	public String listTicket(Model model, HttpServletRequest req) {
		return "activity/addProduct";
	}

	/**
	 * 列出所有公共活动
	 */
	@RequestMapping("/activity/list")
	@ResponseBody
	public Map<String, Object> list() {
		User user = getCurrentUser();
		List<ActivityVO> list = activityService.listPublicAndPrivate(user.getId(), user.getShopId());
		if (list != null) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("total", list.size());
			data.put("rows", list);
			return data;
		}
		return null;
	}

	/**
	 * 活动中增加商品
	 */
	@RequestMapping(value = "/activity/products/addByAdmin", method = RequestMethod.POST)
	@ResponseBody
	public String addProducts(@RequestParam(value = "id") String id, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, MultipartHttpServletRequest request) {
		try {
			MultipartFile excTable = request.getFile("excTable");
			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
			Date start = format.parse(startTime.replace('T', ' '));
			Date end = format.parse(endTime.replace('T', ' '));
			ActivityVO activity = activityService.loadVO(id);
			if (activity == null) {
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动不存在");
			}
			if (ActivityStatus.CLOSED.equals(activity.getStatus())) {
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动已经关闭，不能添加商品");
			}
			Map<CampaignProduct, ActivityTicket> pData = this.buildCampaignProduct(excTable.getInputStream(), id, start,
					end);
			this.activityService.addProductsToActivity(pData, true);
			return "success";
		} catch (IOException e) {
			throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "读取商品表格出错");
		} catch (Exception e) {
			if (e != null && !StringUtils.isEmpty(e.getMessage()))
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "错误:" + e.getMessage());
			else
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "未知错误");
		}
	}

	protected Map<CampaignProduct, ActivityTicket> buildCampaignProduct(InputStream excTable, String activityId,
			Date startTime, Date endTime) {
		Map<CampaignProduct, ActivityTicket> pData = new HashMap<CampaignProduct, ActivityTicket>();
		ExcelReader excel = new ExcelReader(excTable);
		List<Map<Integer, String>> data = excel.readExcelContent();
		for (Map<Integer, String> map : data) {
			CampaignProduct product = new CampaignProduct();
			String productId = map.get(0);
			if (StringUtils.isEmpty(productId)) break;
			Integer type = Float.valueOf(map.get(1)).intValue();
			Float reduction = 0f;
			Float discount = 10f;
			if (productId == null)
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "有商品ID为空");
			Product p = this.productService.findProductById(productId);
			if (p == null)
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "有商品ID无法解析，请确认ID正确,ID:" + productId);
			/**
			 * 判断商家参与其他活动的状态
			 **/
			if (this.activityService.existProductInRange(startTime, endTime, activityId, p.getId())) {
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "商品参与活动时间有重复,ID：" + p.getId());
			}
			ActivityTicket ticket = new ActivityTicket();
			ticket.setActivityId(activityId);
			ticket.setAuditStatus(ActivityTicketAuditStatus.APPROVED);
			ticket.setCreatedAt(new Date());
			ticket.setShopId(p.getShopId());
			ticket.setStatus(ActivityStatus.NOT_STARTED);
			//优惠方式 统一修改成减价方式
			ticket.setPreferentialType(3);
			ticket.setStartTime(startTime);
			ticket.setEndTime(endTime);
			if (type == PreferentialType.PRODUCT_REDUCTION_PRICE) {
				reduction = Float.valueOf(map.get(2));
				if (reduction <= 0)
					throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "商品减价不合适, 减价值需大于0并小于商品原价,ID:" + productId);
				product.setReduction(reduction);
			} else if (type == PreferentialType.ACTIVITY_PRODUCT_DISCOUNT) {
				discount = Float.valueOf(map.get(3));
				if (discount <= 0.0 || discount >= 1.0)
					throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "商品折扣不合适,折扣值需在0到1之间,ID:" + productId);
//				ticket.setDiscount(discount);
//				product.setDiscount(discount);  // 现有活动模型不支持一个活动+店铺有多个报名记录(submit), 改为减价方式可以解决该问题
				Float value = p.getMarketPrice().subtract(p.getMarketPrice().multiply(new BigDecimal(discount))).floatValue();
				product.setReduction((Float)ObjectUtils.defaultIfNull(value, .0f));
			}

			Integer freeShipping = Float.valueOf(map.get(4)).intValue();
			product.setFreeShipping(freeShipping);
			product.setActivityId(activityId);
			product.setProductId(productId);
			pData.put(product, ticket);
		}
		return pData;
	}

	/**
	 * 活动中去掉商品 需要参数 活动ID 和需要删除的商品ID
	 * 
	 * @return
	 */
	@RequestMapping("/activity/products/remove")
	@ResponseBody
	public ResponseObject<Boolean> removeProducts(@ModelAttribute ActivityProductRemoveForm form) {
		activityService.removeProducts(form.getId(), form.getProductIds(), true);
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}

	///////////////////// 其他未用接口 具体使用需要具体修改
	/**
	 * 活动详情
	 */
	@RequestMapping("/activity/detail")
	@ResponseBody
	public ResponseObject<ActivityVO> view(@RequestParam String id) {
		ActivityVO activity = activityService.loadVO(id);
		return new ResponseObject<ActivityVO>(activity);
	}

}

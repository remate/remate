package com.vdlm.web.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;

import com.vdlm.dal.model.CashierItem;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.Order;
import com.vdlm.dal.status.CouponStatus;
import com.vdlm.dal.status.OrderStatus;
import com.vdlm.dal.status.PaymentStatus;
import com.vdlm.dal.type.CashierType;
import com.vdlm.dal.type.PaymentChannel;
import com.vdlm.dal.type.PaymentMode;
import com.vdlm.service.cashier.CashierService;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.pricing.CouponVO;
import com.vdlm.service.pricing.PricingService;
import com.vdlm.service.pricing.vo.PricingResultVO;
import com.vdlm.utils.UserAgent;
import com.vdlm.utils.UserAgentUtils;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;

@Controller
public class CouponController extends BaseController {

	@Autowired
    private CouponService couponService;
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
    private PricingService pricingService;
	
	@Autowired
    private CashierService cashierService;
	
	@Autowired
	private RestOperations restTemplate;
	
    @Value("${xiangqu.web.site}")
	private String xiangquWebSite;

    @ResponseBody
    @RequestMapping("/coupon/{orderId}/save")
    public ResponseObject<Coupon> save(@PathVariable("orderId") String orderId, @ModelAttribute CouponForm form) {
    	Coupon coupon = null;
    	if(StringUtils.isBlank(form.getCouponType())){
    		coupon = new CouponVO();
    	}
    	
    	if("XQ.FIRST".equals(form.getCouponType())){
    		//首单减5元
    		coupon = couponService.load(form.getCouponId());
    		if(coupon == null || (StringUtils.isNoneBlank(coupon.getUserId()) && !getCurrentUser().getId().equals(coupon.getUserId()) ) || coupon.getStatus() != CouponStatus.VALID ){
        		return new ResponseObject<Coupon>("优惠码不存在或者已经被使用！", GlobalErrorCode.INVALID_ARGUMENT);
        	}
    	}else if("XQ.COUPONCODE".equals(form.getCouponType())){
    		//优惠码
    		coupon = couponService.loadByCouponCode(form.getCouponType(), form.getDiscountCode());
    		if(coupon == null || (StringUtils.isNoneBlank(coupon.getUserId()) && !getCurrentUser().getId().equals(coupon.getUserId()) ) || coupon.getStatus() != CouponStatus.VALID ){
        		return new ResponseObject<Coupon>("优惠码不存在或者已经使用！", GlobalErrorCode.INVALID_ARGUMENT);
        	}
    		couponService.grantCoupon(coupon, getCurrentUser().getId());
    	}else if("XQ.HONGBAO".equals(form.getCouponType())){
    		BigDecimal discount = new BigDecimal(form.getDiscount()).setScale(2, BigDecimal.ROUND_DOWN);
    		coupon = couponService.grantExtCoupon(form.getCouponType(), form.getExtHongbaoId(), discount, getCurrentUser().getId());
    	}
    	
    	//更新order表
  		Order order = orderService.load(orderId);
  		if(order.getStatus() == OrderStatus.SUBMITTED){
  			PricingResultVO pricing = pricingService.calculate(orderId, coupon.getId());
	  		order = new Order();
	  		order.setId(orderId);
	  		order.setDiscountFee(pricing.getDiscountFee());
	  		order.setTotalFee(pricing.getTotalFee());
	  		orderService.update(order);
	  		
	  		order = orderService.load(orderId);
	  		
	  		//更新收银台
	  		cashierService.save(createCashier(coupon, order), order.getOrderNo(), PaymentChannel.PARTNER);
  		}
		return new ResponseObject<Coupon>(coupon);
    }
    
    private List<CashierItem> createCashier(Coupon coupon, Order order) {
		List<CashierItem> items = new ArrayList<CashierItem>();
		if(coupon == null || coupon.getId() == null)
			return items;
		
//		BigDecimal totalFee = order.getTotalFee();
		BigDecimal discount = coupon.getDiscount();
		CashierItem couponItem = new CashierItem();
		couponItem.setBizNo(order.getOrderNo());
		couponItem.setPaymentChannel(PaymentChannel.PLATFORM);
		if(order.getDiscountFee().compareTo(discount)==-1){
			couponItem.setAmount(order.getDiscountFee());
		}else{
			couponItem.setAmount(discount);
		}
		couponItem.setCouponId(coupon.getId());
		couponItem.setBankCode("");
		couponItem.setBankName("");
		couponItem.setAgreementId(null);
		couponItem.setStatus(PaymentStatus.PENDING);
		couponItem.setPaymentMode(PaymentMode.XIANGQU);
		items.add(couponItem);
        return items;
	}
    
    
    @RequestMapping("/coupon")
    public String add(String price, String orderNo, Model model, HttpServletRequest request) {
    	BigDecimal bPrice = NumberUtils.createBigDecimal(price).setScale(2, BigDecimal.ROUND_DOWN);
    	UserAgent ua = UserAgentUtils.getUserAgent(request);
    	Map<String, List<CouponVO>> coupons = couponService.listValidsWithExt(bPrice, orderNo, ua.getChannel());
        model.addAttribute("coupons", couponService.dealCoupons(coupons));
        return "xiangqu/coupon";
    }
    
    /**
     * 通过优惠码获取优惠码对象
     * */
    @ResponseBody
    @RequestMapping("/coupon/detail")
    public ResponseObject<Coupon> detail(String code){
    	String userId = getCurrentUser().getId();
    	Coupon coupon = couponService.obtainCoupon(code);
    	if(coupon == null || coupon.getStatus() == CouponStatus.USED){
    		return new ResponseObject<Coupon>("优惠码不存在或者已经被使用！", GlobalErrorCode.INVALID_ARGUMENT);
    	} else if( StringUtils.isNoneBlank(coupon.getUserId()) && !userId.equals(coupon.getUserId()) ){
    		return new ResponseObject<Coupon>("优惠码已经被使用！" + coupon.getCode(), GlobalErrorCode.INVALID_ARGUMENT);
    	} else{
    		return new ResponseObject<Coupon>(coupon);
    	}
    }
}

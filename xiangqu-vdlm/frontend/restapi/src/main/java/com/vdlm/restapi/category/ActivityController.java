package com.vdlm.restapi.category;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.Activity;
import com.vdlm.dal.model.ActivityTicket;
import com.vdlm.dal.model.CampaignProduct;
import com.vdlm.dal.model.PreferentialType;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.ActivityTicketAuditStatus;
import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.dal.type.ActivityType;
import com.vdlm.dal.vo.ActivityEX;
import com.vdlm.dal.vo.CampaignProductEX;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.activity.ActivityVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.wordnik.swagger.annotations.Api;

@Api(value="activity")
@Controller
public class ActivityController extends BaseController {

    @Autowired
    private ResourceFacade resourceFacade;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private ShopService shopService;
    
    @Value("${site.web.host.name}")
    private String siteHost;
    
    private void checkActivityForm(ActivitySaveForm form) {
    	if (form  == null)
    		throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "参数非法");
    	if (form.getStartTime() != null)
            if (form.getStartTime() <= new Date().getTime())
                throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动开始时间不能早于当前时间");
        if (form.getStartTime() - new Date().getTime() < 10 * 60 * 1000)
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动开始时间必须至少10分钟之后");
        if (form.getEndTime() - form.getStartTime() < 10 * 60 * 1000)
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动持续时间不得小于10分钟");
        if (form.getDiscount() != null && (form.getDiscount() <= 0 || form.getDiscount() > 1))
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "折扣必须是大于0小于1的数值");
        if (form.getPreferentialType() == null)
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "没有优惠类型信息");
        if (form.getPreferentialType() == PreferentialType.SHOP_DISCOUNT) {
        	if (form.getDiscount() == null)
        		throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "全店活动优惠信息不完整");
        }
    }
    
    /**
     * 卖家用户创建活动基本信息保存
     * @param form
     * @return
     */
    @RequestMapping("/activity/save")
    @ResponseBody
    public ResponseObject<ActivityVO> save(@Valid @ModelAttribute ActivitySaveForm form, Errors errors) {
    	ControllerHelper.checkException(errors);
    	checkActivityForm(form);
        if (form.getPreferentialType() == PreferentialType.SHOP_DISCOUNT) {
	    	List<ActivityTicket> list = activityService.selectConflictByShopId(getCurrentUser().getShopId(), form.getStartTime(), form.getEndTime());
	    	if (list != null && list.size() != 0)
	    		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "全店活动时间与已有活动时间有冲突");
        }
        if (PreferentialType.ACTIVITY_PRODUCT_DISCOUNT == form.getPreferentialType() && form.getDiscount() == null)
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "没有优惠信息");
        Activity activity = transForm2Activity(form);
        if(activity == null)
        	throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动Type类型异常");
    	Shop shop = shopService.findByUser(activity.getCreatorId());
    	if (shop == null) 
    		throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "店铺信息异常");
    	
    	ActivityTicket ticketInfo =  new ActivityTicket();
    	BeanUtils.copyProperties(form, ticketInfo);
    	activity = activityService.create(activity, ticketInfo, shop);
        
        ActivityVO vo = activityService.loadVO(activity.getId());
        return new ResponseObject<ActivityVO>(vo);
    }
    
    /**
     * 卖家用户创建活动基本信息更新
     */
    @RequestMapping("/activity/update")
    @ResponseBody
    public ResponseObject<ActivityVO> update(@Valid @ModelAttribute ActivitySaveForm form, Errors errors) {
    	ControllerHelper.checkException(errors);
    	checkActivityForm(form);
    	
        Activity activity = transForm2Activity(form);
        if(activity == null)
        	throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动Type类型异常");
    	Shop shop = shopService.findByUser(activity.getCreatorId());
    	if (shop == null) 
    		throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "店铺信息异常");
        
    	ActivityTicket ticketInfo =  new ActivityTicket();
    	BeanUtils.copyProperties(form, ticketInfo);
    	activity = activityService.update(activity, ticketInfo, shop);
        
        ActivityVO result = activityService.loadVO(activity.getId());
        return new ResponseObject<ActivityVO>(result);
    }
    
    /**
     * 活动中我的所有可供选择的商品
     */
    @RequestMapping("/activity/products/available")
    @ResponseBody
    public ResponseObject<List<CampaignProductVO>> availableProducts(@RequestParam String id, String channel, Pageable pager,String key) {
        User user = getCurrentUser();
        
        List<CampaignProduct> camProducts = activityService.listCampaignProducts(id);
        Map<String, CampaignProduct> prodMap = new HashMap<String, CampaignProduct>();
        for (CampaignProduct cp : camProducts) {
            prodMap.put(cp.getProductId(), cp);
        }
        
        List<CampaignProductVO> result = new ArrayList<CampaignProductVO>();
        
        List<Product> products = productService.listProductsAvailableByChannel(id,user.getShopId(), pager, channel,key);

        // TODO 有问题，如果只是复制
        for (Product p : products) {
            if (p.getMarketPrice() == null) {
                p.setMarketPrice(p.getPrice());
            }
            
            CampaignProductVO cpv = new CampaignProductVO();
            CampaignProduct cp = prodMap.get(p.getId());
            if (cp != null) {
                BeanUtils.copyProperties(cp, cpv);
            }

            BeanUtils.copyProperties(p, cpv);
            
            cpv.setImgUrl(p.getImg());
            result.add(cpv);
        }
        return new ResponseObject<List<CampaignProductVO>>(result);
    }
    
    /**
     * 活动中我的商品
     */
    @RequestMapping("/activity/products/campaign")
    @ResponseBody
    public ResponseObject<List<CampaignProductVO>> campaignProducts(@RequestParam String id) {
    	User user = getCurrentUser();
        List<CampaignProductEX> cps = activityService.loadCampaignProductEX(id, user.getShopId());
        
        List<CampaignProductVO> result = new ArrayList<CampaignProductVO>();
        for (CampaignProductEX cp : cps) {
            if (!ActivityStatus.CLOSED.equals(cp.getStatus()) && cp.getArchive() == true && !ActivityTicketAuditStatus.REJECTED.equals(cp.getAuditStatus())) {
            	// 审核拒绝也会删除商品,但还是需要被展示出来
            	continue;
            }
            CampaignProductVO cpv = new CampaignProductVO();
            BeanUtils.copyProperties(cp, cpv);
            
            Product p = productService.findProductById(cp.getProductId());
            if (p == null)	 //throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "商品不存在");
            	continue;
            if (p.getMarketPrice() == null) {
                p.setMarketPrice(p.getPrice());
            }
            BeanUtils.copyProperties(p, cpv);

            cpv.setFeedback(cp.getAuditReason());
            cpv.setImgUrl(p.getImg());
            result.add(cpv);
        }
        return new ResponseObject<List<CampaignProductVO>>(result);
    }

    /**
     * 客户端首页获取的所有的活动数据(包含公共数据和私有数据)
     * @param pager
     * @return
     */
    @RequestMapping("/activity/list")
    @ResponseBody
    public ResponseObject<List<ActivityVO>> list(Pageable pager) {
        User user = getCurrentUser();
        List<ActivityVO> list = activityService.listPublicAndPrivate(user.getId(),user.getShopId());
        Iterator<ActivityVO> iter = list.iterator();
        while (iter.hasNext()) {  // TODO: 配置 暂不开放商家期限public活动
        	ActivityVO vo = iter.next();
        	if (ActivityType.PUBLIC.equals(vo.getType())) {
        		iter.remove();
        	}
        }
        return new ResponseObject<List<ActivityVO>>(list);
    }
    
    /**
     * 平台活动
     * @param id
     * @param req
     * @param pageable
     * @return
     */
    /*@RequestMapping("/activity/pub-list")
    @ResponseBody
    public ResponseObject<List<ActivityVO>> pubList(Pageable pager) {
        return new ResponseObject<List<ActivityVO>>(activityService.listPublic());
    }*/
    
    /**
     * 我创建的活动
     */
    /*@RequestMapping("/activity/my-list")
    @ResponseBody
    public ResponseObject<List<ActivityVO>> mine(Pageable pager) {
        User user = getCurrentUser();
        return new ResponseObject<List<ActivityVO>>(activityService.listByUser(user.getId()));
    }*/
    
    /**
     * 活动详情
     */
    @RequestMapping("/activity/detail")
    @ResponseBody
    public ResponseObject<ActivityVO> view(@RequestParam String id) {
        ActivityVO activity = activityService.loadVO(id);
        return new ResponseObject<ActivityVO>(activity);
    }
    
    /**
     * 关闭活动
     */
    /*@RequestMapping("/activity/close")
    @ResponseBody
    public ResponseObject<Boolean> close(@RequestParam String id) {
        activityService.close(id);
        return new ResponseObject<Boolean>(Boolean.TRUE);
    }*/
    
    /**
     * 删除活动
     */
    @RequestMapping("/activity/delete")
    @ResponseBody
    public ResponseObject<Boolean> delete(@RequestParam String id) {
        return new ResponseObject<Boolean>(activityService.delete(id) > 0 ? true : false);
    }
    
    /**
     * 活动中增加商品
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/activity/products/add")
    @ResponseBody
    public ResponseObject<Boolean> addProducts(@ModelAttribute ActivityProductForm form) {
    	User user = getCurrentUser();
    	Shop shop = shopService.findByUser(user.getId());
    	Activity activity = activityService.selectByPrimaryKey(form.getId());
        ActivityEX activityEX = activityService.loadActivityEx(form.getId(), shop.getId(), activity.getType());
        
        if (activityEX == null || activityEX.getPreferentialType() == null) {
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动信息不完整");
        }
        // 私有活动其实最好不要限制, 但现在三方活动是通过私有活动来配置的, 先加上限制
        if (ActivityStatus.IN_PROGRESS.equals(activity.getStatus()) && ActivityType.PRIVATE.equals(activity.getType())) {
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动已经开始，不能添加商品");
        }
        if (ActivityStatus.CLOSED.equals(activity.getStatus())) {
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动已经关闭，不能添加商品");
        }
        ActivityTicket ticket = activityService.getDefaultJoinActivityTicket(form.getId());
        if (ticket == null) {
            throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "您还没有参加该活动，请从活动页面的报名按钮进入");
        }
        
        for (CampaignProduct prod : form.getProducts()) {
            if (activityEX.getPreferentialType().equals(PreferentialType.ACTIVITY_PRODUCT_DISCOUNT)) {
                prod.setDiscount(activityEX.getDiscount());
            }
            if (activityEX.getPreferentialType().equals(PreferentialType.PRODUCT_REDUCTION_PRICE)) {
            	if (prod.getReduction() == null || prod.getReduction() <= 0)  // TODO or >= orig_price 
            		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "部分商品减价不合适, 减价值需大于0并小于商品原价");
            }
            if (activityEX.getPreferentialType().equals(PreferentialType.ACTIVITY_PRODUCT_DISCOUNT)) {
            	if (prod.getDiscount() == null || prod.getDiscount() <= 0.0 || prod.getDiscount() >= 1.0)  // TODO or >= orig_price 
            		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "部分商品折扣不合适,折扣值需在1到10之间");
            }
            prod.setActivityId(form.getId());
            prod.setTicketId(ticket.getId());
        }
        
        activityService.addProductToPrivateAct(ticket, user.getShopId(), form.getProducts());

        return new ResponseObject<Boolean>(Boolean.TRUE);
    }

    /**
     * 活动中去掉商品
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/activity/products/remove")
    @ResponseBody
    public ResponseObject<Boolean> removeProducts(@ModelAttribute ActivityProductRemoveForm form) {
        activityService.removeProducts(form.getId(), form.getProductIds(), false);
        return new ResponseObject<Boolean>(Boolean.TRUE);
    }
    
    /**
     * 参加平台促销活动
     * @param id
     * @return
     */
    /*@RequestMapping("/activity/request")
    @ResponseBody
    public ResponseObject<ActivityTicket> request(@RequestParam String id) {
        ActivityTicket ticket = activityService.requestTicket(id);
        return new ResponseObject<ActivityTicket>(ticket);
    }*/
    
    /**
     * 提交参加促销活动
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/activity/join")
    @ResponseBody
    public ResponseObject<Boolean> join(ActivityJoinForm form) {
    	 User user = getCurrentUser();
    	 Activity activity = activityService.load(form.getId());
    	 if(!activity.getType().equals(ActivityType.PUBLIC_FOREVER) && activity.getStatus()==ActivityStatus.CLOSED){
    		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动已经结束");
    	 }
    	 if(activity.getType()==ActivityType.PRIVATE){
    		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "个人活动不支持报名操作");
    	 }
    	 Date now = new Date(System.currentTimeMillis());
    	 if(activity.getApplyStartTime()!=null && now.before(activity.getApplyStartTime())){
    		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动报名未开始");
    	 }
    	 if(activity.getApplyEndTime()!=null && now.after(activity.getApplyEndTime())){
    		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动报名已结束");
    	 }
    	 if (form.getPreferentialType() == null ||
			 (PreferentialType.SHOP_DISCOUNT != form.getPreferentialType() &&
			 PreferentialType.ACTIVITY_PRODUCT_DISCOUNT != form.getPreferentialType() &&
			 PreferentialType.PRODUCT_REDUCTION_PRICE != form.getPreferentialType() ) ) {
    		 throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "活动报名参数错误");
    	 }
		 if (PreferentialType.ACTIVITY_PRODUCT_DISCOUNT == form.getPreferentialType()) {  // 如果是折扣类型
			 if (form.getDiscount() == null  || form.getDiscount() <= .0 || form.getDiscount() >= 1.0)
			 		throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "请设置正确的折扣值(0~10之间).");
		 }
    	 
    	 ActivityTicket ticket = activityService.getDefaultJoinActivityTicket(form.getId());
    	 
    	 ActivityTicket bean = new ActivityTicket();
    	 bean.setActivityId(form.getId());
    	 bean.setAuditStatus(ActivityTicketAuditStatus.SUBMITTED);
    	 bean.setCreatedAt(new Date());
    	 bean.setShopId(user.getShopId());
    	 bean.setStatus(ActivityStatus.NOT_STARTED);
    	 bean.setReason(form.getReason());
    	 bean.setPreferentialType(form.getPreferentialType());
   		 bean.setDiscount(form.getDiscount());
    	 if(ticket == null){
   	        activityService.insertTicket(bean);
      	}else{
      		bean.setId(ticket.getId());
      		activityService.updateTicket(bean);
      	}
        return new ResponseObject<Boolean>(Boolean.TRUE);
    }
    
    
    /**
     * 推出活动
     * @param id
     * @param req
     * @return
     */
    @RequestMapping("/activity/unjoin")
    @ResponseBody
    public ResponseObject<Boolean> unjoin(@RequestParam String id) {
        activityService.unjoin(id);
        return new ResponseObject<Boolean>(Boolean.TRUE);
    }
    
    private Activity transForm2Activity(ActivitySaveForm form){
    	Activity activity = new Activity();
    	BeanUtils.copyProperties(form, activity);
    	if (form.getStartTime() != null){
    		activity.setStartTime(new Date(form.getStartTime()));
    	}
    	if (form.getEndTime() != null) {
            activity.setEndTime(new Date(form.getEndTime()));
        }
    	if(form.getApplyStartTime()!=null){
        	activity.setApplyStartTime(new Date(form.getApplyStartTime()));
        }
        if(form.getApplyEndTime()!=null){
        	activity.setEndTime(new Date(form.getApplyEndTime()));
        }
        if(form.getChannel() == null){
        	activity.setChannel(ActivityChannel.PRIVATE);
        }
    	User user = getCurrentUser();
    	if(form.getType() ==null){
    		activity.setType(ActivityType.PRIVATE);
    	}
    	activity.setCreatorId(user.getId());
    	activity.setStatus(ActivityStatus.NOT_STARTED);
    	if (activity.getType()==ActivityType.PRIVATE){
	        Shop shop = shopService.findByUser(user.getId());
	        activity.setImg(shop.getImg());
	        activity.setBanner(shop.getBanner());
    	}else if(activity.getType() == ActivityType.PUBLIC){
    		activity.setImg(form.getImg());
	        activity.setBanner(form.getBanner());
    	}else{
    		activity = null;
    	}
    	return activity;
    }
    
}

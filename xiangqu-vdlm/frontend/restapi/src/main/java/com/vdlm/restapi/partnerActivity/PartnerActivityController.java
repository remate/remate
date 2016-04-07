package com.vdlm.restapi.partnerActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.mapper.CampaignProductMapper;
import com.vdlm.dal.mapper.ProductMapper;
import com.vdlm.dal.model.Activity;
import com.vdlm.dal.model.ActivityTicket;
import com.vdlm.dal.model.CampaignProduct;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.status.ActivityTicketAuditStatus;
import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.dal.type.ActivityType;
import com.vdlm.dal.vo.CampaignProductEX;
import com.vdlm.dal.vo.XQHomeActProductVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.ProductService;
//import com.vdlm.service.rocketmq.SyncRocketMq;
import com.vdlm.service.shop.ShopService;

@Controller
public class PartnerActivityController extends BaseController {

	@Autowired
    private ActivityService activityService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
    private CampaignProductMapper campaignProductMapper;
	
	@Autowired
    private ResourceFacade resourceFacade;
	
	@Autowired
	private ProductMapper productMapper;
	
//    @Autowired
//    private SyncRocketMq syncRocketMq;
	
	@Value("${xiangqu.xianshigou.default.img}")
	private String xq_default_img;
	
	@Value("${xiangqu.xianshigou.default.banner}")
	private String xq_default_banner;
	
	/**
     * 根据活动id获取活动信息
     */
    @ResponseBody
    @RequestMapping("/partner/act/get/{id}")
    public ResponseObject<XQActivityVO> listActivitys(@PathVariable String id){
    	Activity bean = activityService.selectByPrimaryKey(id);
    	XQActivityVO vo = new XQActivityVO(bean);
    	return new ResponseObject<XQActivityVO>(vo);
    }
	
	/**
     * 根据条件查询活动列表接口
     */
    @ResponseBody
    @RequestMapping("/partner/act/list")
    public ResponseObject<Map<String, Object>> listActivitys(PartnerActivityQueryForm form, Pageable pager){
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	if(StringUtils.isNoneBlank(form.getTitle())){
    		paramsMap.put("name", "%" + form.getTitle() + "%");
    	}
    	paramsMap.put("channel", form.getActChannel());
    	if (form.getActStartTime() != null) paramsMap.put("startTime", new Date(form.getActStartTime()));
    	if (form.getActEndTime() != null) paramsMap.put("endTime", new Date(form.getActEndTime()));
    	if (form.getApplyStartTime() != null) paramsMap.put("applyStartTime", new Date(form.getApplyStartTime()));
    	if (form.getApplyEndTime() != null) paramsMap.put("applyEndTime", new Date(form.getApplyEndTime()));
    	if(form.getActState()!=null) paramsMap.put("actState", form.getActState());
    	Long totalCount = activityService.countActivitysByQuery(paramsMap);
    	List<XQActivityVO> dataList = new ArrayList<XQActivityVO>();
    	if(totalCount>0){
    		List<Activity> list = activityService.listActivitysByQuery(paramsMap, pager);
    		for (int i = 0; i < list.size(); i++) {
    			Activity bean = list.get(i);
    			XQActivityVO vo = new XQActivityVO(bean);
    			dataList.add(vo);
			}
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("totalCount", totalCount);
    	result.put("list", dataList);
    	return new ResponseObject<Map<String, Object>>(result);
    }
    
    /**
     * 查询活动商品审核列表接口
     */
    @ResponseBody
    @RequestMapping("/partner/act/audit/list")
    public ResponseObject<Map<String, Object>> listActivityProducts(PartnerActivityProductQueryForm form, Pageable pager){
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	if(StringUtils.isNoneBlank(form.getActId())){
    		paramsMap.put("activityId", form.getActId());
    	}
    	if(form.getAuditState() != null){
    		paramsMap.put("auditStatus", form.getAuditState());
    	}
    	if(form.getProductName() != null){
    		paramsMap.put("shortName", "%" + form.getProductName() +"%");
    	}
    	if(form.getProductBrand() != null){
    		paramsMap.put("productBrand", "%" + form.getProductBrand() + "%");
    	}
    	if(form.getShortName() != null){
    		paramsMap.put("shortName", "%" + form.getShortName() + "%");
    	}
    	if(form.getStartTime() != null){
    		paramsMap.put("startTime", new Date(form.getStartTime()));
    	}
    	if(form.getEndTime() != null){
    		paramsMap.put("endTime", new Date(form.getEndTime()));
    	}
   		paramsMap.put("sortType", form.getSortType());
   		long getList0 = System.currentTimeMillis();
   		long getList1 = 0;
   		long getList2 = 0;
    	Long totalCount = activityService.countCampaignProductByQuery(paramsMap);
    	List<XQActivityProductVO> dataList = new ArrayList<XQActivityProductVO>();
    	if(totalCount>0){
    		List<CampaignProductEX> list = activityService.listCampaignProductByQuery(paramsMap, pager);
    		getList1 = System.currentTimeMillis();
    		for (int i = 0; i < list.size(); i++) {
    			CampaignProductEX bean = list.get(i);
    			XQActivityProductVO vo = new XQActivityProductVO(bean);
    			//ProductVO productVO = productService.load(bean.getProductId());
    			Product p = productMapper.selectByPrimaryKey(bean.getProductId());
    			Shop shop = shopService.load(p.getShopId());
    			vo.setShopName(shop.getName());
    			vo.setOldPrice(p.getMarketPrice());
    			if(bean.getDiscount()!=null){
    				vo.setActPrice(p.getMarketPrice().multiply(new BigDecimal(bean.getDiscount())));
    			}else{
    				vo.setActPrice(p.getMarketPrice().subtract(new BigDecimal(bean.getReduction())));
    			}
    			dataList.add(vo);
			}
    		getList2 = System.currentTimeMillis();
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("totalCount", totalCount);
    	result.put("list", dataList);
    	long endTime = System.currentTimeMillis();
    	log.debug("totle eclipse: " + (endTime - startTime) + " and getList:" + getList0 +" getList1:" + getList1 + " getList2:" + getList2);
    	return new ResponseObject<Map<String, Object>>(result);
    }
    
    /**
     * 审核活动商品接口,新建一条ticket并且将活动商品关联到该ticket
     * @return
     */
    @ResponseBody
    @RequestMapping("/partner/act/audit")
    public ResponseObject<Boolean> auditActivityProduct(PartnerActivityProductAudit form){
    	ActivityTicket ticket = activityService.loadSubmittedTicket4Audit(form.getActId(), form.getProductId());
    	if(ticket==null){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动商品申请不存在");
    	}
    	if (form.getAuditState() != null && form.getAuditState().equals(ActivityTicketAuditStatus.APPROVED) && 
    			(form.getStartTime() == null || form.getEndTime() == null)) {
    		throw new BizException(GlobalErrorCode.UNKNOWN, "审核的活动开始时间不能为空");
    	}
    	ActivityTicket newTicket = new ActivityTicket();
		BeanUtils.copyProperties(ticket, newTicket);
		newTicket.setAuditStatus(form.getAuditState());
		newTicket.setStartTime( form.getStartTime() != null ? new Date(form.getStartTime()) : null );
		newTicket.setEndTime( form.getEndTime() != null ? new Date(form.getEndTime()) : null );
		newTicket.setAuditReason(form.getAuditReason());
		newTicket.setAuditor(form.getAuditor());
		
		//查询该商品是否已在同一时点的其他活动中有出现过
        if (activityService.existProductInRange(newTicket.getStartTime(), ticket.getEndTime(), form.getActId(), form.getProductId())) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "商品重复参与活动,ID：" + form.getProductId());
        }
		
		// TODO: 如果审核通过的商品再次被审核不通过需要做活动商品还原操作
		activityService.insertTicket(newTicket);
    	activityService.auditTicketProduct(newTicket.getId(), form.getActId(), ticket.getId(), form.getProductId(),
    															form.getProductBrand(), form.getShortName(), form.getSort(), form.getImagePc(), form.getImageApp());
    	if (form.getAuditState().equals(ActivityTicketAuditStatus.APPROVED))
    		productService.lockProduct(form.getProductId(), true);
    	else
    		productService.lockProduct(form.getProductId(), false);
    	return new ResponseObject<Boolean>(true);
    }
    
   	private Activity transFormActInfo(XQActivityVO pai) {
   		Activity activity = new Activity();
   		activity.setId(pai.getActId());
   		if (pai.getActStartTime() != null) activity.setStartTime(new Date(pai.getActStartTime()));
   		if (pai.getActEndTime() != null) activity.setEndTime(new Date(pai.getActEndTime()));
   		if (pai.getApplyStartTime() != null)  activity.setApplyStartTime(new Date(pai.getApplyStartTime()));
   		if (pai.getApplyEndTime() != null)  activity.setApplyEndTime(new Date(pai.getApplyEndTime()));
   		if (pai.getActChannel().equals(ActivityChannel.XIANGQU)) 	activity.setChannel(ActivityChannel.XIANGQU);
   		activity.setName(pai.getActTitle());
   		activity.setDetails(pai.getActDesc());
   		activity.setApplyDesc(pai.getApplyDesc());
   		activity.setImg(xq_default_img);
   		activity.setBanner(xq_default_banner);
   		activity.setCreatedAt(new Date());
   		activity.setType(pai.getActType());
   		if (pai.getActType().equals(ActivityType.PUBLIC_FOREVER))
   			activity.setStatus( ActivityStatus.IN_PROGRESS);
   		else
   			activity.setStatus(ActivityStatus.NOT_STARTED);
   		activity.setUrl(pai.getActUrl());
   		activity.setTagImage(pai.getActTagImage());
   		activity.setActTagType(pai.getActTagType());
		activity.setCreatorId(pai.getCreateOper());
   		
   		return activity;
   	}
   	
   	private XQActivityVO transFormActInfoRevs(Activity activity) {
   			XQActivityVO pai = new XQActivityVO(activity);
   			pai.setActChannel(ActivityChannel.XIANGQU);
   			pai.setActTitle(activity.getName());
   			pai.setActDesc(activity.getDetails());
			if (pai.getActStartTime() != null) activity.setStartTime(new Date(pai.getActStartTime()));
			if (pai.getActEndTime() != null) activity.setEndTime(new Date(pai.getActEndTime()));
			if (pai.getApplyStartTime() != null)  activity.setApplyStartTime(new Date(pai.getApplyStartTime()));
			if (pai.getApplyEndTime() != null)  activity.setApplyEndTime(new Date(pai.getApplyEndTime()));
   			pai.setCreateOper(activity.getCreatorId());
   			pai.setActUrl(activity.getUrl());
   			pai.setActState(activity.getStatus());
   			pai.setActTagImage(activity.getTagImage()); 
//   			pai.setActTagImageUrl(activity.getBanner()); 
   		
   		return pai;
   	}
   	
   	private void actCreateChk(XQActivityVO pai) {
   		if (pai == null)
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "创建活动参数不合法");
   		if (pai.getActType() == null)
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "创建活动类型不能为空");
   		if (ActivityType.PUBLIC.equals(pai.getActType()))
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "暂时不支持有期限活动");
   		
   		if (pai.getActType().equals(ActivityType.PUBLIC_FOREVER)) {
   			pai.setActState(ActivityStatus.IN_PROGRESS);
   		} else {
   			pai.setActState(ActivityStatus.NOT_STARTED);
	   		if (pai.getActStartTime() == null)
	    		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "活动开始时间不能为空");
   		}
    	if (pai.getApplyEndTime() != null && pai.getApplyStartTime() != null && pai.getApplyEndTime() < pai.getApplyStartTime())
    		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "活动申请 结束时间不能小于开始时间");
   	}
   	
    /**
     * 第三方活动创建接口
     * @param id
     * @return
     */
    @RequestMapping("/partner/act/create")
    @ResponseBody
    public ResponseObject<Boolean> actCreateEx(XQActivityVO pai) {
    	actCreateChk(pai);
    	Activity activity  = transFormActInfo(pai);
        Boolean ret = activityService.saveActivity(activity) != null ? true : false;
        
        return new ResponseObject<Boolean>(ret);
    }
    
    /**
     * 第三方更新活动接口
     * @param id
     * @return
     */
    @RequestMapping("/partner/act/update")
    @ResponseBody
    public ResponseObject<Boolean> actUpdateEx(XQActivityVO pai) {
    	if (pai == null) 
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "更新活动参数为空");
    	
    	Activity activity  = transFormActInfo(pai);
        Boolean ret = activityService.update(activity) != null ? true : false;
        
        return new ResponseObject<Boolean>(ret);
    }
    
    /**
     * 第三方删除活动接口
     * @param id
     * @return
     */
    @RequestMapping("/partner/act/delete")
    @ResponseBody
    public ResponseObject<Boolean> actDeleteEx(@RequestParam String actId, String updateOper) {
        return new ResponseObject<Boolean>(activityService.delete(actId) == 1 ? true : false);
    }
    
    /**
     *  根据商品ID查询活动列表接口
     * @param productId
     * @param actChannel
     * @return
     */
    @RequestMapping("/partner/act/list/{productId}")
    @ResponseBody
    public ResponseObject<List<XQActivityVO>> actGetByProductEx(@PathVariable String productId, String actChannel) {
    	List<XQActivityVO> ret = new ArrayList<XQActivityVO>();
    	
    	List<CampaignProduct> cps = campaignProductMapper.selectByProdcut(productId);
    	if (cps != null && cps.size() != 0) {
    		for (CampaignProduct cp : cps) {
    			ActivityTicket at = activityService.loadSubmittedTicket4Audit(cp.getActivityId(), productId);
    			//if (at != null && at.getStatus() != null && at.getStatus().equals(ActivityStatus.IN_PROGRESS)) {
    			if (at != null) {
    				Activity activity = activityService.load(at.getActivityId());
    				if (activity != null && activity.getChannel().equals(ActivityChannel.XIANGQU))
    					ret.add(transFormActInfoRevs(activity));
    			}
    		}
    	}
    	
    	return new ResponseObject<List<XQActivityVO>>(ret);
    }
    
    /*
     *  首页展示限时购商品接口
     */
    @RequestMapping("/partner/act/product/list")
    @ResponseBody
    public ResponseObject<List<XQHomeActProductVO>> actGetActsByProductEx(@RequestParam String actId, Integer sort, Pageable page) {
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	List<XQHomeActProductVO> result = new ArrayList<XQHomeActProductVO>();
    	if(StringUtils.isNoneBlank(actId))
    		paramsMap.put("activityId", actId);
    	if (sort != null)
    		paramsMap.put("sort", sort);
    	
//    	Long totalCount = activityService.countCampaignProduct4Home(paramsMap);
		result = activityService.listCampaignProduct4Home(paramsMap, page);
		for (XQHomeActProductVO vo : result) {
			vo.setSoldOut(vo.getSoldOut() > 0 ? 1 : 2);
			vo.setProductId(IdTypeHandler.encode(Long.parseLong(vo.getProductId())));
		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("totalCount", totalCount);
//		map.put("list", result);
    	return new ResponseObject<List<XQHomeActProductVO>>(result);
    }
    
}

package com.vdlm.openapi.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.mapper.ActivityTicketMapper;
import com.vdlm.dal.mapper.CampaignProductMapper;
import com.vdlm.dal.mapper.ProductMapper;
import com.vdlm.dal.model.Activity;
import com.vdlm.dal.model.ActivityTicket;
import com.vdlm.dal.model.CampaignProduct;
import com.vdlm.dal.model.Coupon;
import com.vdlm.dal.model.CouponActivity;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.status.ActivityTicketAuditStatus;
import com.vdlm.dal.type.ActivityChannel;
import com.vdlm.dal.type.ActivityStatus;
import com.vdlm.dal.type.ActivityType;
import com.vdlm.dal.vo.CampaignProductEX;
import com.vdlm.dal.vo.XQHomeActProductVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.partnerActivity.PartnerActivityProductAudit;
import com.vdlm.restapi.partnerActivity.PartnerActivityProductQueryForm;
import com.vdlm.restapi.partnerActivity.PartnerActivityProductStop;
import com.vdlm.restapi.partnerActivity.PartnerActivityQueryForm;
import com.vdlm.restapi.partnerActivity.XQActivityProductVO;
import com.vdlm.restapi.partnerActivity.XQActivityVO;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.pricing.CouponService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;

@Controller
@RequestMapping(value = "/openapi/s")
public class PartnerActivityControllerApi extends BaseController {

	@Autowired
    private ActivityService activityService;
	@Autowired
	private UserService userService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductMapper	productMapper; 
	@Autowired
	private CampaignProductMapper campaignProductMapper;
	@Autowired
	private ActivityTicketMapper activityTicketMapper;
	
	@Autowired
	private CouponService couponService;

	@Value("${xiangqu.xianshigou.default.img}")
	private String xq_default_img;
	
	@Value("${xiangqu.xianshigou.default.banner}")
	private String xq_default_banner;
	
	/**<pre>
     * 编辑(新增和修改)活动接口
     * 测试数据：
     * 1.1 public数据：状态是进行中和未开始：public  和 publicforver
     *     添加：openapi/s/partner/act/save?createOper=1rzwgd0p&actType=PUBLIC_FOREVER&actTitle=ceshi2
     *     修改：openapi/s/partner/act/save?actId=3177&createOper=1rzwgd0p&actType=PUBLIC_FOREVER&actTitle=ceshi27
     * 1.2 private 需要开始时间
     *     修改 openapi/s/partner/act/save?actId=3180&createOper=1rzwgd0p&actType=PRIVATE&actTitle=ceshi34&actStartTime=2784441632
     * </pre>
     */
    @RequestMapping("/partner/act/save")
    @ResponseBody
    public ResponseObject<Activity> save(@ModelAttribute XQActivityVO pai,  @RequestHeader("Domain") String domain) {
    	// 数据有效性验证
		actCreateChk(pai, domain);
		// 初始化activity
    	Activity initAct  = transFormActInfo(pai);
    	
    	// 执行添加/修改
    	if(StringUtils.isBlank(pai.getActId())){
    		return new ResponseObject<Activity>(activityService.saveActivity(initAct));
    	}

		return new ResponseObject<Activity>(activityService.update(initAct,initAct.getCreatorId(),pai.getShopId()));
    }	

  /**
   * 想去获取优惠券
   * @param actCode
   * @param userId
   * @param deviceId
   * @param domain
   * @return
   */
  @RequestMapping("/partner/coupon/save")
  @ResponseBody
  public ResponseObject<Boolean> saveCoupon(String actId, String extUid,  String deviceId,@RequestHeader(value="Domain") String domain){
	  CouponActivity activity=couponService.loadByActId(actId);
	  Coupon coupon=couponService.grantCoupon(activity.getActCode(), getCurrentUser().getId(), deviceId);
	  return  new ResponseObject<Boolean>((coupon!=null));
   }
  
  @RequestMapping("/partner/coupon/load")
  @ResponseBody
  public ResponseObject<Coupon> loadCoupon(String actId,String extUid,@RequestHeader(value="Domain") String domain){
	     List<Coupon> couponList=couponService.loadByActIdAndUserId(actId, getCurrentUser().getId());
	     Coupon coupon=null;
	     if(couponList!=null&&couponList.size()>0)
	    	 coupon=couponList.get(0);
	     return  new ResponseObject<Coupon>(coupon);
  }
    

    
	/**
     * 删除活动接口
     * userId 是混淆用户
     */
    @RequestMapping("/partner/act/delete")
    @ResponseBody
    public ResponseObject<Boolean> actDeleteEx(@RequestParam String actId, @RequestParam String userId, @RequestHeader("Domain") String domain) {
    	User user = userService.load(userId);
    	if(user == null || !domain.equals(user.getPartner())){
    		log.debug("用户不存在");
			throw new BizException(GlobalErrorCode.UNAUTHORIZED, "用户不存在");
			
    	}
    	
    	return new ResponseObject<Boolean>(activityService.deleteActivity(actId,userId) ==1);
    }
	
	/**
     * 根据活动id获取活动详情信息
     * openapi/s/partner/act?id=3179
     */
    @ResponseBody
    @RequestMapping("/partner/act")
    public ResponseObject<XQActivityVO> listActivitys(@RequestParam String id,  @RequestHeader("Domain") String domain){
    	Activity activity = activityService.selectByPrimaryKey(id);
    	if(activity==null){
    		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "活动不存在");
    	}
    	
        User user=userService.load(activity.getCreatorId());
    	if(user==null || !user.getPartner().equals(domain)){
    		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "您没有权限查看该活动");
    	}
    	
    	return new ResponseObject<XQActivityVO>(new XQActivityVO(activity));
    }
    
	/**
     * 根据条件查询--活动列表
     */
    @ResponseBody
    @RequestMapping("/partner/act/list")
    public ResponseObject<Map<String, Object>> listActivitys(@ModelAttribute PartnerActivityQueryForm form, Pageable pager, @RequestHeader("Domain") String domain){
    	if (form.getActChannel()==null) {
    		throw new BizException(GlobalErrorCode.UNKNOWN, "actChannel 不能为空");
    	}
    	
    	if(ActivityChannel.XIANGQU.equals(form.getActChannel())){
    		boolean isXiangQu = "xiangqu".equals(domain);
    		if(!isXiangQu){
    			throw new BizException(GlobalErrorCode.UNKNOWN, "您没有权限查看活动列表");
    		}
    	}
    	
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	if(StringUtils.isNoneBlank(form.getTitle())){
    		paramsMap.put("name", "%" + form.getTitle() + "%");
    	}
    	
    	
    	paramsMap.put("channel", form.getActChannel());
    	
    	if (form.getActStartTime() != null){
    		paramsMap.put("startTime", new Date(form.getActStartTime()));
    	}
    	
    	if (form.getActEndTime() != null){
    		paramsMap.put("endTime", new Date(form.getActEndTime()));
    	}
    	
    	if (form.getApplyStartTime() != null){
    		paramsMap.put("applyStartTime", new Date(form.getApplyStartTime()));
    	}
    	
    	if (form.getApplyEndTime() != null){
    		paramsMap.put("applyEndTime", new Date(form.getApplyEndTime()));
    	}
    	
    	if(form.getActState()!=null){
    		paramsMap.put("actState", form.getActState());
    	}
    	
    	// 获取符合要求的活动条数
    	Long totalCount = activityService.countActivitysByQuery(paramsMap);
    	
    	List<XQActivityVO> dataList = new ArrayList<XQActivityVO>();
    	if(totalCount>0){
    		List<Activity> list = activityService.listActivitysByQuery(paramsMap, pager);
    		for(Activity a:list){
    			dataList.add(new XQActivityVO(a));
    		}
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("totalCount", totalCount);
    	result.put("list", dataList);
    	result.put("page", pager);
    	return new ResponseObject<Map<String, Object>>(result);
    }
    
    /**
     * 查询审核--商品列表
     * auditState
     */
    @ResponseBody
    @RequestMapping("/partner/act/audit/list")
    public ResponseObject<Map<String, Object>> listActivityProducts(@Valid PartnerActivityProductQueryForm form, Pageable pager){
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	if(StringUtils.isNoneBlank(form.getActId())){
    		paramsMap.put("activityId", form.getActId());
    	}
    	if(form.getAuditState() != null){
    		paramsMap.put("auditStatus", form.getAuditState());
    	}
    	if(form.getProductName() != null){
    		paramsMap.put("shortName", '%'+form.getProductName()+'%');
    	}
    	if(form.getProductBrand() != null){
    		paramsMap.put("productBrand", '%'+form.getProductBrand()+'%');
    	}
    	if(form.getStartTime() != null){
    		paramsMap.put("startTime", new Date(form.getStartTime()));
    	}
    	if(form.getEndTime() != null){
    		paramsMap.put("endTime", new Date(form.getEndTime()));
    	}
   		paramsMap.put("sortType", form.getSortType());
    	    	Long totalCount = activityService.countCampaignProductByQuery(paramsMap);
    	
    	List<XQActivityProductVO> dataList = new ArrayList<XQActivityProductVO>();
    	if(totalCount>0){
    		List<CampaignProductEX> list = activityService.listCampaignProductByQuery(paramsMap, pager);
    		for (int i = 0; i < list.size(); i++) {
    			CampaignProductEX bean = list.get(i);
    			XQActivityProductVO vo = new XQActivityProductVO(bean);
    			Product p = productMapper.selectByPrimaryKey(bean.getProductId());
    			if(p==null){
    				continue;
    				//throw new BizException(GlobalErrorCode.NOT_FOUND, "活动商品"+bean.getProductId()+"不存在");
    			}
    			
    			Shop shop = shopService.load(p.getShopId());
    			vo.setShopName(shop.getName());
    			vo.setOldPrice(p.getMarketPrice());
    			if(bean.getDiscount()!=null){
    				vo.setActPrice(p.getMarketPrice().multiply(new BigDecimal(bean.getDiscount())));
    			}else if (bean.getReduction()!=null){
    				vo.setActPrice(p.getMarketPrice().subtract(new BigDecimal(bean.getReduction())));
    			} else {
    				throw new BizException(GlobalErrorCode.UNKNOWN, "活动信息已不完整");
    			}
    			dataList.add(vo);
			}
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("totalCount", totalCount);
    	result.put("list", dataList);
    	result.put("pager", pager);
    	return new ResponseObject<Map<String, Object>>(result);
    }
    
    /**
     * 再次审核
     */
    @ResponseBody
    @RequestMapping("/partner/act/reAudit")
    public ResponseObject<Boolean> reAuditActivityProduct(@ModelAttribute PartnerActivityProductAudit form,   String domain) {
    	checkForm(form, domain);
    	
    	if (StringUtils.isEmpty(form.getTicketId()))
    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动不存在, 审核失败");
    	
    	ActivityTicket ticket = activityTicketMapper.selectByPrimaryKey(form.getTicketId());
    	if (ticket == null) {
    		log.warn("活动 "+form.getActId()+" 商品 "+form.getProductId()+" 申请不存在，审核失败");
    		return new ResponseObject<Boolean>(false);
    	}
    	
		auditAP(form,ticket,true);
    	return new ResponseObject<Boolean>(true);
    }
    
    /**
     * 再次审核
     */
    @ResponseBody
    @RequestMapping("/partner/act/stop")
    public ResponseObject<Boolean> stopActivityProduct(@ModelAttribute PartnerActivityProductStop form, String domain) {
    	checkForm(form, domain);
    	
    	if (StringUtils.isEmpty(form.getTicketId()))
    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动不存在, 审核失败");
    	
    	ActivityTicket ticket = activityTicketMapper.selectByPrimaryKey(form.getTicketId());
    	if (ticket == null) {
    		log.warn("活动没有找到, 请确认该商品是否正在活动中...");
    		return new ResponseObject<Boolean>(false);
    	}
    	ActivityTicket update = new ActivityTicket();
    	update.setId(ticket.getId());
    	update.setEndTime(new Date());
    	activityTicketMapper.update(update);
    	
    	return new ResponseObject<Boolean>(true);
    }
    
    /**
     * 审核活动商品<br>
     * /openapi/s/partner/act/audit?auditor=123&actId=2585&productId=6e5rm0&auditState=APPROVED&shortName=xbwceshi
     * 问题：审核的时候又复制了一条ticket,没有在原基础上添加审核状态，造成数据冗余，商品的ticketId也级联修改，造成不必要的操作，解决：不添加新的ticket记录<br>
     * 事物回滚应该全部调用放在service层）
	 * 新建一条ticket并且将活动商品关联到该ticket@RequestHeader("Domain")
     */
    @ResponseBody
    @RequestMapping("/partner/act/audit")
    public ResponseObject<Boolean> auditActivityProduct(@ModelAttribute PartnerActivityProductAudit form,   String domain) {
    	checkForm(form, domain);
    	
    	// 获取店铺，商品所属的活动信息
    	ActivityTicket ticket = activityService.loadSubmittedTicket4Audit(form.getActId(), form.getProductId());
    	if (ticket == null) {
    		log.warn("活动 "+form.getActId()+" 商品 "+form.getProductId()+" 申请不存在，审核失败");
    		return new ResponseObject<Boolean>(false);
    	}
    	
    	auditAP(form,ticket,false);
    	return new ResponseObject<Boolean>(true);
    }
   	
	private void auditAP(PartnerActivityProductAudit form, ActivityTicket ticket,boolean isReAudit) {
		// 已经结束了的活动不能再次审核
    	if(ActivityStatus.CLOSED.equals(ticket.getStatus())){
    		log.warn("您要审核的活动"+form.getActId()+"商品 "+form.getProductId()+" 已经结束，不能审核");
    		throw new BizException(GlobalErrorCode.UNKNOWN, "已经结束了的活动不能审核");
    	}
		
		ActivityTicket newTicket = new ActivityTicket();
		BeanUtils.copyProperties(ticket, newTicket);
		newTicket.setAuditStatus(form.getAuditState());
		newTicket.setStartTime(form.getStartTime() != null ?  new Date(form.getStartTime()) : null);
		newTicket.setEndTime(form.getEndTime() != null ? new Date(form.getEndTime()) : null);
		newTicket.setAuditReason(form.getAuditReason());
		newTicket.setAuditor(form.getAuditor());
		
		if (isReAudit) { // 再次审核
	    	// 获取报名信息
			if (StringUtils.isEmpty(form.getTicketId()))
	    		throw new BizException(GlobalErrorCode.UNKNOWN, "再次审核,活动商品id不能为空");
	    	ActivityTicket at = activityTicketMapper.selectByPrimaryKey(form.getTicketId());
	    	if (at == null)
	    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动不存在, 再次审核失败");
	    	if ( !at.getStatus().equals(ActivityStatus.NOT_STARTED) )
	    		throw new BizException(GlobalErrorCode.UNKNOWN, "进行中或已关闭的活动不能再次审核");
			if ( !ActivityTicketAuditStatus.APPROVED.equals(at.getAuditStatus()) &&
				 !ActivityTicketAuditStatus.REJECTED.equals(at.getAuditStatus()) ) {
	    		throw new BizException(GlobalErrorCode.UNKNOWN, "只能重审未通过或已通过状态的活动商品");
			}
			newTicket.setAuditReason(StringUtils.isEmpty(form.getAuditReason()) ? at.getAuditReason() : form.getAuditReason());
			newTicket.setReason(at.getReason());
			activityTicketMapper.deleteByIds(at.getId()); // 重审直接删除后新增,保留才记录方便追踪
			newTicket.setId(null);
		}
		
		//查询该商品是否已在同一时点的其他活动中有出现过
		if (ActivityTicketAuditStatus.APPROVED.equals(form.getAuditState())) {
	        if (activityService.existProductInRange(newTicket.getStartTime(), newTicket.getEndTime(), null, form.getProductId())) {
				throw new BizException(GlobalErrorCode.INTERNAL_ERROR, "商品重复参与活动,ID：" + form.getProductId());
	        }
		}
		
        activityService.insertTicket(newTicket);
		
    	activityService.auditTicketProduct(newTicket.getId(), form.getActId(), ticket.getId(), form.getProductId(),
    															form.getProductBrand(), form.getShortName(),
    															form.getSort(), form.getImagePc(), form.getImageApp());
    	// 拒绝活动申请--商品可以修改
    	if (!isReAudit && form.getAuditState().equals(ActivityTicketAuditStatus.REJECTED)) {
    		productService.lockProduct(form.getProductId(), false);
	    	campaignProductMapper.deleteActivityProducts(form.getActId(), newTicket.getId(), form.getProductId());
	    	List<CampaignProduct> list = campaignProductMapper.selectByTicket(ticket.getId());
	    	// 如果该活动再没有商品待审核直接删除该活动申请以便重新报名
	    	if (list == null || list.size() ==0 || 
	    			(list.size()==1 && list.get(0).getProductId().equals(form.getProductId())) ) {
	    		activityTicketMapper.deleteByIds(ticket.getId());
	    	}
    	}
	}
	
	private void checkForm(PartnerActivityProductStop form, String domain) {
    	if(StringUtils.isBlank(form.getActId())){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动Id不存在");
    	}
    	
    	if(StringUtils.isBlank(form.getProductId())){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "商品Id不存在");
    	}
    	
    	if(form.getAuditor()==null){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "审核人不存在");
    	}
    	
    	User user = userService.loadExtUser(StringUtils.defaultString(domain, "xiangqu"), form.getAuditor());
    	if(user == null){
    		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "您没有权限审核该活动");
    	}
		
	}
	
	
	private void checkForm(PartnerActivityProductAudit form, String domain) {
    	if(StringUtils.isBlank(form.getActId())){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "活动Id不存在");
    	}
    	
    	if(StringUtils.isBlank(form.getProductId())){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "商品Id不存在");
    	}
    	
    	if(form.getAuditState()==null){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "审核状态不存在");
    	}
    	
    	if(form.getAuditor()==null){
    		throw new BizException(GlobalErrorCode.UNKNOWN, "审核人不存在");
    	}
    	
    	if (ActivityTicketAuditStatus.APPROVED.equals(form.getAuditState())) {
    		if (form.getStartTime() == null || form.getEndTime() == null)
    			throw new BizException(GlobalErrorCode.UNKNOWN, "审核通过必须分配活动开始结束时间");
    		if (form.getProductId() == null)
    			throw new BizException(GlobalErrorCode.UNKNOWN, "审核信息不完整");
    	}
    	
    	User user = userService.loadExtUser(StringUtils.defaultString(domain, "xiangqu"), form.getAuditor());
    	if(user == null){
    		throw new BizException(GlobalErrorCode.UNAUTHORIZED, "您没有权限审核该活动");
    	}
		
	}

    /*
     *  某个活动的商品列表
     *  /openapi/s/partner/act/product/list?actId=3165
     */
    @RequestMapping("/partner/act/product/list")
    @ResponseBody
    public ResponseObject<List<XQHomeActProductVO>> actGetActsByProductEx(@RequestParam String actId,  Pageable page) {//Integer sort,
    	Map<String, Object> paramsMap = new HashMap<String, Object>();
    	if(StringUtils.isNoneBlank(actId))
    		paramsMap.put("activityId", actId);
    	
    	//if (sort != null)
    		//paramsMap.put("sort", sort);
    	
    	List<XQHomeActProductVO> result =  activityService.listCampaignProduct4Home(paramsMap, page);
    	for (XQHomeActProductVO vo : result) {
			vo.setSoldOut(vo.getSoldOut() > 0 ? 1 : 2);
			vo.setProductId(IdTypeHandler.encode(Long.parseLong(vo.getProductId())));
		}
    	return new ResponseObject<List<XQHomeActProductVO>>(result);
    }
    
    /**
   	 * 检查活动是否合法
   	 * 创建的时候：创建人，活动类型，标题
   	 * @param pai
     * @param domain 
   	 */
	private void actCreateChk(XQActivityVO pai, String domain) {
   		if (pai == null) {
    		throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "活动不能为空");
   		}
   		domain = StringUtils.defaultIfBlank(domain, "xiangqu");
   		
   		// 创建人
   		if(StringUtils.isBlank(pai.getCreateOper())){
   			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "创建人不能为空");
   		} else {
   			User user=null;
   			if(!StringUtils.isBlank(pai.getExtUid())) {
   				user=userService.loadExtUser(domain, pai.getExtUid());
   			}else if (!StringUtils.isBlank(pai.getCreateOper())) {
   				user=userService.loadExtUser(domain, pai.getCreateOper());
   			} else {
   				user = userService.loadByAdmin(pai.getCreateOper());
   			}
   			
   			if(user == null || !domain.equals(user.getPartner())){
   				log.debug("用户不存在");
   				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "用户不存在");
   			}
   			
   			if(StringUtils.isBlank(pai.getShopId())){
   				pai.setShopId(user.getShopId());
   			}
   		}
   		
	   	if(StringUtils.isBlank(pai.getActId())){	
	   		// 活动名称
	   		if(StringUtils.isBlank(pai.getActTitle())){
	   			throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "活动名称不能为空");
	   		}
	   		
	   		// 活动永久的，不需要时间信息
	   		if (ActivityType.PUBLIC_FOREVER.equals(pai.getActType())) {
	   			pai.setActState(ActivityStatus.IN_PROGRESS);
	   			return;
	   		}
	   		
	   		pai.setActState(ActivityStatus.NOT_STARTED);
	   		if (pai.getActStartTime() == null || pai.getActEndTime() == null){
	    		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "活动开始或结束时间不能为空");
	   		}
	   		
	   		// 活动报名时间
	    	if (pai.getApplyEndTime() != null && pai.getApplyStartTime() != null && pai.getApplyEndTime() > pai.getActStartTime()){
	    		throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "活动报名 截止时间不能大于开始时间");
	    	}
   		}
   	}
	
	/**
	 * 初始化活动
	 * @param pai
	 * @return
	 */
	private Activity transFormActInfo(XQActivityVO pai) {
   		Activity activity = new Activity();
   		activity.setId(pai.getActId());
   		activity.setName(pai.getActTitle());
   		activity.setDetails(pai.getActDesc());
   		activity.setApplyDesc(pai.getApplyDesc());
   		activity.setCreatedAt(new Date());
   		activity.setType(pai.getActType());
   		activity.setImg(xq_default_img);
	   	activity.setBanner(xq_default_banner);
	   	activity.setUrl(pai.getActUrl());
   		activity.setTagImage(pai.getActTagImage());
   		activity.setActTagType(pai.getActTagType());
		activity.setCreatorId(pai.getCreateOper());
   		
   		// 时间初始化
   		if (pai.getActStartTime() != null) {
   			activity.setStartTime(new Date(pai.getActStartTime()));
   		}
   		
   		if (pai.getActEndTime() != null) {
   			activity.setEndTime(new Date(pai.getActEndTime()));
   		}
   		
   		if (pai.getApplyStartTime() != null) {
   			activity.setApplyStartTime(new Date(pai.getApplyStartTime()));
   		}
   		
   		if (pai.getApplyEndTime() != null) {
   			activity.setApplyEndTime(new Date(pai.getApplyEndTime()));
   		}
   		
   		// 活动所属
   		if (ActivityChannel.XIANGQU.equals(pai.getActChannel())){
   			activity.setChannel(ActivityChannel.XIANGQU);
   		}
   		
   		// 活动状态
   		if (ActivityType.PUBLIC_FOREVER.equals(pai.getActType())){
   			activity.setStatus( ActivityStatus.IN_PROGRESS);
   		} else {
   			activity.setStatus(ActivityStatus.NOT_STARTED);
   		}
   		
   		return activity;
   	}
    
}

/**
 * 关于商品的 open api
 */
package com.vdlm.openapi.product;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.dal.type.ProductSource;
import com.vdlm.dal.vo.FragmentChip;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.dal.vo.ShopPostAge;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.product.FragmentAndDescController;
import com.vdlm.restapi.product.ProdutSaveForm;
import com.vdlm.restapi.product.SkuWithMappingVO;
import com.vdlm.service.category.CategoryService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.vo.OfflineProduct;
import com.vdlm.service.product.vo.ProductImageVO;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.service.shop.ShopPostAgeService;
import com.vdlm.service.zone.ZoneService;
import com.wordnik.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/openapi")
public class ProductControllerApi extends FragmentAndDescController {

	@Autowired
    private ShopPostAgeService shopPostAgeService;
	@Autowired
	private ZoneService zoneService;
	@Autowired
	private UrlHelper urlHelper;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProdSyncMapper prodSyncMapper;
	
	@Value("${xiangqu.cart.host.url}")
    String xiangquCartHost;
	
	/**
	 * 获取商品的片段
	 * 获取主图（只有一张主图）：openapi/product/viewFragment?productId=1
	 * 获取主图（3张主图）openapi/product/viewFragment?productId=xpwb
	 * 获取片段，为空 ： openapi/product/viewFragment?productId=0
	 * 获取片段 /openapi/product/viewFragment?productId=o0h9nog
	 * 片段1 ：描述在前，图片（3个）在后
	 * 片段2 ：描述在前，图片（1）在后
	 * 片段3： 图片（2）在前，描述在后
	 * 备注：改变最后一个配置的showModel就可以改变显示的列表了
	 */
	@ResponseBody
	@RequestMapping("/product/viewFragment")
	public ResponseObject<List<FragmentChip>> getProductFragments(@RequestParam String productId) {
		List<FragmentChip> fcList=new ArrayList<FragmentChip>();
		ProductVO product = getProductFragment(productId);
		if(product==null){
			return new ResponseObject<List<FragmentChip>>(fcList);
		}
		
		List<FragmentVO> list=product.getFragments();
		if(list == null || list.isEmpty()){
			// 获取主图
			List<String> imgs = product.getMainImgs();
			if(imgs != null){
				for(String s:imgs){
					addFragment(true,s,fcList);
				}
			}
		}else{
			for(FragmentVO f2:list){
				if(f2.getShowModel()){
					// 描述在前
					addFragment(false,f2.getDescription(),fcList);
					
					List<FragmentImageVO> imgs=f2.getImgs();
					if (imgs == null) continue;
					for(FragmentImageVO f3:imgs){
						addFragment(true,f3.getImgUrl(),fcList);
					}
				}else{
					List<FragmentImageVO> imgs=f2.getImgs();
					if (imgs == null) continue;
					for(FragmentImageVO f3:imgs){
						addFragment(true,f3.getImgUrl(),fcList);
					}
					
					addFragment(false,f2.getDescription(),fcList);
				}
			}
		}
		return new ResponseObject<List<FragmentChip>>(fcList);
	}
	
	private void addFragment(boolean isImage, String content, List<FragmentChip> result) {
		if(StringUtils.isBlank(content)){
			return;
		}
		
		FragmentChip fp=new FragmentChip();
		fp.setIsImg(isImage);
		fp.setContent(content);
		result.add(fp);
	}

	/**
	 * 商品详情获取
	 */
	@ApiOperation("list product by id")
	@ResponseBody
	@RequestMapping("/product/view")
	public ResponseObject<ProductVO> view(@RequestParam String productId) {
		// 获取商品信息@ModelAttribute Product productForm
		ProductVO product =getProductFragment(productId);
		
		 // 生成商品的图片URL
		for (ProductImageVO img : product.getImgs()) {
			img.setImgUrl(img.getImg());
			break;
		}
		
		product.setCategory(categoryService.loadCategoryByProductId(productId));
		return new ResponseObject<ProductVO>(product);
	}
	
	private ProductVO getProductFragment(String productId) {
		Product productdb = productService.findProductById(productId);
		if (productdb == null) {
			log.warn("获取商品片段信息时，商品 "+productId+" 不存在");
			return null;
		}
		
		Shop shop = shopService.load(productdb.getShopId());
		if(shop == null){
			log.warn("获取商品片段信息时，商品所属店铺"+productdb.getShopId()+"不存在");
			return null;
		}
		
		ProductVO product = new ProductVO(productdb);
		if (shop.getFragmentStatus()) {
			setFragmentAndDescInfo(product);
		}
		if (CollectionUtils.isEmpty(product.getFragments()) ||
				(product.getFragments().size() == 1 && StringUtils.isEmpty(product.getFragments().get(0).getId()))) {  // 说明是平台公告
			setMainImgs(product);
			
			if (CollectionUtils.isEmpty(product.getFragments()))
				return product;
			
			List<FragmentImageVO> list = product.getFragments().get(0).getImgs();
			if (CollectionUtils.isNotEmpty(list) && list.get(0).getImg() != null) {
				String plantBulltin = resourceFacade.resolveUrl(list.get(0).getImg() + "|" + ResourceFacade.IMAGE_S1);
				if (StringUtils.isNotEmpty(plantBulltin)) {
					product.getMainImgs().add(0, plantBulltin);
					product.setFragments(null);
				}
			}
		}
	    return product;
	}

	@ApiOperation("list product's skus by productId & partner")
	@ResponseBody
    @RequestMapping("/product/skus")
    public ResponseObject<SkuWithMappingVO> listSkusByProductId(@RequestParam String productId, 
    					@RequestHeader(value="Domain", required=false, defaultValue = "xiangqu") String partner) {
		
        SkuWithMappingVO result = new SkuWithMappingVO();
		// 获取商品
		ProductVO product = productService.loadSkuInfoByProductId(productId);
		if (product == null) {
			log.debug("openapi 获取商品 sku 时商品不存在 id:" + productId);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product.not.found");
		}
		
		String unionId = "";
		User user = userService.loadByLoginname(partner);
		if (user == null) {
        	List<ProdSync> syncList = prodSyncMapper.selectByPassedShopId(product.getShopId());
    		if (syncList != null && syncList.size() >0) {
    			unionId = syncList.get(0).getUnionId();
    		} else {
    			return new ResponseObject<SkuWithMappingVO>(result);
    		}
		}  else {
			unionId = user.getId();
		}
		
		// 获取商品的维度列表
		List<Sku> skus = new ArrayList<Sku>();
        for (Sku sku : product.getSkus()) {
        	// 获取合法skuUrl
//        	if (sku.getAmount() <= 0) continue;
        	String skuUrl = xiangquCartHost + "/cart/checkout?skuId=" + sku.getId() + "&partner=" + partner + "&union_id=";
    		skuUrl +=unionId;
        	try {
	      		sku.setSkuUrl(URLEncoder.encode(skuUrl, "utf-8"));
			} catch (Exception e) {
				log.warn("URLEncoder.encode err for skuUrl");
			}
        	skus.add(sku);
        }
        if(skus.isEmpty()) log.debug("openapi 查询的sku 不存在");
        
		result.setSkus(skus);
		result.setSkuMappings(product.getSkuMappings());
        return new ResponseObject<SkuWithMappingVO>(result);
    }
	
	/**
	 * 获取地区下级
	 */
	@ApiOperation("get city children by cityId")
	@ResponseBody
	@RequestMapping("/zone/viewChildren")
	public ResponseObject<List<Zone>> viewChildren(@RequestParam String id) {
		List<Zone> zoneList=zoneService.listChildren(id);
		return new ResponseObject<List<Zone>>(zoneList);
	}
	
	/**
	 * 获取商品邮费
	 * 商品id
	 * zoneId 根据买家位置确定地区计算邮费
	 */
	@ApiOperation("get product's postage")
	@ResponseBody
	@RequestMapping("/product/postage2")
	public ResponseObject<BigDecimal> postage2(@RequestParam String productId, String zoneId) {
		BigDecimal big=new BigDecimal(-1);
		ResponseObject<BigDecimal> result =  new ResponseObject<BigDecimal>(big);
		
		Product product=productService.findProductById(productId);
		if (product == null) {
			log.warn("获取商品的邮费时，商品 "+productId+" 不存在");
			return result;
		}
		
		return postage(product, zoneId);
	}
	
	/**
	 * 获取商品的邮费
	 * 店铺ID:商品所属店铺ID
	 * 地区ID：用于判断自定义的邮费地区设置，可不传，就不走自定义邮费的分支了
	 * 
	 * ps:若数据发生变动会造成结果与预期不符
	 * 实现思路<br>
	 * 1.商店是否设置邮费--无，免邮  openapi/product/postage?shopId=1r5qpay9&skuIds=1r72mo01
	 * 2.是否符合满xx包邮  
	 *   2.1 有该项设置，但是未达到包邮金额  ?shopId=1r5xg7z5&skuIds=1r8r0ao9  0.02 <1
	 *   2.2 满足该项包邮金额 ?shopId=1r5xg7z5&skuIds=1rfacys9 2>1
	 * 3.是否在包邮商品设置列表中 ?shopId=1r5xg7z5&skuIds=1r8tkqs1
	 * 4.是否在自定义邮费地区设置中
	 *   4.1 父省级的邮费设置 ?shopId=1r5xg7z5&skuIds=1r8r0ao9&zoneId=1008
	 *   4.2 常用地区邮费设置
	 *     4.2.1 不在常设邮费区 ?shopId=1r5xg7z5&skuIds=1r8r0ao9&zoneId=3515
	 *     4.2.2 在 shopId=1r5xg7z5&skuIds=1r8r0ao9&zoneId=1037
	 * 5.默认邮费   shopId=b8qp&skuIds=exm1
	 */
	//@ApiOperation("get product's postage")
	//@ResponseBody
	//@RequestMapping("/product/postage")
	//public
	private ResponseObject<BigDecimal> postage(Product product, String zoneId) {
		ResponseObject<BigDecimal> noDisplay =  new ResponseObject<BigDecimal>(new BigDecimal(-1));
		
		// 获取店铺
		Shop shop = shopService.load(product.getShopId());
		if(shop == null){
			log.warn("获取商品的邮费时，商品所属店铺"+product.getShopId()+"不存在");
			 return noDisplay;  // -1 表示不显示邮费
		}
		
		// 获取店铺邮费设置,  TODO: 这里需要优化,如果发生异常默认免邮可能会被投诉
		ShopPostAge spa = shopPostAgeService.getPostAgeByShop(product.getShopId());
		if (spa == null || spa.getPostageStatus() == null || !spa.getPostageStatus()){
			// 没有设置邮费信息
			return new ResponseObject<BigDecimal>(BigDecimal.ZERO);
		} 
		
		// 满xxx包邮
		BigDecimal manBaoFree=spa.getFreeShippingPrice();
		if ( manBaoFree != null && 
			 manBaoFree.compareTo(BigDecimal.ZERO) > 0 &&  // 防止数据错误设置满0元包邮情况
			product.getPrice().compareTo(manBaoFree) >= 0) {   // 商品价格默认为sku最低价
			return new ResponseObject<BigDecimal>(BigDecimal.ZERO);
		}
		
		// 免邮商品中是否包含该商品
		List<String> productIds = spa.getFreeShippingGoods();
		if (productIds != null && !productIds.isEmpty()) {
			 if(productIds.contains(product.getId())){
				 return new ResponseObject<BigDecimal>(BigDecimal.ZERO);
			 }
		}
		
		// 自定义的地区中是否包含 邮费设置
		if( ! StringUtils.isBlank(zoneId)){
			BigDecimal customized = shopService.getCustomizedPostage(spa.getCustomizedPostage(),zoneId);
			if(customized != null){
				return new ResponseObject<BigDecimal>(customized);
			}
		}
		
		// 取全局邮费
	    BigDecimal ret = spa.getPostage(); 
		if(ret != null){
			return new ResponseObject<BigDecimal>(ret);
		}
		
		// 地区为空，全局邮费也没有获取的时候返回-1表明不是包邮的意思
		// 但是bug依然存在，zoneId不传就有可能导致商品显示的邮费与最后订单的邮费不一致
		return noDisplay;
	}
	
	/**
	 * 保存商品
	 */
	@ApiOperation("save product & sku info")
	@ResponseBody
    @RequestMapping(value = "/product/save")
    public ResponseObject<ProductVO> save(@ModelAttribute ProdutSaveForm form) {
		Product product = new Product();
		BeanUtils.copyProperties(form, product);
				
		if (StringUtils.isBlank(product.getName())) {
			product.setName(StringUtils.abbreviate(product.getDescription(), 25));
		}

		/*if(!StringUtils.isBlank(form.getExtUid())){
			User user=userService.loadExtUser(null, form.getExtUid());
			if(user!=null){
				form.setUserId();
			}
		}*/
		// 客户端上传商品
		product.setSource(ProductSource.CLIENT);
		product.setUserId(form.getUserId());
		product.setShopId(form.getShopId());
		
		if(form.getSkus()==null){
			List<Sku> skus=Lists.newArrayList();
			form.setSkus(skus.toArray(new Sku[skus.size()]));
		}
		
		List<Sku> skus=Lists.newArrayList(form.getSkus());
		
		// 第三方商品 id
		if(form.getPartnerProductId()!=null && !skus.isEmpty()){
			for(Sku sku:skus){
				sku.setPartnerProductId(form.getPartnerProductId());
			}
		}
		
		int rc = 0;
		if (StringUtils.isBlank(form.getId())) {
			rc = productService.create(product, skus, null, null, null);
		} else {
			rc = productService.update(product, skus, null,null, null);
		}

		if (rc == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product");
		}
		
		ProductVO vo = productService.load(product.getId());
		return new ResponseObject<ProductVO>(vo);
    }
	
	
	/**
	 * 保存sku
	 */
	/*@ApiOperation("save product's sku")
	@ResponseBody
    @RequestMapping(value = "/product/saveSku")
    public ResponseObject<ProductVO> saveSku(@ModelAttribute Sku... skus) {
		if(ArrayUtils.isEmpty(skus)){
			return null;
		}
		
		Product product=new Product();
		product.setId(skus[0].getProductId());

		productService.addSkus(product, Lists.newArrayList(skus));	
		
		ProductVO vo = productService.load(product.getId());
		return new ResponseObject<ProductVO>(vo);
	}*/
	
	 /*
	  * 保存店铺
	  * 
	  */
	/*@ResponseBody
    @RequestMapping(value = "/product/saveShop")
    public ResponseObject<Shop> saveSku(@ModelAttribute Shop shop) {
		//shopService.insert(shop);	
		shop.setStatus(ShopStatus.ACTIVE);
		shop.setOwnerId("3");
		shop.setDanbao(true);
		int id= shopMapper.insert(shop);
		shopMapper.addCode(shop.getId());
		return new ResponseObject<Shop>(shop);
	}*/
	
	/**
	 * 商品 code整理
	 */
	@ResponseBody
    @RequestMapping(value = "/product/arrangeProductCode")
    public ResponseObject<String> arrangeProductCode() {
		// 获取需要整理的商品信息
		List<Product> list=productService.listNoCodeProducts();
		List<String> ids=new ArrayList<String>();
		
		// 设置加密的code值
		for(Product p:list){
			// 修改商品信息
			ids.add(p.getId());
		}
		
		productService.addCode(ids.toArray(new String[ids.size()]));
		String tip=getTip(list.size(),"product"); 
		return new ResponseObject<String>(tip);
	}
	
	/**
	 * 用户整理
	 */
	@ResponseBody
    @RequestMapping(value = "/product/arrangeUserCode")
    public ResponseObject<String> arrangeUserCode() {
		List<User> list=userService.listNoCodeUsers();
		List<String> ids=new ArrayList<String>();
		
		// 设置加密的code值
		for(User p:list){
			ids.add(p.getId());
		}
		
		userService.addCode(ids.toArray(new String[ids.size()]));
		String tip = getTip(list.size(),"user");
		return new ResponseObject<String>(tip);
	}
	
	private String getTip(int size, String name) {
		String tip="";
		if(size==0){
			tip="已经没有需要添加code的"+name+"存在，无需整理。";
		}else{
			tip=name+" 添加code正在执行，需要整理 "+size+" 条记录，";
			if(size>100000){
				tip+="，估计需要耗时1分钟。";
			}else if(size>50000){
				tip+="，估计需要耗时30s。";
			}
		}
		return tip;
	}

	/**
	 * 店铺整理
	 */
	@ResponseBody
    @RequestMapping(value = "/product/arrangeShopCode")
    public ResponseObject<String> arrangeShopCode() {
		List<Shop> list=shopService.listNoCodeShops();
		List<String> ids=new ArrayList<String>();
		
		// 设置加密的code值
		for(Shop p:list){
			ids.add(p.getId());
		}
		
		shopService.addCode(ids.toArray(new String[ids.size()]));
		String tip=getTip(list.size(),"shop"); 
		return new ResponseObject<String>(tip);
	}
	
	/**
	 * sku 整理
	 */
	@ResponseBody
    @RequestMapping(value = "/product/arrangeSkuCode")
    public ResponseObject<String> arrangeSkuCode() {
		List<Sku> list=shopService.listNoCodeSkus();
		List<String> ids=new ArrayList<String>();
		
		// 设置加密的code值
		for(Sku p:list){
			ids.add(p.getId());
		}
		
		shopService.addSkuCode(ids.toArray(new String[ids.size()]));
		String tip=getTip(list.size(),"sku"); 
		return new ResponseObject<String>(tip);
	}
	
	
	/*
	 * 线下商品打标
	 * */
	@ResponseBody
	@RequestMapping(value = "/product/offline/sign")
	public ResponseObject<Boolean> markingOfflineProduct(@ModelAttribute OfflineProduct product) {
		try {
			ArrayList<OfflineProduct> list = new ArrayList<OfflineProduct>();
			list.add(product);
			this.productService.updateOfflineProduct(new ArrayList<OfflineProduct>(list));
		} catch(Exception e) {
			return new ResponseObject<Boolean>(false);
		}
		return new ResponseObject<Boolean>(true);
	}
}

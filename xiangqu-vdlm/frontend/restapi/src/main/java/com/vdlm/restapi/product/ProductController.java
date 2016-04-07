package com.vdlm.restapi.product;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.ProductImage;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.SkuMapping;
import com.vdlm.dal.model.Tag;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.dal.type.ProductSource;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.dal.vo.ProductAdmin;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.category.CategoryService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.vo.ProductImageVO;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.utils.HtmlUtils;

@Controller
public class ProductController extends FragmentAndDescController {

	@Autowired
	private UrlHelper urlHelper;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProdSyncMapper prodSyncMapper;
	@Autowired
	private ActivityService activityService;
	@Value("${xiangqu.cart.host.url}")
    String xiangquCartHost;
	
	/**
	 * 查看某个具体的商品<br>
	 * @param id
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/{id}")
	public ResponseObject<ProductVOEx> view(@PathVariable String id, HttpServletRequest req) {
		ProductVO product = productService.load(id);
		if (product == null) {
		    RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.NOT_FOUND,
					requestContext.getMessage("product.not.found"));
		}
		//test
		convertUrl(product); // 生成商品的图片URL
		// 约定获取商品的图文信息的时候，从productService中获取，实现：先获取富文本信息，再获取片段信息
		setFragmentAndDescInfo(product);
		// 说明是平台公告 不用展示到卖家端
		List<FragmentVO> fragments = product.getFragments();
		if (!CollectionUtils.isEmpty(fragments) && StringUtils.isEmpty(fragments.get(0).getId())) {
			if (fragments.size() == 1)
				product.setFragments(new ArrayList<FragmentVO>() );
			else
				product.getFragments().remove(0);
		}
		return new ResponseObject<ProductVOEx>(new ProductVOEx(product, urlHelper.genProductUrl(product.getId()), product.getImg(), null));
	}
	
	@ResponseBody
    @RequestMapping("/product/view")
    public ResponseObject<ProductVOEx> view2(String id, HttpServletRequest req) {
	    return this.view(id, req);
	}

	/**
	 * 删除某个具体的删除
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/delete/{id}")
	public ResponseObject<Boolean> delete(@PathVariable String id) {
		int result = productService.delete(id);
		return new ResponseObject<Boolean>(result > 0);
	}

	/**
	 * 商品下架
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/instock/{id}")
	public ResponseObject<Boolean> instock(@PathVariable String id) {
		int result = 0;
		try {
			result = productService.instock(id);
		} catch (Exception e) {
		}
		return new ResponseObject<Boolean>(result > 0);
	}

	/**
	 * 商品上架
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/onsale/{id}")
	public ResponseObject<Boolean> onsale(@PathVariable String id) {
		int result = productService.onsale(id);
		return new ResponseObject<Boolean>(result > 0);
	}

	/**
	 * 设置商品定时发布
	 * @param id
	 * @param forsaleDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/forsale/{id}")
	public ResponseObject<Boolean> forsale(@PathVariable String id, String forsaleDate) {
		Date forsaleAt = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			forsaleAt = df.parse(forsaleDate);
		} catch (Exception e) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product");
		}

		int result = productService.forsale(id, forsaleAt);
		return new ResponseObject<Boolean>(result > 0);
	}

	/**
	 * 商品佣金设置
	 */
	/*@ResponseBody
	@RequestMapping("/product/setcommission")
	public ResponseObject<Boolean> setCommission(String id, String commission) {
		return null;
	}*/
	
	/**
	 * 商品保存，新增和修改共用一个api，接口
	 * @param form
	 * @param errors
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/save")
	public ResponseObject<ProductVOEx> save(@Valid @ModelAttribute ProductForm form, Errors errors, HttpServletRequest req) {
		ControllerHelper.checkException(errors);
		RequestContext requestContext = new RequestContext(req);

		User user = userService.getCurrentUser();
		if(StringUtils.isBlank(user.getShopId())){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("shop.not.found"));
		}
		
		if (form.getSkus() == null || form.getSkus().size() == 0) {
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.sku.notBlank.message"));
		}
		
		if(form.getDelayed()>0 && form.getDelayAt()==0){
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.isDelay.delayAt.notBlank.message"));
		}

		Product product = new Product();
		BeanUtils.copyProperties(form, product);
		product.setRecommend(form.getRecommend() == 1);

		if (form.getStatus() == ProductStatus.FORSALE) {
			try {
				product.setForsaleAt( new Date(form.getForsaleDate()));
			} catch (Exception e) {
				throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.product.forsale.notBlank.message"));
			}
		}

		// 商品的名称取描述的前25个字符
		if (StringUtils.isBlank(product.getName())) {
			product.setName(StringUtils.abbreviate(product.getDescription(), 25));
		}

		product.setUserId(userService.getCurrentUser().getId());
		Shop shop = shopService.mine();
		product.setShopId(shop.getId());

		List<Sku> skus = initSku(form);
		List<SkuMapping> skuMapping = initSkuMapping(form);
		List<ProductImage> imgs = initImage(form);
		
		List<Tag> tags = initTag(form);
		product.setImg(imgs.get(0).getImg());
		
		int rc = 0;
		if (StringUtils.isBlank(form.getId())) {
		    product.setSource(ProductSource.CLIENT);
			rc = productService.create(product, skus, tags, imgs, skuMapping);
		} else {
			rc = productService.update(product, skus, tags, imgs, skuMapping);
		}

		if (rc == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product");
		}

		// 设置商品类目
		if(StringUtils.isNotBlank(form.getCategory())){
			categoryService.addProductCategory(product.getId(), form.getCategory());
		}
		
		ProductVO vo = productService.load(product.getId());
		convertUrl(vo); // 生成商品的图片URL
		formatSynchronousVal(vo);
		return new ResponseObject<ProductVOEx>(new ProductVOEx(vo, urlHelper.genProductUrl(vo.getId()), vo.getImg(), null));
	}

	/**
	 * 生成商品的图片URL
	 * @param vo
	 */
	private void convertUrl(ProductVO vo) {
		for (ProductImageVO img : vo.getImgs()) {
			img.setImgUrl(img.getImg());
		}
		//TODO
		vo.setCategory(categoryService.loadCategoryByProductId(vo.getId()));
	}

	/**
	 * 保存商品时的标签,把tagform转化成model
	 * @param form
	 * @return
	 */
	private List<Tag> initTag(ProductForm form) {
		List<Tag> tags = new ArrayList<Tag>();
		Tag tag = null;
		if (form.getTags() != null) {
			for (TagForm tagForm : form.getTags()) {
				tag = new Tag();
				BeanUtils.copyProperties(tagForm, tag);
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * 保存商品时的sku,把skuform转化成model
	 * @param form
	 * @return
	 */
	private List<Sku> initSku(ProductForm form) {
		List<Sku> skus = new ArrayList<Sku>();
		Sku sku = null;
		for (SkuForm skuForm : form.getSkus()) {
			sku = new Sku();
			BeanUtils.copyProperties(skuForm, sku);
			skus.add(sku);
		}
		return skus;
	}
	
	private List<SkuMapping> initSkuMapping(ProductForm form) {
		List<SkuMapping> skuMappings = new ArrayList<SkuMapping>();
		
		if(form.getSkuMappings() != null){
		
			SkuMapping skuMapping = null;
			for (SkuMappingForm skuMappingForm : form.getSkuMappings()) {
				skuMapping = new SkuMapping();
				BeanUtils.copyProperties(skuMappingForm, skuMapping);
				skuMappings.add(skuMapping);
			}
		}
		return skuMappings;
	}

	/**
	 * 保存商品时的图片,把imageForm转化成model
	 * @param form
	 * @return
	 */
	private List<ProductImage> initImage(ProductForm form) {
		List<ProductImage> imgs = new ArrayList<ProductImage>();
		ProductImage pimg = null;
		int idx = 0;   // 从0开始
		for (String img : form.getImgs()) {
			pimg = new ProductImage();
			pimg.setImgOrder(idx++);
			pimg.setImg(img);
			//快店发布、编辑商品，图片默认为组图
			pimg.setType(1);
			imgs.add(pimg);
		}
		return imgs;
	}
	
	/**
	 * 店长推荐列表zzd
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/list/recommend")
	public ResponseObject<List<ForsaleListVO>> listForRecommend(Pageable pageable) {
		String shopId = this.getCurrentUser().getShopId();
		return new ResponseObject<List<ForsaleListVO>>(forsale2Vo(generateImgUrl(productService.listProductsByRecommend(shopId, pageable))));
	}
	
	/**
	 * 计划发布列表
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/list/forsale")
	public ResponseObject<List<ForsaleListVO>> listForsale(Pageable pageable) {
		String shopId = this.getCurrentUser().getShopId();
		return new ResponseObject<List<ForsaleListVO>>(forsale2Vo(generateImgUrl(productService.listProductsByForSale(shopId, pageable))));
	}
	
	
	/**
	 * 计划发布列表by zzd pc端返回map,ap端没有返回总的记录数，故需再次新增此方法
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/list/forsalebyPC")
	public ResponseObject<Map<String,Object>> listForsaleByPC(Pageable pageable) {
		String shopId = this.getCurrentUser().getShopId();
	    Map<String,Object> map=new HashMap<String,Object>();
	    List<ForsaleListVO> lsForSale=forsale2Vo(generateImgUrl(productService.listProductsByForSale(shopId, pageable)));
	    map.put("list", lsForSale);
	  	map.put("categoryTotal", productService.countProductsByStatus(shopId, ProductStatus.FORSALE));
	    //return new ResponseObject<List<ForsaleListVO>>(forsale2Vo(generateImgUrl(productService.listProductsByForSale(shopId, pageable))));
	   return new ResponseObject<Map<String,Object>>(map);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	private List<ForsaleListVO> forsale2Vo(List<ProductVOEx> products) {
		Map<String, List<Product>> mapProducts = new LinkedHashMap<String, List<Product>>();
		List<Product> productsTmp = new ArrayList<Product>();
		String forsaleAt = null;
		for (Product product : products) {
			try{
			forsaleAt = DateFormatUtils.format(product.getForsaleAt(), "yyyy-MM-dd HH");
			
			// 15 minutes interval per list
			int minutes = product.getForsaleAt().getMinutes();
			if (0 <= minutes && minutes < 15) {
				forsaleAt += ":00";
			} else if ( 15 <= minutes && minutes < 30) {
				forsaleAt += ":15";
			} else if ( 30 <= minutes && minutes < 45) {
				forsaleAt += ":30";
			} else { // 45 <= minutes && minutes < 60
				forsaleAt += ":45";
			}
			productsTmp = (List<Product>)ObjectUtils.defaultIfNull(mapProducts.get(forsaleAt), new ArrayList<Product>());
			productsTmp.add(product);
			mapProducts.put(forsaleAt, productsTmp);
			}catch(Exception e){}
			
		}
		String serverTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		List<ForsaleListVO> vos = new ArrayList<ForsaleListVO>();
		try {
			for (String date : mapProducts.keySet()) {
				List<Product> ps = mapProducts.get(date);
				ForsaleListVO vo = new ForsaleListVO();
				vo.setDate(DateUtils.parseDate(date, "yyyy-MM-dd HH:mm"));
				vo.setServerTime(serverTime);
				vo.setList(ps);
				vos.add(vo);
			}
		} catch (ParseException e) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product");
		}

		return vos;
	}
	
	/**
	 * 按各种排序，获取商品列表
	 * 
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/product/list")
	public ResponseObject<Map<String, Object>> list(String order, 
			String direction, Pageable pageable) {
		// 签名做jian
		String shopId = this.getCurrentUser().getShopId();
		order = StringUtils.defaultIfBlank(order, "onsale"); // ios传参是onsale ,pc/安卓 是onsaleAt 
		direction = StringUtils.defaultIfBlank(direction, "desc");
		List<Product> products = null;
		
		if ("sales".equals(order)) { //按销量
			products = productService.listProductsBySales(shopId, pageable, Direction.fromString(direction));
		} else if ("amount".equals(order)) { //按库存
			products = productService.listProductsByAmount(shopId, pageable, Direction.fromString(direction));
		} else if ("soldout".equals(order)) { //按下架时间
			products = productService.listProductsBySoldout(shopId, pageable, Direction.fromString(direction));
		} else if ("statusDraft".equals(order)) { //按草稿
			products = productService.listProductsByStatusDraft(shopId, pageable);
		} else if ("outofstock".equals(order)) { //按缺货
			products = productService.listProductsByOutOfStock(shopId, pageable);			
		} else if ("postage".equalsIgnoreCase(order)) {
			products = productService.listProductsByPostAge(shopId, pageable, Direction.fromString(direction));
		} else if ("price".equals(order)){
			/** by zzd 根据市场价格进行排序**/
			products = productService.listProductsByPrice(shopId, pageable, Direction.fromString(direction));	
		}else if("onsaleAt".equals(order) || "onsale".equals(order)){
			// 在售的上架商品
			products = productService.listProductsByOnsaleAt(shopId, pageable);
		}
		
		Map<String, Object> aRetMap = new HashMap<String, Object>();
		aRetMap.put("categoryTotal", productService.getLastTotalCnt(shopId, order, new HashMap<String, Object>()));
		if ("postage".equalsIgnoreCase(order))
			aRetMap.put("list", generateImgUrlEx(products));
		 else
			aRetMap.put("list", generateImgUrl(products));
		
		return new ResponseObject<Map<String, Object>>(aRetMap);
	}

	/**
	 * 商品查询
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/search/{shopId}/{key}")
	public ResponseObject<List<ProductVOEx>> search(@PathVariable String shopId,
			@PathVariable String key) { 
		List<Product> list = productService.search(shopId, key);
		List<ProductVOEx> voList = new ArrayList<ProductVOEx>();
		for (int i = 0; i < list.size(); i++) {
			Product product = list.get(i);
			formatSynchronousVal(product);
			ProductVO vo = new ProductVO(product);
			ProductVOEx voEX = new ProductVOEx(vo, urlHelper.genProductUrl(product.getId()), product.getImg(), null);
			vo.setImgUrl(product.getImg());
			voList.add(voEX);
		}
		return new ResponseObject<List<ProductVOEx>>(voList);
	}
	
	/**
	 * 商品查询
	 *  modify by zzd 新增 用于pc端,进行分页
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/searchbyPc/{shopId}/{key}")
	public ResponseObject<Map<String,Object>> search(@PathVariable String shopId,
			@PathVariable String key,Pageable page) { 
		Map<String,Object> map=new HashMap<String,Object>();
		List<Product> list = productService.search(shopId, key,page);
		List<ProductVOEx> voList = new ArrayList<ProductVOEx>();
		for (int i = 0; i < list.size(); i++) {
			Product product = list.get(i);
			ProductVO vo = new ProductVO(product);
			ProductVOEx voEX = new ProductVOEx(vo, urlHelper.genProductUrl(product.getId()), product.getImg(), null);
			vo.setImgUrl(product.getImg());
			voList.add(voEX);
			
		}
		 map.put("list",voList);
		 Long count=productService.CountTotalByName(shopId, key);
		 map.put("categoryTotal", count);
		return new ResponseObject<Map<String,Object>>(map);
	}
	
	/**
	 * 商品管理首页
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/product/index")
	public ResponseObject<Map<String, Object>> productIndex() {
	    String shopId = this.getCurrentUser().getShopId();
		Map<String, Object> index = new HashMap<String, Object>();
		index.put("draft", productService.countProductsByStatus(shopId, ProductStatus.DRAFT)); // 草稿
		index.put("forsale", productService.countProductsByStatus(shopId, ProductStatus.FORSALE)); // 发布计划
		index.put("outofstock", productService.countProductsByOutofStock(shopId)); // 缺货
		index.put("onsale", productService.countProductsByStatus(shopId, ProductStatus.ONSALE)); // 上架
		List<Product> products = productService.listProductsByOnsaleAt(shopId, new PageRequest(0, 10));
		index.put("products", generateImgUrl(products));

		return new ResponseObject<Map<String, Object>>(index);
	}

	@ResponseBody
    @RequestMapping("/product/all")
    public ResponseObject<List<ProductAdmin>> productAll(Pageable pager) {
	    Map<String, Object> params = new HashMap<String, Object>();
        List<ProductAdmin> products = productService.listProductsByAdmin(params, pager);
        return new ResponseObject<List<ProductAdmin>>(products);
    }
	
	/**
     * TODO 该接口不需要用户登录
     * @param pager
     * @return
     */
	@ResponseBody
    @RequestMapping("/product/skus")
    public ResponseObject<SkuWithMappingVO> skusWithMapping(String productId, String partner, HttpServletRequest req) {
		ProductVO product = productService.load(productId);
		if(product == null){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product.not.found");
		}
		   
		SkuWithMappingVO vo = new SkuWithMappingVO();
		
		List<Sku> skus = genPartnerSkus(productId, req);
	
		vo.setSkus(skus);
		vo.setSkuMappings(product.getSkuMappings());
		
	    return new ResponseObject<SkuWithMappingVO>(vo);
    }
	
    /**
     * TODO 该接口不需要用户登录
     * @param pager
     * @return
     */
	@ResponseBody
    @RequestMapping("/product/{id}/skus")
    public ResponseObject<List<Sku>> skus(@PathVariable String id, HttpServletRequest req) {
        return new ResponseObject<List<Sku>>(genPartnerSkus(id, req));
    }
	
	private List<Sku> genPartnerSkus(String productId, HttpServletRequest req){
		Product product = productService.findProductById(productId);
		RequestContext requestContext = new RequestContext(req);
		if(product == null){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, requestContext.getMessage("product.not.found"));
		}
		
		List<Sku> products = productService.listSkus(productId);
		List<ProdSync> aList = prodSyncMapper.selectByPassedShopId(product.getShopId());
		User aUser = userService.loadByLoginname("xiangqu");
        List<Sku> result = new ArrayList<Sku>();
        for (Sku sku : products) {
        	String skuUrl = xiangquCartHost+"/cart/checkout?skuId=" + sku.getId() + "&partner=xiangqu";
        	if (product != null ) { 
        	if (aList != null && aList.size() >0) {
        			skuUrl += "&union_id=";
        			skuUrl += aList.get(0).getUnionId();
        		} else {
        			if (aUser != null) {
        				skuUrl += "&union_id=";
        				skuUrl += aUser.getId();
        			}
        		}
        	}
        	try {
	      		sku.setSkuUrl(URLEncoder.encode(skuUrl, "utf-8"));
			} catch (Exception e) {
				log.warn("URLEncoder.encode err for skuUrl");
			}
            if (sku.getAmount() > 0) {
                result.add(sku);
            }
        }

        return result;
	}
	
	private List<ProductVOEx> generateImgUrlEx(List<Product> products){
		List<ProductVOEx> exs = new ArrayList<ProductVOEx>();
		ProductVOEx ex = null;
		if (products == null) return exs;
		for(Product product : products){
			ex = new ProductVOEx(new ProductVO(product), urlHelper.genProductUrl(product.getId()), 
					product.getImg(), null);
			ex.setCategory(categoryService.loadCategoryByProductId(product.getId()));
			exs.add(ex);
		}
		return exs;
	}
	
	
	private List<ProductVOEx> generateImgUrl(List<Product> products){
		List<ProductVOEx> exs = new ArrayList<ProductVOEx>();
		ProductVOEx ex = null;
		if (products == null) return exs;
		for(Product product : products){
			//Activity activity = activityService.obtainProductCurrentActivities(product.getId());
			formatSynchronousVal(product);
			ex = new ProductVOEx(new ProductVO(product), urlHelper.genProductUrl(product.getId()), 
					product.getImg(), null);
			ex.setCategory(categoryService.loadCategoryByProductId(product.getId()));
			exs.add(ex);
		}
		return exs;
	}
	
	// 格式化商品来源渠道
	private void formatSynchronousVal(Product product) {
		String synchronousVal = "";
		String synchronous = product.getSynchronousFlag();
		if(synchronous !=null && synchronous.length()>1){
			if(synchronous.substring(9,10).equals("1"))
				synchronousVal += "XIANGQU" + ",";
		}
		product.setSynchronousFlag(synchronousVal);
	}
	
	/**
	 * 获取延迟发货商品列表
	 * 
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/product/list/delay")
	public ResponseObject<Map<String, Object>> productListDelay(Pageable pageable) {
	    String shopId = this.getCurrentUser().getShopId();
		List<Product> products = null;
		long total = productService.countDelayProduct(shopId);
		if(total>0){
			products = productService.listDelayProduct(shopId, pageable);
		}else{
			products = new ArrayList<Product>();
		}
		Map<String, Object> aRetMap = new HashMap<String, Object>();
		aRetMap.put("categoryTotal", total);
		aRetMap.put("list", generateImgUrl(products));
		return new ResponseObject<Map<String, Object>>(aRetMap);
	}
	
	@ResponseBody
	@RequestMapping("/product/batch-onsale")
	public ResponseObject<Map<String, Object>> batchOnsale(@Valid @ModelAttribute BatchIdsForm form, Errors errors){
		ControllerHelper.checkException(errors);
		List<String> successList = new ArrayList<String>();
		List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
		for(int i=0;i<form.getIds().size();i++){
			Map<String, String> failMap = new LinkedHashMap<String, String>();
			failMap.put("id", form.getIds().get(i));
			try {
				int result = productService.onsale(form.getIds().get(i));
				if(result>0){
					successList.add(form.getIds().get(i));
					continue;
				}else{
					failMap.put("message", "update fail");
					failList.add(failMap);
				}
			} catch (Exception e) {
				failMap.put("message", e.getMessage());
				failList.add(failMap);
			}
		}
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("isAllSuccess", successList.size()==form.getIds().size());
		json.put("successList", successList);
		json.put("failList", failList);
		return new ResponseObject<Map<String, Object>>(json);
	}
	
	@ResponseBody
	@RequestMapping("/product/batch-instock")
	public ResponseObject<Map<String, Object>> batchInstockWithLock(@Valid @ModelAttribute BatchIdsForm form, Errors errors){
		ControllerHelper.checkException(errors);
		List<String> successList = new ArrayList<String>();
		List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
		for(int i=0;i<form.getIds().size();i++){
			Map<String, String> failMap = new LinkedHashMap<String, String>();
			failMap.put("id", form.getIds().get(i));
			try {
				int result = productService.instock(form.getIds().get(i));
				if(result>0){
					successList.add(form.getIds().get(i));
					continue;
				}else{
					failMap.put("message", "update fail");
					failList.add(failMap);
				}
			} catch (Exception e) {
				failMap.put("message", e.getMessage());
				failList.add(failMap);
			}
		}
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("isAllSuccess", successList.size()==form.getIds().size());
		json.put("successList", successList);
		json.put("failList", failList);
		return new ResponseObject<Map<String, Object>>(json);
	}
	
	/**
	 * 审核同步商品来源
	 * @param sId
	 * @param sourceVal 资源平台
	 * @param examineRet 审核结果 1:通过 0：不通过
	 * @return
	 */		
	@ResponseBody
	@RequestMapping("/product/synchronous")
	public ResponseObject<Boolean> synchronous(String[] ids, String sourceVal, Integer examineRet) {
	    // need to be refine
		int result = 0;
		List<String> lsId = new ArrayList<String>(ids.length);
		for(String id : ids)
			lsId.add(id);
		if(examineRet != 0 && examineRet != 1)
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "state.error");
		List<String> sKeys = productService.obtainDbSynchronous(lsId);
		if(sKeys.size() > 0){
			String dbVal = null; 
			List<String> sourceCodes = new ArrayList<String>(ids.length);
			for(int i=0;i<sKeys.size();i++){
				if(sKeys.get(i) == null)
					dbVal = HtmlUtils.padLeft("0", 8);
				else if(sKeys.get(i).length() < 2)
					dbVal = HtmlUtils.padLeft(sKeys.get(i), 8);
				else 
					dbVal = sKeys.get(i);
				int t = 0;
				if(sourceVal != null){
					if(sourceVal.equals("xiangqu")){
						if(examineRet > 0)
							t = examineRet | Integer.valueOf(dbVal.substring(9,10));
						else
							t = examineRet & Integer.valueOf(dbVal.substring(9,10));
						sourceCodes.add(dbVal.substring(0,9) + String.valueOf(t));
					}
					else if(sourceVal.equals("mogujie")){
						if(examineRet > 0)
							t = examineRet | Integer.valueOf(dbVal.substring(8,9));
						else
							t = examineRet & Integer.valueOf(dbVal.substring(8,9));
						sourceCodes.add(dbVal.substring(0,8) + String.valueOf(t) + dbVal.substring(9,10));
					}	
					else{
						throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "platform.error");
					}
				}
				else{
					throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "platform.error");
				}
				result = productService.synchronous(lsId.get(i), sourceCodes.get(i));
			}
		}
		else {
			throw new BizException(GlobalErrorCode.NOT_FOUND, "product.not.found");
		}
		return new ResponseObject<Boolean>(result > 0);
	}
	
	/**
	 * 获取所有在售商品（ap端不存在）
	 * @param pageable
	 * @return
	 */
	public ResponseObject<List<Product>> listProductsAll(Pageable pageable){
	    String shopId = this.getCurrentUser().getShopId();
		return new ResponseObject<List<Product>>(productService.listProducts(shopId));
	}
	
	/**
	 * 修复某个商品的sku 价格
	 * @param productId
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/fix/sku/{productId}")
	public ResponseObject<Boolean> fixSku(@PathVariable String productId) {
		log.debug("----");
		System.out.print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		productService.fixSku(productId);
		
		return new ResponseObject<Boolean>(Boolean.TRUE);
	}
	
}

package com.vdlm.restapi.union;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.ProductImage;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.Sku;
import com.vdlm.dal.model.Tag;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.file.ImageUploadForm;
import com.vdlm.restapi.product.ProductVOEx;
import com.vdlm.restapi.product.SkuForm;
import com.vdlm.service.common.SignService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductImageVO;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.user.UserService;
import com.vdlm.utils.MD5Util;

@Controller
public class UnionShopController extends BaseController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private SignService signService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Autowired
	private UrlHelper urlHelper;

	private boolean signCheck(HttpServletRequest req) {
		try {
			return signCheck(req, null);
		} catch (IOException e) {
			log.error("第三方店铺验证出错",e);
		}
		return false;
	}
	
	private boolean signCheck(HttpServletRequest req, List<MultipartFile> file) throws IOException{
		String sign = req.getParameter("sign");
		String unionId = req.getParameter("union_id");
        String queryString = getQueryString(req);
        StringBuffer fileMd5 = new StringBuffer("");
        if(file != null && file.size() > 0){
        	for(int i = 0; i < file.size(); i++){
	        	if(fileMd5.length() > 0) fileMd5.append("&");
	        	fileMd5.append("file["+i+"]=" + MD5Util.MD5Encode(file.get(i).getInputStream())); 
        	}
        }
        if(fileMd5.length() > 0){
        	queryString += "&" + fileMd5;
        }
        return signService.signCheck(unionId, sign, queryString);
	}
	
	/**
	 * 开放给第三方使用
	 * @param form
	 * @param errors
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/partner/_f/u")
	public ResponseObject<List<UpLoadFileVO>> uploadUnion(@Valid @ModelAttribute ImageUploadForm form, String union_id, Errors errors, HttpServletRequest request)
			throws Exception {
				
		log.info("partner upload file unionId=" + union_id);
		//TODO，考虑放到拦截器，减少代码侵入
		if(!signCheck(request, form.getFile())){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "验签失败");
		}
		if(form.getBelong() == null){
			log.warn( "upload warning 文件belong为空  file length=[" + form.getFile().size() + "]");
			RequestContext requestContext = new RequestContext(request);
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.fileBelong.message"));
		}
		ControllerHelper.checkException(errors);
		return new ResponseObject<List<UpLoadFileVO>>(resourceFacade.uploadFile(form.getFile(), form.getBelong()));
	}
	
	/**
	 * 按各种排序，获取商品列表
	 * 
	 * @param shopId
	 * @param pageable
	 * @return
	 */
	@ResponseBody
    @RequestMapping("/partner/p/list")
	public ResponseObject<List<Product>> list(HttpServletRequest req, String order, 
			String direction, Pageable pageable) {
	    //TODO，考虑放到拦截器，减少代码侵入
		if(!signCheck(req)){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "验签失败");
		}
		
		User user = userService.load(req.getParameter("union_id"));
		String shopId = user.getShopId();
		
		order = StringUtils.defaultIfBlank(order, "onsaleAt");
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
		} else {
			products = productService.listProductsByOnsaleAt(shopId, pageable);
		}
		
		//return new ResponseObject<List<ProductVOEx>>(generateImgUrl(products));
		return new ResponseObject<List<Product>>(products);
	}
	
	/**
	 * 第三方合作账户直接调用 接口保存商品<br>
	 * 使用访问白名单方式实现安全控制，程序里不做处理<br>
	 * 逻辑与快店商品保存一致，但是不包含<b>延迟发货</b>，<b>商品预售</b>，<b>商品tag设置</b>，<b>商品类目设置</b><br>
	 * @param unionId
	 * @param form
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping("partner/p/save")
	public ResponseObject<ProductVOEx> unionSaveProduct(String union_id, 
			@Valid @ModelAttribute UnionSaveProductForm form, HttpServletRequest req){
		RequestContext requestContext = new RequestContext(req);
	    //TODO，考虑放到拦截器，减少代码侵入
		if(!signCheck(req)){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "验签失败");
		}
		
		//无获取currentUser逻辑，直接根据unionId获取
		//只处理user.roles=2的用户的请求
		User user = userService.load(union_id);
		if(user==null || user.getRoles()!=2){
			//无权限操作
			throw new BizException(GlobalErrorCode.UNAUTHORIZED, "unauthorized, unionId="+union_id);
		}
		Shop shop = shopService.findByUser(union_id);
		if(shop == null){
			//TODO create()?exception
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("shop.not.found"));
		}
		if (form.getSkus() == null || form.getSkus().size() == 0) {
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.sku.notBlank.message"));
		}
		
		Product product = new Product();
		BeanUtils.copyProperties(form, product);
		product.setStatus(ProductStatus.ONSALE);
		
		product.setRecommend(form.getRecommend() == 1);
		
		// 商品的名称取描述的前25个字符
		if (StringUtils.isBlank(product.getName())) {
			product.setName(StringUtils.abbreviate(product.getDescription(), 25));
		}
		
		product.setUserId(union_id);
		product.setShopId(shop.getId());

		List<Sku> skus = initSku(form);
		List<ProductImage> imgs = initImage(form);
		//壁纸不需要tags
		List<Tag> tags = null;//initTag(form);
		product.setImg(imgs.get(0).getImg());

		int rc = 0;
		if (StringUtils.isBlank(form.getId())) {
			rc = productService.createByUnion(product, skus, tags, imgs);
		} else {
			rc = productService.updateByUnion(product, skus, tags, imgs);
		}

		if (rc == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "product");
		}
		
		ProductVO vo = productService.load(product.getId());
		convertUrl(vo); // 生成商品的图片URL
		
		//梦象壁纸直接跳转的订单H5页面
		List<Sku> skusList = vo.getSkus();
		if(skusList!=null&&skusList.size()>0){
			String skuId = skusList.get(0).getId();
			vo.setDirectCartUrl("/cart/next?skuId="+skuId);
		}
		return new ResponseObject<ProductVOEx>(new ProductVOEx(vo, urlHelper.genProductUrl(vo.getId()), vo.getImg(), null));
	}
	
	/**
	 * 保存商品时的sku,把skuform转化成model
	 * 
	 * @param form
	 * @return
	 */
	private List<Sku> initSku(UnionSaveProductForm form) {
		List<Sku> skus = new ArrayList<Sku>();
		Sku sku = null;
		for (SkuForm skuForm : form.getSkus()) {
			sku = new Sku();
			BeanUtils.copyProperties(skuForm, sku);
			skus.add(sku);
		}
		return skus;
	}

	/**
	 * 保存商品时的图片,把imageForm转化成model
	 * 
	 * @param form
	 * @return
	 */
	private List<ProductImage> initImage(UnionSaveProductForm form) {
		List<ProductImage> imgs = new ArrayList<ProductImage>();
		ProductImage pimg = null;
		int idx = 0;
		for (String img : form.getImgs()) {
			pimg = new ProductImage();
			pimg.setImgOrder(idx++);
			pimg.setType(1);
			pimg.setImg(img);
			imgs.add(pimg);
		}
		return imgs;
	}
	
	/**
	 * 生成商品的图片URL
	 * 
	 * @param vo
	 */
	private void convertUrl(ProductVO vo) {
		for (ProductImageVO img : vo.getImgs()) {
			img.setImgUrl(img.getImg());
		}
	}
}

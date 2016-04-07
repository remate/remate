package com.vdlm.bos.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.bos.BaseController;
import com.vdlm.bos.vo.Json;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.vo.ProductAdmin;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.syncevent.SyncEventService;

@Controller
public class ProductController extends BaseController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Autowired
	private SyncEventService syncEventService;
	
	@Value("${site.web.host.name}")
	String siteHost;
	
	@RequestMapping(value = "product")
	public String list(Model model, HttpServletRequest req) {
		return "product/products";
	}
	
	@RequestMapping(value = "/product/syncUpdate")
	public void startFirstSync(String shopId, Boolean syncProd) {
			syncEventService.startUpdateSync(shopId, syncProd);
	}
	
	@ResponseBody
	@RequestMapping(value = "product/list")
	public Map<String, Object> list(ProductSearchForm form, String order, String direction, Pageable pageable) {
		order = StringUtils.defaultIfBlank(order, "onsaleAt");
		direction = StringUtils.defaultIfBlank(direction, "desc");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("archive", ObjectUtils.defaultIfNull(form.getArchive_kwd(), Boolean.FALSE));
		if(StringUtils.isNotBlank(form.getProduct_name_kwd())){
			params.put("productName", "%" + form.getProduct_name_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getShop_name_kwd())){
			params.put("shopName", "%" + form.getShop_name_kwd() + "%" );
		}
		if(StringUtils.isNotBlank(form.getCreated1_kwd())){
			params.put("createdFrom", form.getCreated1_kwd());
		}
		if(StringUtils.isNotBlank(form.getCreated2_kwd())){
			params.put("createdTo", form.getCreated2_kwd());
		}
		if(StringUtils.isNotBlank(form.getUpdated1_kwd())){
			params.put("updatedFrom", form.getUpdated1_kwd());
		}
		if(StringUtils.isNotBlank(form.getUpdated2_kwd())){
			params.put("updatedTo", form.getUpdated2_kwd());
		}
		params.put("productStatus", form.getProduct_status_kwd());
		params.put("containCloseShop", form.getContain_closed_shop_kwd());
		List<ProductAdmin> products = productService.listProductsByAdmin(params, pageable);
		generateImgUrl(products);
		
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", productService.countProductsByAdmin(params));
		data.put("rows", products);
		
		return data;
	}
	
	private void  generateImgUrl(List<ProductAdmin> products){
		for(Product product : products){
			product.setImg(resourceFacade.resolveUrl(product.getImg() + "|" + ResourceFacade.IMAGE_S025));
		}
	}

	@ResponseBody
	@RequestMapping(value = "product/delete")
	public Boolean delete(String[] ids) {
		log.info( super.getCurrentUser().getPhone() + "删除商品productId in:" + ids);
		return productService.deleteByAdmin(ids) == ids.length;
	}
	
	@ResponseBody
	@RequestMapping(value = "product/undelete")
	public Boolean undelete(String[] ids) {
		log.info( super.getCurrentUser().getPhone() + "恢复商品productId in:" + ids);
		return productService.undeleteByAdmin(ids) == ids.length;
	}
	
	@RequestMapping(value = "product/view/{id}")
	public String view(@PathVariable("id") String productId, Model model) {
		model.addAttribute("product", productService.loadByAdmin(productId));
		return "product/view";
	}
	
	@RequestMapping(value = "/redirectProductView/{productId}")
	public String redirectUserShop(@PathVariable("productId") String productId) {
		return "redirect:"+siteHost+"/p/"+productId;
	}
	
	@ResponseBody
	@RequestMapping(value = "product/instock")
	public Json instock(String[] ids) {
		Json json = new Json();
		try {
			if(productService.instockByAdmin(ids))
				json.setMsg("商品下架成功");	
			else{
				json.setRc(Json.RC_FAILURE);
				json.setMsg("商品下架失败");	
			}
			
		} catch (Exception e) {
			json.setRc(Json.RC_FAILURE);
			json.setMsg("下架失败;" + e.getMessage());
		}
		log.info( super.getCurrentUser().getId() + "商品id=[" + ids + "] rc=[" + json.getRc() + "]");
		return json;
	}
}

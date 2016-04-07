package com.vdlm.restapi.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.url.UrlHelper;
import com.vdlm.dal.model.Activity;
import com.vdlm.dal.model.Category;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.User;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.product.ProductVOEx;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.category.CategoryService;
import com.vdlm.service.category.TermTaxonomyService;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductVO;

@Controller
public class CategoryController extends BaseController {

    @Autowired
    private TermTaxonomyService termTaxonomyService;

    @Autowired
    private ResourceFacade resourceFacade;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UrlHelper urlHelper;

    @Value("${site.web.host.name}")
    private String siteHost;
    
    /**
     * 取类目下面的商品,排序
     * http://localhost:8888/v2/category/product/list?id=1302
     * @param id
     * @param req
     * @param pageable
     * @return
     */
	@ResponseBody
    @RequestMapping("/category/product/list")
    public ResponseObject<Map<String, Object>> products(@RequestParam String id,Boolean isDesc, String orderName, Integer size,
			Integer page) {
		Pageable pageable = initPage(page, size, orderName, isDesc);
		List<Product> prods = new ArrayList<Product>();
        long count = 0;
        if ("0".equals(id)) {
        	// 获取未分类的商品
        	// http://localhost:8888/v2/category/product/list?id=0&page=0&size=4&orderName=price
            prods = categoryService.listUnCategoriedProducts(pageable);
            count = categoryService.countUnCategoriedProducts();
        } else if("-1".equals(id)) {
        	// http://localhost:8888/v2/category/product/list?id=-1&page=0&size=4&orderName=market_price&isDesc=false
        	// 获取全部分类
        	// 全部商品按上架时间显示
        	prods = productService.listAllProductsByOnsaleAt(getCurrentUser().getShopId(),pageable);
        	count =productService.countAllShopProduct(getCurrentUser().getShopId());
        }else{
        	// 获取分类
            Category category = termTaxonomyService.load(id);
            if (category == null) {
                throw new BizException(GlobalErrorCode.NOT_FOUND, "分类不存在");
            }
            // 获取分类下的所有商品
            count = categoryService.countProductsUnderCategory(id);
            if (count != 0)
            	prods = categoryService.listUnCategoriedProducts(id, pageable);
        }
        
        Map<String, Object> aRetMap = new HashMap<String, Object>();
        aRetMap.put("categoryTotal", count);
        aRetMap.put("list", generateImgUrl(prods));
        aRetMap.put("page", pageable);
        return new ResponseObject<Map<String, Object>>(aRetMap);
    }
    
    @ResponseBody
    @RequestMapping("/category/{id}/products")
    public ResponseObject<List<ProductVO>> products2(@PathVariable String id,
            HttpServletRequest req, Pageable pageable) {
        User user = null;
        try {
            user = getCurrentUser();
        } catch (Exception e) {
            log.error("user does not login");
        }
        Activity activity = activityService.load(id);
        if (activity == null) {
            throw new BizException(GlobalErrorCode.NOT_FOUND, "分类不存在");
        }
        // Pageable pageable = new PageRequest(page - 1, DEFAULT_PAGE_SIZE);
        List<Product> prods = productService.listProductsByActId(id, pageable);
        List<ProductVO> products = new ArrayList<ProductVO>();
        for (Product p : prods) {
            ProductVO prodVO = new ProductVO(p);
            prodVO.setImgUrl(p.getImg());
            if (user != null) {
                prodVO.setUrl(siteHost + "/p/" + p.getId() + "?union_id=" + user.getId());
            } else {
                prodVO.setUrl(siteHost + "/p/" + p.getId());
            }
            prodVO.setCommission(prodVO.getPrice().multiply(prodVO.getCommissionRate()));
            products.add(prodVO);
        }
        return new ResponseObject<List<ProductVO>>(products);
    }
    
    /**
     *编辑/修改商品的分类
     *http://localhost:8888/v2/category/product/add?categoryId=3&productIds[0]=1r69lc0p
     * @param form
     * @return
     */
    @ResponseBody
    @RequestMapping("/category/product/add")
    public ResponseObject<Boolean> addProduct(@Valid @ModelAttribute AddOrRemoveProductForm form) {
        categoryService.addProductsCategory(form.getProductIds(), form.getCategoryId());
        return new ResponseObject<Boolean>(true);
    }
    
    @ResponseBody
    @RequestMapping("/category/product/remove")
    public ResponseObject<Boolean> removeProduct(@Valid @ModelAttribute AddOrRemoveProductForm form) {
        categoryService.removeProductsCategory(form.getProductIds(), form.getCategoryId());
        return new ResponseObject<Boolean>(true);
    }
    
    private List<ProductVOEx> generateImgUrl(List<Product> products){
        List<ProductVOEx> exs = new ArrayList<ProductVOEx>();
        ProductVOEx ex = null;
        for(Product product : products){
            ex = new ProductVOEx(new ProductVO(product), urlHelper.genProductUrl(product.getId()), product.getImg(), null);
            ex.setCategory(categoryService.loadCategoryByProductId(product.getId()));
            exs.add(ex);
        }
        return exs;
    }
    
    /*
	 * 封装page对象 sort结构示例 Iterator<org.springframework.data.domain.Sort.Order>
	 * orders = page.getSort().iterator(); if(orders.hasNext()){
	 * orders.next().getDirection(); orders.next().getProperty() }
	 */
	protected Pageable initPage(Integer page, Integer size, String orderName,
			Boolean isDesc) {
		if (size == null || size < 0) {
			size = 10;
		}

		if (page == null || page < 0) {
			page = 0;
		}
		if (isDesc != null || !StringUtils.isBlank(orderName)) {
			Direction orderType = Direction.DESC;
			if (isDesc != null && !isDesc) {
				orderType = Direction.ASC;
			}

			return new PageRequest(page, size, orderType, orderName);
		}
		return new PageRequest(page, size);
	}
    
}

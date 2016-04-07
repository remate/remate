package com.vdlm.restapi.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.ProductDesc;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.product.ProductService;

@Controller
public class ProductDescController extends BaseController {

	@Autowired
	private ProductService productService;
	
	/**
	 * 编辑商品的富文本信息
	 * 接口功能已废弃
	 */
	@ResponseBody
    @RequestMapping("/product/saveProductDesc")
    public ResponseObject<Boolean> createProductDesc(@ModelAttribute ProductDesc productDesc) {
		return new ResponseObject<Boolean>(true);
	}
	
	/**
	 * 修改product enable_desc字段  开启/关闭富文本
	 * 接口功能已废弃
	 */
	@ResponseBody
    @RequestMapping("/product/changeEnableDesc")
    public ResponseObject<Product> changeEnableDesc(@RequestParam String productId, @RequestParam Boolean enableDesc) {
		return new ResponseObject<Product>(productService.load(productId));
	}

}

package com.vdlm.web.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.service.activity.ActivityService;
import com.vdlm.service.product.ProductService;
import com.vdlm.web.BaseController;

@Controller
@RequestMapping("/market")
public class MarketController extends BaseController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@RequestMapping("/b8qp/{actId}")
	public String view(@PathVariable String actId, String union_id, Model model, Pageable pageable) throws Exception {
		//370 9块9包邮  mkj6
		//124  女装  18ygs
		//247  海淘  26o6v
		model.addAttribute("products", productService.listProductsByActId(actId, pageable));
		model.addAttribute("union_id", union_id);
		model.addAttribute("actObj", activityService.load(actId));
		return "activity/20140728/act";
	}	
}

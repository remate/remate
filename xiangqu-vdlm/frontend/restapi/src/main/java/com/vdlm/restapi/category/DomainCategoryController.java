package com.vdlm.restapi.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.DomainCategory;
import com.vdlm.dal.model.DomainCategoryVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.category.DomainCategoryService;

@Controller
public class DomainCategoryController extends BaseController {

	@Autowired
	private DomainCategoryService domainCategoryService;
	
	@ResponseBody
	@RequestMapping("/domain/category")
	public ResponseObject<List<DomainCategoryVO>> getDomainCategorybyParentId(String id) throws Exception {
		List<DomainCategoryVO> list = new ArrayList<DomainCategoryVO>();
	    List<DomainCategory> categories = domainCategoryService.getListbyParentId(id);
	    if(null!=categories&&categories.size()>0){
	    for (DomainCategory cat : categories) {
	    	DomainCategoryVO vo = new DomainCategoryVO();
	        BeanUtils.copyProperties(cat, vo);
	        long childCount = domainCategoryService.getChildcountbyId(cat.getId());
	        vo.setChildcount(childCount);
	        list.add(vo);
         }
	    }
		return new ResponseObject<List<DomainCategoryVO>>(list);
	}
}

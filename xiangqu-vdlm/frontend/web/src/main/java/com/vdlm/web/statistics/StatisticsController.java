package com.vdlm.web.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.ShopAccess;
import com.vdlm.dal.model.User;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.shop.ShopAccessService;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;

@Controller
public class StatisticsController extends BaseController{
	@Autowired
	ShopAccessService shopAccessService;
	
	@ResponseBody
	@RequestMapping(value = "/statistics")
	public ResponseObject<Boolean> order(@ModelAttribute StatisticsForm form, Model model){
		ShopAccess access = new ShopAccess();
		if (form.getShopId() == null){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,"shopId不能为空");
		} else {
			access.setShopId(form.getShopId());
		}
		if (form.getUserId() == null){
		    
			User user = null;
            try {
                user = getCurrentUser();
            } catch (Exception e) {
            }
            
			if (user != null) {
			    access.setUserId(user.getId());			
			}
		} else {
			access.setUserId(form.getUserId());
		}
		if (shopAccessService.save(access)>0){
			return new ResponseObject<Boolean>(true);
		} else {
			return new ResponseObject<Boolean>(false);
		}
	}

}

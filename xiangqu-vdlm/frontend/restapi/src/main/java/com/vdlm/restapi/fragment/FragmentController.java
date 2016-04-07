package com.vdlm.restapi.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.dal.model.Fragment;
import com.vdlm.dal.model.FragmentImage;
import com.vdlm.dal.model.ProductFragment;
import com.vdlm.dal.model.User;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.product.FragmentAndDescController;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.fragment.ProductFragmentService;
import com.vdlm.service.product.vo.ProductVO;

@Controller
public class FragmentController extends FragmentAndDescController {
	
	@Autowired
	private ProductFragmentService productFragmentService;
	
	@ResponseBody
	@RequestMapping("/fragment/list")
	public ResponseObject<List<FragmentVO>> listFragmentByShop(){
		List<FragmentVO> list = getFragmentByShop(getCurrentUser().getShopId());
		return new ResponseObject<List<FragmentVO>>(list);
	}

	@ResponseBody
	@RequestMapping("/fragment/{id}")
	public ResponseObject<FragmentVO> loadFragment(@PathVariable String id){
		FragmentVO vo = fragmentService.selectById(id);
		if(vo == null){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, id+"片段信息不存在");
		}
		
		setFragmentImage(vo);
		return new ResponseObject<FragmentVO>(vo);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/saveIdx")
	public ResponseObject<Boolean> saveFragmentIdx(String... ids){
		if(ids==null || ArrayUtils.isEmpty(ids)){
			log.warn("保存片段的时候，ids 不能为空");
			return new ResponseObject<Boolean>(false);
		}
		
		Set<Fragment> forms=new HashSet<Fragment>();
		String index="";
		int i=0;
		for(String s :ids){
			i++;
			if(StringUtils.isBlank(s)){
				index=index+i+",";
				continue;
			}
			
			Fragment f=new Fragment();
			f.setId(s);
			f.setIdx(i);
			forms.add(f);
		}

		if(forms.size()!=ids.length){
			log.warn("保存片段的时候，ids 存在无效空值,为空的有"+index);
			return new ResponseObject<Boolean>(false);
		}
		
		fragmentService.update(forms.toArray(new Fragment[forms.size()]));
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/save")
	public ResponseObject<FragmentVO> saveFragment(@Valid @ModelAttribute FragmentForm form, Errors errors){
		ControllerHelper.checkException(errors);
		User user = getCurrentUser();
		String shopId = user.getShopId();
		
		Fragment fragment = new Fragment();
		BeanUtils.copyProperties(form, fragment);
		fragment.setShopId(shopId);
		if(StringUtils.isBlank(form.getId())){
			fragmentService.insert(fragment);
		}else{
			fragmentService.update(fragment);
		}
		
		FragmentVO result = new FragmentVO();
		BeanUtils.copyProperties(fragment, result);
		
		fragmentImageService.deleteByFragmentId(fragment.getId());
		List<FragmentImageVO> fragmentImageList = new ArrayList<FragmentImageVO>();
		List<String> imgs = form.getImgs();
		for (int i = 0; i < imgs.size(); i++) {
			FragmentImage bean = new FragmentImage();
			bean.setImg(imgs.get(i));
			bean.setIdx(i);
			bean.setFragmentId(fragment.getId());
			fragmentImageService.insert(bean);
			
			FragmentImageVO imgVo = new FragmentImageVO();
			BeanUtils.copyProperties(bean, imgVo);
			String imgUrl = resourceFacade.resolveUrl(imgVo.getImg());
			imgVo.setImgUrl(imgUrl);
			fragmentImageList.add(imgVo);
		}
		result.setImgs(fragmentImageList);
		
		return new ResponseObject<FragmentVO>(result);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/saveShowModel")
	public ResponseObject<Boolean> saveShowModel(String id, boolean showModel){
		Fragment fragment = new Fragment();
		fragment.setId(id);
		fragment.setShowModel(showModel);
		fragmentService.update(fragment);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/delete")
	public ResponseObject<Boolean> deleteByFragmentId(String id){
		if (id == null)
			return new ResponseObject<Boolean>(false);
		productFragmentService.deleteByProductId(id);
		fragmentImageService.deleteByFragmentId(id);
		fragmentService.delete(id);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/move-before")
	public ResponseObject<Boolean> moveBeforeFragment(String srcId, String desId){
		fragmentService.moveBefore(srcId, desId);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/fragment/move-after")
	public ResponseObject<Boolean> moveAfterFragment(String srcId, String desId){
		fragmentService.moveAfter(srcId, desId);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/productFragment/move-before")
	public ResponseObject<Boolean> moveBeforeProductFragment(String srcId, String desId){
		productFragmentService.moveBefore(srcId, desId);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/productFragment/move-after")
	public ResponseObject<Boolean> moveAfterProductFragment(String srcId, String desId){
		productFragmentService.moveAfter(srcId, desId);
		return new ResponseObject<Boolean>(true);
	}
	
	@ResponseBody
	@RequestMapping("/productFagement/selectById")
	public ResponseObject<List<FragmentVO>> loadFagement(String productId){
		List<FragmentVO> list = getProductFragmentList(productId);
		return new ResponseObject<List<FragmentVO>>(list);
	}
	
	@ResponseBody
	@RequestMapping("/productFragment/save")
	public ResponseObject<Boolean> saveProductFragment(@Valid @ModelAttribute ProductFragmentForm form, Errors errors, Device device){
		ControllerHelper.checkException(errors);
		User user = fragmentService.getCurrentUser();
		String shopId = user.getShopId();
		ProductVO product = productService.load(form.getProductId());
		if(!shopId.equals(product.getShopId())){
			throw new BizException(GlobalErrorCode.UNAUTHORIZED, "当前用户与店铺不符");
		}
		
		if(device != null && device.isMobile()){
			log.warn("避免客户端传参异常，片段信息id为空的时候，不再修改片段信息");
			// 暂时规避客户端参数丢失导致详情被无意删除的bug  2015-07-09
			if (CollectionUtils.isEmpty(form.getFragmentIds()) ||
						StringUtils.isBlank(form.getFragmentIds().get(0)) || form.getFragmentIds().get(0).equalsIgnoreCase("null"))
				return new ResponseObject<Boolean>(true);
		}else{
			log.warn("浏览器执行修改片段代码");
		}
		productFragmentService.deleteByProductId(form.getProductId());
		if(CollectionUtils.isNotEmpty(form.getFragmentIds()) && StringUtils.isNotBlank(form.getFragmentIds().get(0))){
			for (int i = 0; i < form.getFragmentIds().size(); i++) {
				String fragmentId = form.getFragmentIds().get(i);
				if (fragmentId.equalsIgnoreCase("null")) continue;
				ProductFragment bean = new ProductFragment();
				bean.setProductId(form.getProductId());
				bean.setFragmentId(fragmentId);
				productFragmentService.insert(bean);
			}
		}
		return new ResponseObject<Boolean>(true);
	}
	
}

package com.vdlm.restapi.product;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.Image;
import com.vdlm.dal.model.ProductImage;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.dal.vo.FragmentImageVO;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.service.file.ImageService;
import com.vdlm.service.fragment.FragmentImageService;
import com.vdlm.service.fragment.FragmentService;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.product.vo.ProductVO;
import com.vdlm.service.shop.ShopService;

@Controller
public class FragmentAndDescController extends BaseController {

	@Autowired
	protected ProductService productService;
	@Autowired
	protected ShopService shopService;
	@Autowired
	protected ImageService imageService;
	@Autowired
	protected ResourceFacade resourceFacade;
	@Autowired
	protected FragmentService fragmentService;
	@Autowired
	protected FragmentImageService fragmentImageService;
	
	protected void setFragmentAndDescInfo(ProductVO product) {
		// 当店铺片段功能开启时，读取商品的片段信息
		Shop shop = shopService.load(product.getShopId());
		if(shop.getFragmentStatus()){
			List<FragmentVO> fragments = getProductFragmentList(product.getId());
			product.setFragments(fragments);
		}
	}
	
	/**
	 * 回填片段信息的图片信息
	 * @param fragments
	 */
	protected void setFragmentImage(FragmentVO... fragments) {
		for (FragmentVO vo:fragments) {
			List<FragmentImageVO> imgs;
			if (vo.getId() == null)  // 平台配置的公告 id==null,
				imgs = vo.getImgs();
			else
				imgs = fragmentImageService.selectByFragmentId(vo.getId());
			List<FragmentImageVO> showImgs=new ArrayList<FragmentImageVO>();
			if (imgs == null) continue;
			for (FragmentImageVO imgVo:imgs) {
				// 获取图片信息 小于一定大小的图片不再显示
				Image image=imageService.loadByImgKey(imgVo.getImg());
				if(image == null){
					continue;
				}
				
				if (image.getWidth() < 100 || image.getHeight() <100){
					continue;
				}
				
				imgVo.setImgUrl(resourceFacade.resolveUrl(imgVo.getImg()));
				showImgs.add(imgVo);
			}
			
			vo.setImgs(showImgs);
		}
	}

	/**
	 * 通过商品的id获取商品的片段信息
	 * @param productId
	 * @return
	 */
	protected List<FragmentVO> getProductFragmentList(String productId) {
		List<FragmentVO> fragments = fragmentService.selectByProductId(productId);
		setFragmentImage(fragments.toArray(new FragmentVO[fragments.size()]));
		return fragments;
	}
	
	/**
	 * 通过店铺获取片段信息
	 * @param shopId
	 * @return
	 */
	protected List<FragmentVO> getFragmentByShop(String shopId) {
		List<FragmentVO> list = fragmentService.selectByShopId(shopId);
		setFragmentImage(list.toArray(new FragmentVO[list.size()]));
		return list;
	}

	/**
	 * 获取主图和sku图和详情图
	 * @param productId
	 * @return
	 */
	protected void setMainImgs(ProductVO product) {
		List<String> imgsList = new ArrayList<String>();
		List<ProductImage> imgs = productService.requestImgs(String.valueOf(IdTypeHandler.decode(product.getId())), "");
		for(ProductImage img : imgs){
			imgsList.add(resourceFacade.resolveUrl(img.getImg() + "|" + ResourceFacade.IMAGE_S1));
		}
		
		product.setMainImgs(imgsList);
	}
}

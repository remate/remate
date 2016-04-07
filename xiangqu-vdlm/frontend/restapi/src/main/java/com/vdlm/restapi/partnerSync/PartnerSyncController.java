package com.vdlm.restapi.partnerSync;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.mapper.ProductImageMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.ProductImage;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.dal.vo.SyncActivityVO;
import com.vdlm.dal.vo.SyncProdVO;
import com.vdlm.dal.vo.SyncShopVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.fragment.FragmentImageService;
import com.vdlm.service.fragment.ProductFragmentService;
import com.vdlm.service.sync.PartnerSyncService;

@Controller
public class PartnerSyncController extends BaseController {
    
    @Autowired
    private PartnerSyncService partnerSyncService;
    
    @Autowired
    private ProductFragmentService productFragmentService;
    
    @Autowired
    private FragmentImageService fragmentImageService;
    
    @Autowired
    private ProdSyncMapper prodSyncMapper;
    
    @Autowired
    private ResourceFacade resourceFacade;
    
	@Autowired
	private ProductImageMapper productImageMapper;
    
    @Value("${site.web.host.name}")
    private String siteHost;
    
	private static final Integer IMAGELIMIT = 30; // 最大图片数量
    
	private List<SyncProdVO> generatorSyncProds(List<Product> prods ) {
		List<SyncProdVO> retList = new ArrayList<SyncProdVO>();
		if (prods == null || prods.size() == 0) return retList;
		for (Product p : prods ) {
			SyncProdVO item = new SyncProdVO();
			item.setShopId(p.getShopId());
			if (p.getDelayed() != null && p.getDelayed() != 0) {
				item.setIsDelay(true);
				item.setDelayDays(p.getDelayAt());
			} else if (p.getDelayAt() == null) {
				item.setIsDelay(false);
				item.setDelayDays(0);
			} else {
				item.setIsDelay(null);
				item.setDelayDays(null);
			}
			item.setAmount(p.getAmount());
			item.setProductId(p.getId());
			item.setTitle(p.getName());
			item.setImage(generatorProdSyncImg(p).toString());
			item.setArchive(p.getArchive());
			item.setStatus(ProductStatus.ONSALE.equals(p.getStatus()) ? 0 : 1);
			
			List<ProdSync> aList = prodSyncMapper.selectByPassedShopId(p.getShopId());
			if (aList != null && aList.size() > 0)
				item.setUrl(siteHost+"/p/"+p.getId()+ "?partner=xiangqu&union_id="+aList.get(0).getUnionId());
			item.setSellNum(p.getSales());
			item.setCreateTime(p.getCreatedAt());
			item.setPrice(p.getPrice());
			log.info(JSONObject.toJSONString(item));
			retList.add(item);
		}
		return retList;
	}
	
	private StringBuilder generatorProdSyncImg(Product p) {
		StringBuilder imgUrl = new StringBuilder();
		if (p == null) return imgUrl;
		
		List<ProductImage> imgs = productImageMapper.getImgs4Sync(p.getId());
		int cnt = 0;
		if (imgs != null && imgs.size() != 0) {
			for (ProductImage i : imgs) {
				String img  =  resourceFacade.resolveUrl2Orig(i.getImg());
				if (img == null || !img.startsWith("http"))  {
					log.warn("sync img[{}, {}] resolveUrl2Orig failed!!!", i.getImg(), i.getImgOrder());
					continue;
				}
				imgUrl.append(img);
				imgUrl.append(";");
				cnt++;
				if (cnt >= IMAGELIMIT) break;
			}
		}
		return imgUrl;
	}

	@ResponseBody
	@RequestMapping("/sync/shop/create")
	public ResponseObject<SyncShopVO> shopCreate(String partner, String shopId, HttpServletRequest req) {
		if (partner.isEmpty() || shopId.isEmpty()) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		SyncShopVO ret = partnerSyncService.shopCreate(partner, shopId);
		ret.setImage(resourceFacade.resolveUrl2Orig(ret.getImage()));
		log.debug(JSONObject.toJSONString(ret));
		return new ResponseObject<SyncShopVO>(ret);
	}
	
	@ResponseBody
	@RequestMapping("/sync/shop/update")
	public ResponseObject<SyncShopVO> shopUpdate(String partner, String shopId, HttpServletRequest req) {
		if (partner.isEmpty() || shopId.isEmpty()) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		SyncShopVO ret = partnerSyncService.shopUpdate(partner, shopId);
		ret.setImage(resourceFacade.resolveUrl2Orig(ret.getImage()));
		log.debug(JSONObject.toJSONString(ret));
		return new ResponseObject<SyncShopVO>(ret);
	}
	
	@ResponseBody
	@RequestMapping("/sync/product/create")
	public ResponseObject<List<SyncProdVO>> productCreate(String partner, String shopId, Integer page, HttpServletRequest req) {
		if (partner.isEmpty() || shopId.isEmpty() || page == null || page < 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		return new ResponseObject<List<SyncProdVO>>(generatorSyncProds(partnerSyncService.productCreate(partner, shopId, page)));
	}
	
	@ResponseBody
	@RequestMapping("/sync/product/update")
	public ResponseObject<List<SyncProdVO>> productUpdate(String partner, @RequestParam(value = "list") List<String> list, HttpServletRequest req) {
		if (partner.isEmpty() || list == null || list.size() == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		return new ResponseObject<List<SyncProdVO>>(generatorSyncProds(partnerSyncService.productUpdate(partner, list)));
	}
    
	@ResponseBody
	@RequestMapping("/sync/product/instock")
	public ResponseObject<List<SyncProdVO>> productInstock(String partner, @RequestParam(value = "list") List<String> prodList, HttpServletRequest req) {
		if (partner.isEmpty() || prodList == null || prodList.size() == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		//return new ResponseObject<SyncProdVO>(partnerSyncService.productUpdate(partner, prodList));
		return null;
	}
	
	@ResponseBody
	@RequestMapping("/sync/activity/start")
	public ResponseObject<List<SyncActivityVO>> activityStart(String partner, String actId,@RequestParam(value = "list") List<String> prodList, HttpServletRequest req) {
		if (partner.isEmpty() || prodList == null || prodList.size() == 0) {
			throw new BizException(GlobalErrorCode.THIRDPLANT_BUZERROR, "parameter error...");
		}
		return new ResponseObject<List<SyncActivityVO>>(partnerSyncService.activityStart(partner, actId, prodList));
	}
	
	@ResponseBody
	@RequestMapping("/sync/activity/stop")
	public ResponseObject<List<SyncActivityVO>> activityStop(String partner, @RequestParam(value = "list") List<String> prodList, HttpServletRequest req) {
		if (partner.isEmpty() || prodList == null || prodList.size() == 0) {
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "parameter error...");
		}
		return new ResponseObject<List<SyncActivityVO>>(partnerSyncService.activityStop(partner, prodList));
	}
	
}

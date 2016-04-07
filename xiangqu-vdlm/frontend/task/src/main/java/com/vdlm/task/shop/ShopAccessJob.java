package com.vdlm.task.shop;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.dal.model.AccessReport;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.dal.vo.ShopAccessEx;
import com.vdlm.service.shop.ShopAccessService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.utils.ImageUtils;

/**
 * 每天生成昨天的客户访问统计数据
 * @author huxaya
 *
 */
@Component
public class ShopAccessJob {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ShopAccessService shopAccessService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	@Value("${shop.access.bgImg.url}")
	String accessBgImgUrl;
	
	/**
	 * 定时生成客户访问统计UV
	 * 每天凌晨1点，生成昨天的客户访问统计数据
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void generateAccess() {
		
		long begin = System.currentTimeMillis();
		Long result;
		long end = System.currentTimeMillis();
		StringBuffer info = new StringBuffer();
		try {
			
			result = autoGenerateAccess();
			info.append("生成客户访问统计");
			info.append(result);
			info.append("条");
		} catch (Exception e) {
			info.append("生成客户访问统计失败");
			log.error("生成客户访问统计失败",e);
		}
		info.append(",花费：");
		info.append(end-begin);
		info.append("ms");
		log.info(info.toString());
	}
	
	/**
	 * 目前数量比较少，可以先用一个@Transactional
	 * @throws Exception 
	 */
	@Transactional
	private Long autoGenerateAccess() throws Exception {
		
		URL imgBgUrl = new URL(accessBgImgUrl);//resourceFacade.resolveUrl(accessBgImg));
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		if(shopAccessService.countReportByDate(date.getTime()) > 0)
			return 0l;
		
		Pageable pageable = new PageRequest(0, 5000); //每次统计一K家
		Long count = shopService.countByShop();
		List<Shop> shops = shopService.listAll(pageable);
		
		List<ShopAccessEx> accesses = null;
		for(Shop shop : shops){
			accesses = shopAccessService.findAccessUvByShopId(shop.getId(), date.getTime());
			generateImage(shop.getId(), date.getTime(), accesses, imgBgUrl);
		}
		return count;
	}
	
	private void generateImage(String shopId, Date date, List<ShopAccessEx> accesses, URL imgBgUrl) throws Exception{
		AccessReport accessReport = new AccessReport();
		Integer uv = 0;
		for(ShopAccessEx ex : accesses){
			uv += ex.getUv();
		}
		List<Integer> uvs = new ArrayList<Integer>();
		for(int i = 0;i < 24; i++){
			int u = 0;
			for(ShopAccessEx ex : accesses){
				if(i == ex.getHour()){
					u = ex.getUv();
					break;
				}
			}
			uvs.add(u);
		}
		
		if(uv == 0)
			return;
		
		BufferedImage image = ImageUtils.graphicsGeneration(uvs, imgBgUrl);
		InputStream in = ImageUtils.getImageStream(image);
		UpLoadFileVO vo = resourceFacade.uploadFileStream(in, FileBelong.STAT);
		accessReport = new AccessReport();
		accessReport.setImg(vo.getId());
		accessReport.setShopId(shopId);
		accessReport.setUv(uv);
		accessReport.setDate(date);
		shopAccessService.insertReport(accessReport);
	}
}

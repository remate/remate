package com.vdlm.bos.sync;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.bos.BaseController;
import com.vdlm.dal.mapper.ProdSyncMapper;
import com.vdlm.dal.model.ProdSync;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.dal.status.SyncAuditStatus;
import com.vdlm.dal.vo.ProdSyncVO;
import com.vdlm.service.product.ProductService;
import com.vdlm.service.shop.ShopService;
import com.vdlm.service.syncevent.SyncEventService;
import com.vdlm.service.user.UserService;

@Controller
public class SyncController extends BaseController {

	@Autowired
	private ProductService  productService;
	
	@Autowired
	private SyncEventService syncEventService;
	
	@Autowired
	private ProdSyncMapper  prodSyncMapper;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private UserService userService;
	
	@Value("${site.web.host.name}")
	private String domain;
	
	@RequestMapping(value = "sync")
	public String list(Model model, HttpServletRequest req) {
		return "sync/sync";
	}
	
	@ResponseBody
	@RequestMapping(value = "/fixSync")
	public Boolean fixSync(Integer event, Integer type, String shopId, String[] ids) {
		syncEventService.fixSync(event, type, shopId, Arrays.asList(ids));
		return true;
	}
	
	@ResponseBody
	@RequestMapping(value = "/fixSyncSimple")
	public Boolean fixSync(String[] ids) {
		syncEventService.fixSyncSimple(Arrays.asList(ids));
		return true;
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateSync")
	public Boolean startUpdateSync(String shopId, Boolean syncProd) {
		syncEventService.startUpdateSync(shopId, syncProd);
		return true;
	}
	
	@ResponseBody
	@RequestMapping(value = "sync/list")
	public Map<String, Object> list(SyncSearchForm form, Pageable pageable) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(form.getShopName())){
			params.put("shopName", form.getShopName());
		}
		if (form.getStatus() != null) {
			params.put("syncStatus", form.getStatus());
		}
		if(StringUtils.isNotBlank(form.getAuditSts())){
			params.put("auditSts", form.getAuditSts());
		}
		
		List<ProdSync> aList = null;
		List<ProdSyncVO> retList = new ArrayList<ProdSyncVO>();
		Long total = prodSyncMapper.countSyncShops(params);
		if(total.longValue()>0) {
			aList = prodSyncMapper.findByParmas(params, pageable);
			if (aList != null && aList.size() != 0) {
				for (ProdSync aps : aList) {
					ProdSyncVO aVo = new ProdSyncVO();
					BeanUtils.copyProperties(aps, aVo);
					aVo.setShopUrl(domain + "/shop/" + aps.getShopId());
					retList.add(aVo);
				}
			}
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", total);
		data.put("rows", retList);
		return data;
	}
	
	private String preper4Sync(String shopUrl, BigDecimal commissionRate) {
		if (StringUtils.isEmpty(shopUrl)) {
			return null;
		}
		
		String[] array = shopUrl.split("/");
		String shopId = array[array.length-1];
		Shop aShop = null;
		List<ProdSync> aList = null;
		try {
			aShop = shopService.load(shopId);
			aList = prodSyncMapper.selectByShopId(shopId, null);
			if (aShop != null) {
				// 先删除再插入新记录
				if (aList != null) {
					for (ProdSync ps : aList) {
						prodSyncMapper.delete(ps.getId());
					}
				}
				ProdSync aps = new ProdSync();
				aps.setShopId(shopId);
				aps.setName(aShop.getName());
				//aps.setSynced(false);
				aps.setSynced(true);
				aps.setAuditSts("PASS");
				aps.setCommissionRate(commissionRate);
				User aUser = userService.loadByLoginname("xiangqu");
				if (aUser != null) {
					aps.setUnionId(aUser.getId()); 
				}
				prodSyncMapper.insert(aps);
				return shopId;
			}
		} catch (Exception e) {
			log.debug(e.toString());
			return null;
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "sync/addProduct")
	public boolean startAdd(String id){
		ProdSync aps = prodSyncMapper.load(id);
		if (aps != null && aps.getAuditSts().equalsIgnoreCase(SyncAuditStatus.PASS.toString())) {
			syncEventService.startSpecifySync(aps.getShopId());
			return true;
		}
		return false;
	}
	
	@ResponseBody
	@RequestMapping(value = "sync/update")
	public boolean startUpdate(String[] ids) {
		for (String id : ids) {
			ProdSync aps = prodSyncMapper.load(id);
			if (aps != null && aps.getAuditSts().equalsIgnoreCase(SyncAuditStatus.PASS.toString())) {
				if (aps.getSynced())
					syncEventService.startUpdateSync(aps.getShopId(), true);
				else
					syncEventService.startSpecifySync(aps.getShopId());
			}
		}
		return true;
	}
	
	@RequestMapping(value = "sync/start")
	public String startSync(@RequestParam("shopUrl") String shopUrl ,  @RequestParam("commisionRate") String commisionRate, Model model) {
		Boolean ret = false;
		String shopId = null;
		if (StringUtils.isEmpty(shopUrl)) {
			model.addAttribute("errors", "店铺地址不能为空");
		} else {
			shopId = preper4Sync(shopUrl, new BigDecimal(commisionRate));
			if (shopId != null) {
				syncEventService.startSpecifySync(shopId);
				model.addAttribute("success","后台已开始同步,请稍后检查同步结果");
				ret = true;
			} else {
				model.addAttribute("errors","请确认是否为有效店铺地址");
			}
		}
		
		ProdSyncVO aVo = new ProdSyncVO();
		aVo.setShopUrl(shopUrl);
		aVo.setCommisionRate(commisionRate);
		model.addAttribute("syncItem", aVo);
		if (ret && shopId != null) {
			List<ProdSync> list = prodSyncMapper.selectByShopId(shopId, SyncAuditStatus.PASS.toString());
			aVo.setId(list.get(0).getId()); // 一个第三方平台只会有一个审核通过的店铺记录
			aVo.setCommissionRate(new BigDecimal(commisionRate));
			prodSyncMapper.update(aVo);
		}
		return "sync/start";
	}
	
	@RequestMapping(value = "sync/toStart")
	public String toSend(Model model) {
		model.addAttribute("syncItem", new ProdSyncVO() );
		return "sync/start";
	}
	
	@ResponseBody
	@RequestMapping(value = "sync/audit")
	public boolean startAudit(@RequestParam("ids") String[] ids, @RequestParam("auditResult") String auditResult, String auditNote) {
		Boolean ret = true;
		for (String id : ids) {
			ProdSync aps = prodSyncMapper.load(id);
			if ( SyncAuditStatus.PASS.toString().equalsIgnoreCase(aps.getAuditSts()) &&
				 SyncAuditStatus.DENY.toString().equals(auditResult)) {
				// 1. sendtomq type:1, event:3
				syncEventService.closeShop(aps.getShopId());
				
				// 2. del white list from prodsync
				prodSyncMapper.delete(id);
				continue;
			}
			if ( ! SyncAuditStatus.AUDITTING.toString().equalsIgnoreCase(aps.getAuditSts()))
				continue;
			
			ProdSync record = new ProdSync();
			record.setId(id);
			if (auditResult.equalsIgnoreCase(SyncAuditStatus.DENY.toString())) {
				if (StringUtils.isEmpty(auditNote)) {
					return false;
				} else {
					record.setAuditSts(SyncAuditStatus.DENY.toString());
					record.setAuditNote(auditNote);
					prodSyncMapper.update(record);
				}
			} else { // pass
				if (aps.getShopId() != null) {
					List<ProdSync> aList = prodSyncMapper.selectByPassedShopId(aps.getShopId());
					// 删除除本条外的其它已通过记录
					if (aList != null) {
						for (ProdSync ps : aList) {
							if (ps.getId() != id) prodSyncMapper.delete(ps.getId());
						}
					}
					record.setAuditSts(SyncAuditStatus.PASS.toString());
					prodSyncMapper.update(record);
					syncEventService.startSpecifySync(aps.getShopId());
				}
			}
			
		}
		return ret;
	}
}

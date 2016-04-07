package com.vdlm.web.address;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Address;
import com.vdlm.dal.model.User;
import com.vdlm.dal.model.Zone;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.user.UserService;
import com.vdlm.service.zone.ZoneService;
import com.vdlm.web.BaseController;
import com.vdlm.web.ResponseObject;

@Controller
public class AddressController extends BaseController {
    
    @Autowired
    private AddressService addressService;

    @Autowired
    private ZoneService zoneService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/addresses")
    @ResponseBody
    public ResponseObject<List<AddressVO>> list() {
        List<AddressVO> addresses = addressService.listUserAddressesVo();
        return new ResponseObject<List<AddressVO>>(addresses);
    }
    
    @RequestMapping("/address/add")
    public String add(HttpServletRequest req, Model model, @RequestHeader(value = "Referer", required = false)String referer) {
        
        String skuId = req.getParameter("skuId");
        String shopId = req.getParameter("shopId");
//        model.addAttribute("backUrl", backUrl);
        if (referer != null) {
            model.addAttribute("backUrl", referer);
        } else {
            String backUrl = "/cart/next?skuId=" + skuId;
            if(StringUtils.isBlank(skuId)){
                backUrl = "/cart/next?shopId=" + shopId;
            }
            model.addAttribute("backUrl", backUrl);
        }
        
		if ("xiangqu".equalsIgnoreCase(getCurrentUser().getPartner())){
			return "xiangqu/address";
		} else {
			return "cart/address";
		}
    }
    
    @RequestMapping("/address/{id}/edit")
    public String edit(@PathVariable("id") String id, HttpServletRequest req, Model model, @RequestHeader(value = "Referer", required = false)String referer) {
        Address address = addressService.load(id);
        model.addAttribute("address", address);
        
        List<Zone> parents = zoneService.listParents(address.getZoneId());
        // parents.add(zoneService.load(address.getZoneId()));
        
        Zone province = null;
        List<Zone> provinceList = null;
        if (parents.size() > 1) {
            province = parents.get(1);
            provinceList = zoneService.listSiblings(province.getId());
        } else {
            provinceList = zoneService.listChildren("1");
        }
        model.addAttribute("province", province);
        model.addAttribute("provinceList", provinceList);
        
        Zone city = null;
        List<Zone> cityList = null;
        if (parents.size() > 2) {
            city = parents.get(2);
            cityList = zoneService.listSiblings(city.getId());
        }
        model.addAttribute("city", city);
        model.addAttribute("cityList", cityList);
        
        Zone district = null;
        List<Zone> districtList = null;
        if (parents.size() > 3) {
            district = parents.get(3);
            districtList = zoneService.listSiblings(district.getId());
        }
        model.addAttribute("district", district);
        model.addAttribute("districtList", districtList);
        
        String skuId = req.getParameter("skuId");
        String shopId = req.getParameter("shopId");
        
        if (referer != null) {
            model.addAttribute("backUrl", referer);
        } else {
            String backUrl = "/cart/next?skuId=" + skuId;
            if(StringUtils.isBlank(skuId)){
                backUrl = "/cart/next?shopId=" + shopId;
            }
            model.addAttribute("backUrl", backUrl);
        }
        
        if ("xiangqu".equalsIgnoreCase(getCurrentUser().getPartner())){
			return "xiangqu/address";
		}else{
			return "cart/address";
		}
    }
    
//    @RequestMapping("/address/update")
//    @ResponseBody
//    public ResponseObject<Address> update(@ModelAttribute AddressForm form) {
//        Address address = new Address();
//        BeanUtils.copyProperties(form, address);
//        address = addressService.saveUserAddress(address);
//        return new ResponseObject<Address>(address);
//    }
    
//    @RequestMapping("/address/save")
//    @ResponseBody
//    public ResponseObject<AddressVO> save(@Valid @ModelAttribute AddressForm form) {
//       Address address = new Address();
//       BeanUtils.copyProperties(form, address);
//       address.setCommon(false);
//       return new ResponseObject<AddressVO>(addressService.saveUserAddress(address));
////        address.setConsignee(form.getConsignee());
////        address.setZoneId(form.getZoneId());
////        address.setStreet(form.getStreet());
////        address.setPhone(form.getPhone());
////        address.setZipcode(form.getZipcode());
////        address.setCommon(false);
////        address = addressService.saveUserAddress(address);
////        return new ResponseObject<Address>(address);
//    }
    
    
	@ResponseBody
	@RequestMapping("/address/save")
	public ResponseObject<Address> save(
			@ModelAttribute Address form,
			@RequestParam(value = "isDefault", required = false) String isDefault,
			@RequestParam(value = "id", required = false) String addressId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseObject<Address> msg = getErrorResposeMsg(form);
		if (msg != null) return msg;
		if (!StringUtils.isBlank(addressId)) {
			form.setId(addressId);
			if (!isUserAddress(user.getId(), addressId)) {
				return new ResponseObject<Address>(new BizException(GlobalErrorCode.UNAUTHORIZED, "非法操作"));
			}
		}
		Address address = addressService.saveUserAddress(form, user.getId());
		if (!StringUtils.isBlank(isDefault) && Boolean.valueOf(isDefault).booleanValue()) {
			addressService.asDefault(address.getId(), user.getId());
			address.setIsDefault(true);
		}
		return new ResponseObject<Address>(address);
	}
    
	@ResponseBody
	@RequestMapping("/address/list")
	public ResponseObject<List<AddressVO>> listByUserId() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<AddressVO> list = addressService.listUserAddressesVo2(user.getId());
		if (list == null || list.size() == 0) 
			return new ResponseObject<List<AddressVO>>(new ArrayList<AddressVO>());
		for (int i = 0, size = list.size(); i < size; i++) {
			if (i != 0 && list.get(i).getIsDefault() != null && list.get(i).getIsDefault()) {
				AddressVO addressVO = list.get(i);
				list.set(i, list.get(0));
				list.set(0, addressVO);
			}
		}
		return new ResponseObject<List<AddressVO>>(list); 
	}
	
	@ResponseBody
	@RequestMapping("/address/update")
	public ResponseObject<Address> update(
			@ModelAttribute AddressForm form,
			@RequestParam(value = "addressId") String addressId, 
			@RequestParam(value = "isDefault", required = false) String isDefault) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Address address = new Address();
		BeanUtils.copyProperties(form, address);
		address.setId(addressId);
		ResponseObject<Address> msg = getErrorResposeMsg(address);
		if (msg != null) return msg;
		if (!isUserAddress(user.getId(), addressId)) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.UNAUTHORIZED, "非法操作"));
		}
		address = addressService.saveUserAddress(address,user.getId());
		if (!StringUtils.isBlank(isDefault) && Boolean.valueOf(isDefault).booleanValue()) {
			addressService.asDefault(addressId,user.getId());
			address.setIsDefault(true);
		}
		return new ResponseObject<Address>();
	}
	
	@ResponseBody
	@RequestMapping("/address/delete")
	public ResponseObject<Boolean> delete(@RequestParam(value = "addressId")  String addressId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!isUserAddress(user.getId(), addressId)) {
			return new ResponseObject<Boolean>(new BizException(GlobalErrorCode.UNAUTHORIZED, "非法操作"));
		}
	    int flag = addressService.archiveAddress(addressId);
	    return new ResponseObject<Boolean>(flag > 0);
	}
	
	
	@ResponseBody
	@RequestMapping("/address/asDefault")
	public ResponseObject<Boolean> Default(@RequestParam(value = "addressId") String addressId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    return new ResponseObject<Boolean>( addressService.asDefault(addressId,user.getId()));
	}
	
	private ResponseObject<Address> getErrorResposeMsg(Address address) {
		if (StringUtils.isBlank(address.getConsignee())) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "收件人姓名不能为空"));
		} 
		if (StringUtils.isBlank(address.getZoneId())) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请选择正确的省市区"));
		}
		if (StringUtils.isBlank(address.getStreet())) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "详细地址不能为空"));
		}
		if (!strickMobileNO(address.getPhone())) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "电话号码无效"));
		}
		if (!strickZipCode(address.getZipcode())) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "请填写正确的邮政编码或不填写"));
		}
		if (address.getConsignee().length() > 20) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "收件人姓名长度不能超过20位"));
		}
		if (address.getStreet().length() > 100) {
			return new ResponseObject<Address>(new BizException(GlobalErrorCode.INVALID_ARGUMENT, "收货地址长度不能超过100位"));
		}
		return null;
	}
	
	private boolean strickMobileNO(String mobiles) {
		if (StringUtils.isBlank(mobiles) || mobiles.length() < 11)  return false;
		Pattern p = Pattern.compile("^1((3|4|5|7|8)\\d)\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	private Boolean strickZipCode(String zipcode) {
		if (StringUtils.isBlank(zipcode)) return true;
		String regexZipCode = "[0-9]{6}$";
		return Pattern.matches(regexZipCode, zipcode);
	}
	
	private boolean isUserAddress(String userId, String addressId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
			return false;
		}
		List<Address> addressList = addressService.listUserAddresses(userId);
		if (addressList == null || addressList.size() == 0) return false;
		for (int i = 0, size = addressList.size(); i < size; i++) {
			if (addressId.equals(addressList.get(i).getId())) {
				return true;
			}
		}
		return false;
	}
}

package com.vdlm.openapi.address;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Address;
import com.vdlm.dal.model.User;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.restapi.address.AddressForm;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;

@Controller
@RequestMapping(value = "/openapi")
public class AddressControllerApi extends BaseController {
	
	@Autowired
	private AddressService addressService;
	 
	private static boolean displayMobileNO(String mobiles) {
		if (StringUtils.isBlank(mobiles) || mobiles.length() < 11)  return false;
		// ^1((3|4|5|7|8)\\d)\\d{8}$
		Pattern p = Pattern.compile("^\\s*(\\+?(00)?86)?\\s*1((3|4|5|7|8)\\d)\\s*\\d{4}\\s*\\d{4}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	private static boolean strickMobileNO(String mobiles) {
		if (StringUtils.isBlank(mobiles) || mobiles.length() < 11)  return false;
		Pattern p = Pattern.compile("^1((3|4|5|7|8)\\d)\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 保存收货人的地址
	 * openapi/address/save?extUid=137070&zoneId=59&street=街道&consignee=收货地址&phone=15968118830&common=0
	 */
	@ResponseBody
	@RequestMapping("/address/save")
	public ResponseObject<Address> save(@ModelAttribute Address form,@RequestHeader("Domain") String domain, @RequestParam String extUid) {
		User user=loadExtUser(domain, extUid);
		//if ( !displayMobileNO(form.getPhone()) )
		if ( !strickMobileNO(form.getPhone()) ){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "电话号码无效");			
		}
		if (null == form.getStreet()){
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "详细地址为空");
		}
//		if (null != form.getZipcode() && !StringUtils.isNumeric(form.getZipcode())){
//			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "邮编无效");
//		}
		Address address = addressService.saveUserAddress(form, user.getId());
		return new ResponseObject<Address>(address);
	}
	
	

	/**
	 * 获取收货人地址
	 * openapi/address/list?extUid=137070
	 */
	@ResponseBody
	@RequestMapping("/address/list")
	public ResponseObject<List<AddressVO>> listByUserId(@RequestHeader("Domain") String domain, @RequestParam String extUid) {
		User user=loadExtUser(domain, extUid);
		List<AddressVO> list = addressService.listUserAddressesVo2(user.getId());
		return new ResponseObject<List<AddressVO>>(list); 
	}
	
	
	//===============================================
	@ResponseBody
	@RequestMapping("/address/{id}")
	public ResponseObject<Address> view(@PathVariable String id) {
		Address address = addressService.loadUserAddress(id);
		return new ResponseObject<Address>(address);
	}
	
	@ResponseBody
	@RequestMapping("/address/update")
	public ResponseObject<Address> update(@RequestHeader("Domain") String domain,@RequestParam String extUid,String addressId, @ModelAttribute AddressForm form) {
	    User user=loadExtUser(domain, extUid);
		Address address = new Address();
		BeanUtils.copyProperties(form, address);
		address.setId(addressId);
		if ( form.getPhone() != null  && ! strickMobileNO(form.getPhone()))
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "电话号码无效");
		address = addressService.saveUserAddress(address,user.getId());
		return new ResponseObject<Address>(address);
	}
	
	@ResponseBody
	@RequestMapping("/address/asDefault")
	public ResponseObject<Boolean>Default(@RequestHeader("Domain") String domain,@RequestParam String extUid, String addressId) {
	    User user=loadExtUser(domain, extUid);
	    return new ResponseObject<Boolean>( addressService.asDefault(addressId,user.getId()));
	}
	

	@ResponseBody
	@RequestMapping("/address/delete")
	public ResponseObject<Boolean> delete(@RequestParam(required=true)  String addressId) {
	    int flag = addressService.archiveAddress(addressId);
	    return new ResponseObject<Boolean>(flag > 0);
	}
	
	
	@ResponseBody
	@RequestMapping("/address/getDefault")
	public ResponseObject<Address>  getDefault(@RequestHeader("Domain") String domain,@RequestParam String extUid) {
	    User user=loadExtUser(domain, extUid);
	    return new ResponseObject<Address>( addressService.getDefault(user.getId()));
	}
	
	// 		Pattern p = Pattern.compile("(^\\+?(00)?\\s*86)?\\s*1((3|4|5|7|8)\\d)\\s*\\d{4}\\s*\\d{4}$");
	public static void main(String[] args) {
		String phos[] = {
								"139 5809 3763",
								"13958093763",
								"  	13958093763",
								"8613958093763",
								"  +8613958093763",
								"+8613958093763",
								"+86 13958093763",
								"+0086 13958093763",
								"+0086 139 58093763",
								"+0086 139 5809 3763",
								" +0086 139 5809 3763",
								"---------------------------",
								" ",
								"abcdefg",
								"++8613958093763",
								"861395809376",
								"+13958093763",
								"+086 139 58093763",
								"+00 139 5809 3763",
								"013958093763",
								"1395809376",
								"+ 86 1395809 3763",
								"+ 008613958093763",
								"   +  0086 139 5809 3763",
								"19958093763"
								};
		
		for (String  pho : phos) {
			System.out.println(pho + "\t" + displayMobileNO(pho));
			System.out.println(pho + "\t" + strickMobileNO(pho));
		}
	}
	
	
}

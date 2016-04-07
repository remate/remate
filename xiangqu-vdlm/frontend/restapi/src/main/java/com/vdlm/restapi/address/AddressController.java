package com.vdlm.restapi.address;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.Address;
import com.vdlm.dal.mybatis.IdTypeHandler;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.address.AddressService;
import com.vdlm.service.address.AddressVO;

@Controller
public class AddressController extends BaseController {
	
	protected static Logger log = LoggerFactory.getLogger("root");
	
	@Autowired
	private AddressService addressService;

	 /** 
     * @param args 
     * @throws IOException  
     * @throws ClientProtocolException  
     */  
    public static void main(String[] args) throws ClientProtocolException, IOException  
    {  
        // 创建HttpClient实例     
        HttpClient httpclient = new DefaultHttpClient();  
        // 创建Get方法实例     
        
        Map<String, String> map = new HashMap<String, String>();
        map.put(IdTypeHandler.encode(214722), "187302");
        map.put(IdTypeHandler.encode(213286), "137987");
        
        map.put(IdTypeHandler.encode(214723), "187302");
        map.put(IdTypeHandler.encode(213373), "137987");
        
        map.put(IdTypeHandler.encode(214724), "187302");
        map.put(IdTypeHandler.encode(214738), "187302");
        map.put(IdTypeHandler.encode(213942), "137987");
        map.put(IdTypeHandler.encode(213943), "137987");
        map.put(IdTypeHandler.encode(214739), "187302");
        map.put(IdTypeHandler.encode(214740), "187302");
        
        
        map.put(IdTypeHandler.encode(213995), "137987");
        map.put(IdTypeHandler.encode(213996), "137987");
        
        for(String m : map.keySet()){        
	        HttpGet httpgets = new HttpGet("http://localhost:8888/v2/openapi/order/viewTest?id="+m+"&extUid=" + map.get(m));
	        System.out.println("aaaaaaaaaaaaaaaaaa" + map.get(m) + " id="+m);
	        httpgets.setHeader("Domain", "xiangqu");
	//        HttpHeaders
	        HttpResponse response = httpclient.execute(httpgets);    
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {    
	            InputStream instreams = entity.getContent();    
	            String str = convertStreamToString(instreams);  
	            System.out.println("Do something");   
	            System.out.println("str=" + str);  
	            // Do not need the rest    
	            httpgets.abort();    
	        }  
        }
    }  
    
    public static String convertStreamToString(InputStream is) {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");      
            }      
        } catch (IOException e) {
        	log.error("convertStreamToString error",e);
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {
            	log.error("convertStreamToString error", e);
            }      
        }      
        return sb.toString();      
    }  
    
	@ResponseBody
	@RequestMapping("/address/save")
	public ResponseObject<Address> save(@ModelAttribute AddressForm form, Errors errors) {
		Address address = new Address();
		address.setId(form.getId());
		address.setConsignee(form.getConsignee());
		address.setZoneId(form.getZoneId());
		address.setStreet(form.getStreet());
		address.setPhone(form.getPhone());
		address.setZipcode(form.getZipcode());
		address.setCommon(false);
		address = addressService.saveUserAddress(address);
		return new ResponseObject<Address>(address);
	}
	
	@ResponseBody
	@RequestMapping("/address/list")
	public ResponseObject<List<AddressVO>> mine() {
		List<AddressVO> list = addressService.listUserAddressesVo();
		return new ResponseObject<List<AddressVO>>(list); 
	}
	
	@ResponseBody
	@RequestMapping("/address/{id}")
	public ResponseObject<Address> view(@PathVariable String id) {
		Address address = addressService.loadUserAddress(id);
		return new ResponseObject<Address>(address);
	}
	
	@ResponseBody
	@RequestMapping("/address/{id}/update")
	public ResponseObject<Address> update(@PathVariable String id, @ModelAttribute AddressForm form) {
		Address address = new Address();
		address.setId(id);
		BeanUtils.copyProperties(form, address);
		address = addressService.saveUserAddress(address);
		return new ResponseObject<Address>(address);
	}
	
	@ResponseBody
	@RequestMapping("/address/{id}/as-default")
	public ResponseObject<Boolean>Default(@PathVariable String id) {
		return new ResponseObject<Boolean>( addressService.asDefault(id));
	}
	
	@ResponseBody
	@RequestMapping("/address/{id}/delete")
	public ResponseObject<Boolean> delete(@PathVariable String id) {
	    int flag = addressService.archiveAddress(id);
	    return new ResponseObject<Boolean>(flag > 0);
	}
	
	@ResponseBody
	@RequestMapping("/address/getDefault")
	public ResponseObject<AddressVO>  getDefault() {
	    return new ResponseObject<AddressVO>(addressService.getDefault());
	}
}

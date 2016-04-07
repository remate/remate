package com.vdlm.restapi.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.dal.model.OrderItem;
import com.vdlm.dal.vo.Customer;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.order.OrderService;
import com.vdlm.service.order.vo.CustomerVO;

@Controller
public class CustomerController extends BaseController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ResourceFacade resourceFacade;

	@ResponseBody
	@RequestMapping("/customer/list")
	public ResponseObject<List<Customer>> listCustomer(Pageable pageable) {
		return new ResponseObject<List<Customer>>(orderService.listCustomers(pageable));
	}
	
	@ResponseBody
	@RequestMapping("/customer/listvip")
	public ResponseObject<List<Customer>> listCostomerByVip() {
		return new ResponseObject<List<Customer>>(orderService.listVipCustomers());
	}
	
	@ResponseBody
	@RequestMapping("/customer/listByKey")
	public ResponseObject<List<Customer>> listCustomer(String key, Pageable pageable) {
		return new ResponseObject<List<Customer>>(orderService.listCustomersByKey(key, pageable));
	}
	
	@ResponseBody
	@RequestMapping("/customer/updateVip")
	public ResponseObject<Boolean> updateVip(String name, String phone, Boolean vip) {
		return new ResponseObject<Boolean>(orderService.updateVip(name, phone, vip));
	}
	
	@ResponseBody
	@RequestMapping("/customer")
	public ResponseObject<CustomerVO> listOrders(String name, String phone) {
		CustomerVO customerVO = orderService.listOrdersByCustomer(name, phone);
		if (customerVO == null) {
			return new ResponseObject<CustomerVO>(new CustomerVO());
		}
		for (OrderVO order : customerVO.getOrders()) {
			String imgUrl = "";
			for (OrderItem item : order.getOrderItems()) {
				imgUrl = item.getProductImg();
				item.setProductImgUrl(imgUrl);
			}
			order.setImgUrl(imgUrl);
		}
		return new ResponseObject<CustomerVO>(customerVO);
	}
	
	@ResponseBody
	@RequestMapping("/customer/{buyerId}")
	public ResponseObject<CustomerVO> listOrdersByCustomer(@PathVariable("buyerId") String buyerId) {
		CustomerVO customerVO = orderService.listOrdersByCustomer(buyerId);
		for (OrderVO order : customerVO.getOrders()) {
			String imgUrl = "";
			for (OrderItem item : order.getOrderItems()) {
				imgUrl = item.getProductImg();
				item.setProductImgUrl(item.getProductImg());
			}
			order.setImgUrl(imgUrl);
		}
		return new ResponseObject<CustomerVO>(customerVO);
	}
}

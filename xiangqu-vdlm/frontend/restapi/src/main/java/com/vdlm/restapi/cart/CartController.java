package com.vdlm.restapi.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.CartItem;
import com.vdlm.dal.model.Shop;
import com.vdlm.dal.model.User;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.cart.CartService;
import com.vdlm.service.cart.vo.CartItemGroupVO;
import com.vdlm.service.cart.vo.CartItemVO;
import com.vdlm.service.pricing.CouponService;

@Controller
@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
public class CartController extends BaseController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CouponService couponService;


    /**
     * 购物车
     * @return
     */
    @RequestMapping("/cart")
    @ResponseBody
    public ResponseObject<List<CartItemGroupVO>> cart() {
        List<CartItemVO> cartItems = cartService.checkout();
        Map<Shop, List<CartItemVO>> cartItemMap = new LinkedHashMap<Shop, List<CartItemVO>>();
        for (CartItemVO item : cartItems) {
            List<CartItemVO> list = cartItemMap.get(item.getShop());
            if (list == null) {
                list = new ArrayList<CartItemVO>();
                cartItemMap.put(item.getShop(), list);
            }
            // TODO will do this in a proper way
            // 设置购物车中的图片大小为140宽度
            item.getProduct().setImgUrl(item.getProduct().getImg() + "|{w=140}");
            list.add(item);
        }
        
        List<CartItemGroupVO> result = new ArrayList<CartItemGroupVO>();
        for (Entry<Shop, List<CartItemVO>> entry : cartItemMap.entrySet()) {
            CartItemGroupVO vo = new CartItemGroupVO();
            vo.setShop(entry.getKey());
            vo.setCartItems(entry.getValue());
            result.add(vo);
        }
        return new ResponseObject<List<CartItemGroupVO>>(result);
    }

    /**
     * 商品加入购物车
     * @param skuId
     * @param amount
     * @return
     */
    @RequestMapping("/cart/add")
    @ResponseBody
    public ResponseObject<Integer> add(@RequestParam("skuId") String skuId, @RequestParam("amount") Integer amount) {
    	User user = this.getCurrentUser();
        cartService.addToCart(user.getId(), skuId, amount);
        Integer count = 0;
        List<CartItemVO> cartItems = cartService.checkout();
        for (CartItemVO cartItemVO : cartItems) {
            if (cartItemVO.getAmount() > 0) {
                count++;
            }
        }
        return new ResponseObject<Integer>(count);
    }

    /**
     * 购物车商品数量
     * @return
     */
    @RequestMapping("/cart/count")
    @ResponseBody
    public ResponseObject<Integer> count(@RequestHeader(value = "User-DeviceId", required = false) String deviceId) {
        // TODO 性能待改进
        Integer count = 0;
        count = cartService.count();
         
        
     	//FIXME 6.1活动发放优惠券，临时方法，用3天。没其他地方调用
        User user = getCurrentUser();
        couponService.autoGrantCoupon(user.getPartner(), user.getId(), deviceId);
//        if("xiangqu".equalsIgnoreCase(user.getPartner())){
//            couponService.grantCoupon("XQ.61", user.getId(), deviceId);
//        }
         
        /*
        List<CartItemVO> cartItems = cartService.checkout();
        for (CartItemVO cartItemVO : cartItems) {
            if (cartItemVO.getAmount() > 0) {
                count++;
            }
        }
      */
        return new ResponseObject<Integer>(count);
    }
    
    

    
    /**
     * 购物车商品数量更新
     * @return
     */
    @ResponseBody
    @RequestMapping("/cart/update")
    public ResponseObject<Boolean> update(@RequestParam("id") String id, @RequestParam("amount") Integer amount) {
        CartItem ci = cartService.saveOrUpdateCartItemAmount(id, amount);
        return new ResponseObject<Boolean>(ci != null);
    }

    @ResponseBody
    @RequestMapping("/cart/delete")
    public ResponseObject<Boolean> delete(@RequestParam("id") String id) {
        return new ResponseObject<Boolean>(cartService.remove(id));
    }
    
    @ResponseBody
    @RequestMapping("/cart/clear")
    public ResponseObject<Boolean> clear() {
        cartService.clear();
        return new ResponseObject<Boolean>(Boolean.TRUE);
    }

}

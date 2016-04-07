package com.vdlm.restapi;

import java.util.List;

import com.vdlm.dal.vo.ShopVO;
import com.vdlm.service.cart.vo.CartItemVO;

public class ShopCartItemVo {
	
	public ShopCartItemVo(ShopVO shop, List<CartItemVO> cartItems) {
		this.shop = shop;
		this.cartItems = cartItems;
	}

	private ShopVO shop;
	
	private List<CartItemVO> cartItems;

	public ShopVO getShop() {
		return shop;
	}

	public void setShop(ShopVO shop) {
		this.shop = shop;
	}

	public List<CartItemVO> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItemVO> cartItems) {
		this.cartItems = cartItems;
	}


}
